package com.openfog.condo.dsign.payment.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.invoice.model.Invoice;
import com.openfog.condo.dsign.invoice.model.Invoice.InvoiceStatus;
import com.openfog.condo.dsign.invoice.model.Invoice.InvoiceType;
import com.openfog.condo.dsign.invoice.model.InvoiceItem;
import com.openfog.condo.dsign.invoice.repository.InvoiceItemRepository;
import com.openfog.condo.dsign.invoice.repository.InvoiceRepository;
import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.owner.repository.OwnerRepository;
import com.openfog.condo.dsign.payment.model.Payment;
import com.openfog.condo.dsign.payment.model.Payment.PaymentMethod;
import com.openfog.condo.dsign.payment.model.Payment.PaymentStatus;
import com.openfog.condo.dsign.payment.model.Payment.PaymentType;
import com.openfog.condo.dsign.payment.repository.PaymentRepository;
import com.openfog.condo.dsign.transaction.model.Transaction;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionStatus;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionType;
import com.openfog.condo.dsign.transaction.service.TransactionService;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.repository.UnitRepository;
import com.openfog.condo.dsign.util.BahtText;
import com.openfog.condo.dsign.util.DateUtil;
import com.openfog.condo.dsign.util.JasperUtil;
import com.openfog.condo.dsign.utility.model.NumericConstant;
import com.openfog.condo.dsign.utility.model.RunningId;
import com.openfog.condo.dsign.utility.model.RunningId.IdType;
import com.openfog.condo.dsign.utility.repository.NumericConstantRepository;
import com.openfog.condo.dsign.utility.repository.RunningIdRepository;
import com.openfog.condo.dsign.utility.repository.TextConstantRepository;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
@Transactional
public class PaymentService {

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private RunningIdRepository runningIdRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private InvoiceItemRepository invoiceItemRepository;

	@Autowired
	private TextConstantRepository textConstantRepository;

	@Autowired
	private NumericConstantRepository numericConstantRepository;

	@Autowired
	private TransactionService transactionService;

	@Value("${condomgt.receipt.jasper}")
	private String receiptJasper;

	@Value("${condomgt.payment.receipt.dir}")
	private String receiptDir;

	public Payment get(Long id) {
		return paymentRepository.findOne(id);
	}

	public Payment getByNo(String receiptNo) {
		return paymentRepository.findByReceiptNo(receiptNo);
	}

	public List<Payment> listByInvoiceNo(Long invoiceId) {
		return paymentRepository.findAllByInvoiceId(invoiceId);
	}

	public Payment pay(Payment payment) throws JRException {
		Invoice invoice = invoiceRepository.findOne(payment.getInvoiceId());
		RunningId runningId = runningIdRepository.findByIdType(IdType.receipt);

		payment.setReceiptNo(runningId.generate());
		payment.setPaymentStatus(PaymentStatus.normal);

		if (payment.getPaymentType().equals(PaymentType.full)) {
			payment.setAmount(invoice.getTotalAmount());
			invoice.setInvoiceStatus(InvoiceStatus.paid);
		} else {
			invoice.setPaidAmount(invoice.getPaidAmount() + payment.getAmount());
			if (invoice.getPaidAmount() + payment.getAmount() > invoice.getTotalAmount()) {
				invoice.setInvoiceStatus(InvoiceStatus.paid);
			} else {
				invoice.setInvoiceStatus(InvoiceStatus.partialPaid);
			}
		}

		if (invoice.getInvoiceType().equals(InvoiceType.facility) || invoice.getInvoiceType().equals(InvoiceType.fine) || invoice.getInvoiceType().equals(InvoiceType.water)) {
			Unit unit = unitRepository.findOne(invoice.getUnitId());
			Owner owner = ownerRepository.findByUnitIdAndActive(unit.getId(), true);
			String payerAddress = textConstantRepository.findByConstantKey("owner.address.pattern").getValue();
			payerAddress = StringUtils.replace(payerAddress, "${no}", unit.getNo());
			payerAddress = StringUtils.replace(payerAddress, "${roomNo}", unit.getRoomNo());

			payment.setPayerAddress(payerAddress);
			payment.setPayerName(owner.getName());

		}

		// generate receipt
		NumericConstant requireApprovalAmount = numericConstantRepository.findByConstantKey("receipt.approval.required.amount");
		boolean generateTemporary = false;
		if (payment.getAmount() >= requireApprovalAmount.getValue()) {
			generateTemporary = true;
		}

		if (payment.getPaymentType().equals(PaymentType.full)) {
			generatePdfFullPaymentReceipt(payment, invoice, false);
			if (generateTemporary) {
				generatePdfFullPaymentReceipt(payment, invoice, true);
			}

		} else {
			generatePdfPartialPaymentReceipt(payment, invoice, false);
			if (generateTemporary) {
				generatePdfPartialPaymentReceipt(payment, invoice, true);
			}

		}

		paymentRepository.save(payment);

		Transaction transaction = new Transaction();
		transaction.setAccountId(payment.getAccountId());
		transaction.setAmount(payment.getAmount());
		transaction.setTransactionType(TransactionType.income);
		transaction.setTransactionStatus(TransactionStatus.normal);
		transaction.setPaymentId(payment.getId());
		transaction.setDetail(getPaymentDescription(payment, invoice));

		invoice.setPaidAmount(invoice.getPaidAmount() + payment.getAmount());
		invoiceRepository.save(invoice);
		transactionService.save(transaction);

		return payment;

	}

	private void generatePdfFullPaymentReceipt(Payment payment, Invoice invoice, boolean generateTemporary) throws JRException {

		// Compile jrxml file.
		JasperReport jasperReport = JasperCompileManager.compileReport(receiptJasper);

		// Parameters for report
		Map<String, Object> parameters = new HashMap<String, Object>();
		mapHeadAndTailParameters(payment, invoice, parameters, generateTemporary);
		mapBodayParameters(payment, invoice, parameters);

		// DataSource
		// This is simple example, no database.
		// then using empty datasource.
		JRDataSource dataSource = new JREmptyDataSource();

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		if (generateTemporary) {
			payment.setTempReciept(receiptDir + "/temp/" + payment.getReceiptNo() + ".pdf");
			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, payment.getReciept());
		} else {
			payment.setReciept(receiptDir + "/" + payment.getReceiptNo() + ".pdf");
			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, payment.getReciept());
		}

	}

	private void generatePdfPartialPaymentReceipt(Payment payment, Invoice invoice, boolean generateTemporary) throws JRException {

		// Compile jrxml file.
		JasperReport jasperReport = JasperCompileManager.compileReport(receiptJasper);

		// Parameters for report
		Map<String, Object> parameters = new HashMap<String, Object>();
		mapHeadAndTailParameters(payment, invoice, parameters, generateTemporary);
		mapBodayParameters(payment, invoice, parameters);

		// DataSource
		// This is simple example, no database.
		// then using empty datasource.
		JRDataSource dataSource = new JREmptyDataSource();

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		if (generateTemporary) {
			payment.setReciept(receiptDir + "/temp/" + payment.getReceiptNo() + ".pdf");
			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, payment.getReciept());
		} else {
			payment.setTempReciept(receiptDir + "/" + payment.getReceiptNo() + ".pdf");
			// Export to PDF.
			JasperExportManager.exportReportToPdfFile(jasperPrint, payment.getReciept());
		}

	}

	private void mapHeadAndTailParameters(Payment payment, Invoice invoice, Map<String, Object> parameters, boolean temporary) throws JRException {

		// head

		parameters.put("juristic.name.th", textConstantRepository.findByConstantKey("juristic.name.th").getValue());
		parameters.put("juristic.name.en", textConstantRepository.findByConstantKey("juristic.name.en").getValue());
		parameters.put("juristic.address.th", textConstantRepository.findByConstantKey("juristic.address.th").getValue());
		parameters.put("juristic.address.en", textConstantRepository.findByConstantKey("juristic.address.en").getValue());
		parameters.put("juristic.tel", textConstantRepository.findByConstantKey("juristic.tel").getValue());
		parameters.put("juristic.fax", textConstantRepository.findByConstantKey("juristic.fax").getValue());
		parameters.put("juristic.email", textConstantRepository.findByConstantKey("juristic.email").getValue());
		parameters.put("juristic.taxid", textConstantRepository.findByConstantKey("juristic.taxid").getValue());

		String receiptTitle = "";
		if (temporary) {
			receiptTitle = textConstantRepository.findByConstantKey("receipt.temp.title").getValue();
		} else {
			receiptTitle = textConstantRepository.findByConstantKey("receipt.real.title").getValue();
		}
		parameters.put("receiptTitle", receiptTitle);

		parameters.put("receiptNo", payment.getReceiptNo());
		parameters.put("invoiceNo", invoice.getInvoiceNo());
		parameters.put("payer", payment.getPayerName());
		parameters.put("payerAddress", payment.getPayerAddress());

		parameters.put("date", DateUtil.formatThaiDate(payment.getPaymentDate(), "dd/MM/yyyy"));

		// tail
		String payByCash = "[  ]";
		if (payment.getPaymentMethod().equals(PaymentMethod.cash)) {
			payByCash = "[x]";
		}

		String payByCheque = "[  ]";
		String chequeBankName = "";
		String chequeNo = "";
		String chequeDate = "";
		if (payment.getPaymentMethod().equals(PaymentMethod.cheque)) {
			payByCheque = "[x]";
			chequeBankName = payment.getChequeBankName();
			chequeNo = payment.getChequeNo();
			chequeDate = DateUtil.formatThaiDate(payment.getChequeDate(), "dd/MM/yyyy");
		}

		String transfer = "[  ]";
		String transferBankName = "";
		String transferDate = "";
		if (payment.getPaymentMethod().equals(PaymentMethod.bankTransfer)) {
			transfer = "[x]";
			transferBankName = payment.getTransferBankName();
			transferDate = DateUtil.formatDate(payment.getTransferDate(), "dd/MM/yyyy");
		}

		parameters.put("payByCash", payByCash);
		parameters.put("payByCheque", payByCheque);
		parameters.put("chequeBankName", chequeBankName);
		parameters.put("chequeNo", chequeNo);
		parameters.put("chequeDate", chequeDate);
		parameters.put("transfer", transfer);
		parameters.put("transferBankName", transferBankName);
		parameters.put("transferDate", transferDate);

		// generate receipt
		NumericConstant requireApprovalAmount = numericConstantRepository.findByConstantKey("receipt.approval.required.amount");
		boolean approvalRequired = false;
		if (payment.getAmount() >= requireApprovalAmount.getValue()) {
			approvalRequired = true;
		}

		String autorizedPersonLine = "";
		String autorizedPerson = "";
		if (approvalRequired && !temporary) {
			autorizedPersonLine = "_______________________";
			autorizedPerson = textConstantRepository.findByConstantKey("receipt.real.authorizedperson").getValue();
		}
		parameters.put("autorizedPersonLine", autorizedPersonLine);
		parameters.put("receipt.real.authorizedperson", autorizedPerson);

		String receiptTailTh = "";
		String receiptTailEn = "";
		if (approvalRequired && !temporary) {
			receiptTailTh = textConstantRepository.findByConstantKey("receipt.real.tail.th").getValue();
			receiptTailEn = textConstantRepository.findByConstantKey("receipt.real.tail.en").getValue();
		}
		parameters.put("receipt.real.tail.th", receiptTailTh);
		parameters.put("receipt.real.tail.en", receiptTailEn);

	}

	private void mapBodayParameters(Payment payment, Invoice invoice, Map<String, Object> parameters) throws JRException {
		List<InvoiceItem> invoiceItems = invoiceItemRepository.findAllByInvoiceId(invoice.getId());

		int index = 1;
		if (payment.getPaymentType().equals(PaymentType.full)) {
			for (int i = 0; i < invoiceItems.size(); i++) {
				InvoiceItem invoiceItem = invoiceItems.get(i);

				parameters.put("no" + index, String.valueOf(index));
				parameters.put("detail" + index, invoiceItem.getDescription());
				parameters.put("unitPrice" + index, JasperUtil.formatCurrency(invoiceItem.getUnitPrice()));
				parameters.put("qty" + index, JasperUtil.formatCurrency(invoiceItem.getUnit()));
				parameters.put("amount" + index, JasperUtil.formatCurrency(invoiceItem.getAmount()));

				index++;

			}

		} else {

			parameters.put("no" + index, String.valueOf(index));
			parameters.put("detail" + index, getPaymentDescription(payment, invoice));
			parameters.put("unitPrice" + index, "");
			parameters.put("qty" + index, "");
			parameters.put("amount" + index, JasperUtil.formatCurrency(payment.getAmount()));

			index++;

		}

		for (int i = index; i <= 8; i++) {

			parameters.put("no" + i, "");
			parameters.put("detail" + i, "");
			parameters.put("unitPrice" + i, "");
			parameters.put("qty" + i, "");
			parameters.put("amount" + i, "");
		}

		parameters.put("totalAmount", JasperUtil.formatCurrency(payment.getAmount()));
		parameters.put("totalAmountText", "-" + BahtText.getBahtText(BigDecimal.valueOf(payment.getAmount())) + "-");

	}

	private String getPaymentDescription(Payment payment, Invoice invoice) {
		List<InvoiceItem> invoiceItems = invoiceItemRepository.findAllByInvoiceId(invoice.getId());
		double leftAmount = invoice.getTotalAmount() - invoice.getPaidAmount();
		if (invoiceItems.size() == 1) {
			InvoiceItem invoiceItem = invoiceItems.get(0);
			if (payment.getPaymentType().equals(PaymentType.full)) {
				return invoiceItem.getDescription();
			}
			return invoiceItem.getDescription() + " [" + JasperUtil.formatCurrency(payment.getAmount()) + "/" + JasperUtil.formatCurrency(leftAmount) + "(" + JasperUtil.formatCurrency(invoice.getTotalAmount()) + ")" + "]";
		}
		if (payment.getPaymentType().equals(PaymentType.full)) {
			return invoice.getInvoiceNo();
		}

		return invoice.getInvoiceNo() + " [" + JasperUtil.formatCurrency(payment.getAmount()) + "/" + JasperUtil.formatCurrency(leftAmount) + "(" + JasperUtil.formatCurrency(invoice.getTotalAmount()) + ")" + "]";

	}



}
