package com.openfog.condo.dsign.watermeter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.criteria.Criteria;
import com.openfog.condo.dsign.invoice.model.Invoice;
import com.openfog.condo.dsign.invoice.model.Invoice.InvoiceStatus;
import com.openfog.condo.dsign.invoice.model.Invoice.InvoiceType;
import com.openfog.condo.dsign.invoice.model.InvoiceItem;
import com.openfog.condo.dsign.invoice.repository.InvoiceItemRepository;
import com.openfog.condo.dsign.invoice.repository.InvoiceRepository;
import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.owner.repository.OwnerRepository;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.repository.UnitRepository;
import com.openfog.condo.dsign.util.BahtText;
import com.openfog.condo.dsign.util.DateUtil;
import com.openfog.condo.dsign.util.ExcelUtil;
import com.openfog.condo.dsign.util.JasperUtil;
import com.openfog.condo.dsign.util.PDFMerger;
import com.openfog.condo.dsign.utility.model.RunningId;
import com.openfog.condo.dsign.utility.model.RunningId.IdType;
import com.openfog.condo.dsign.utility.repository.NumericConstantRepository;
import com.openfog.condo.dsign.utility.repository.RunningIdRepository;
import com.openfog.condo.dsign.utility.repository.TextConstantRepository;
import com.openfog.condo.dsign.watermeter.model.WaterMeter;
import com.openfog.condo.dsign.watermeter.model.WaterUsageHistory;
import com.openfog.condo.dsign.watermeter.repository.WaterMeterRepository;
import com.openfog.condo.dsign.watermeter.repository.WaterUsageHistoryRepository;

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
public class WaterMeterService {

	@Autowired
	private WaterMeterRepository waterMeterRepository;
	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private RunningIdRepository runningIdRepository;

	@Autowired
	private NumericConstantRepository numericConstantRepository;

	@Autowired
	private TextConstantRepository textConstantRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceItemRepository invoiceItemRepository;

	@Autowired
	private WaterUsageHistoryRepository waterUsageHistoryRepository;

	@Value("${condomgt.downloadform.template}")
	private String downloadFormFileName;

	@Value("${condomgt.downloadform.tempdir}")
	private String tempDir;

	@Value("${condomgt.invoice.water.jasper}")
	private String waterInvioceJasper;

	@Value("${condomgt.invoice.water.dir}")
	private String invoiceDir;

	private static int lastNoteDateRownum = 2;
	private static int lastNoteDateCellnum = 0;

	private static int currentNoteDateRownum = 2;
	private static int currentNoteDateCellnum = 4;

	private static int startRowNum = 5;
	private static int noCellnum = 0;
	private static int roomNoCellnum = 1;
	private static int meterNoCellnum = 2;
	private static int meterUnitCellnum = 3;
	private static int currentUnitCellnum = 4;

	private static String sheetName = "Water";

	public WaterMeter get(Long id) {
		return waterMeterRepository.findOne(id);
	}

	public WaterMeter getByNo(String no) {
		return waterMeterRepository.findByNo(no);
	}

	public List<WaterMeter> list() {
		return waterMeterRepository.findAll();
	}

	public Page<WaterMeter> list(Criteria criteria) throws Exception {

		return waterMeterRepository.findAll(createPageRequest(criteria));

	}

	public WaterMeter save(WaterMeter WaterMeter) {
		return waterMeterRepository.save(WaterMeter);
	}

	public List<WaterMeter> save(List<WaterMeter> WaterMeters) {

		for (WaterMeter WaterMeter : WaterMeters) {
			waterMeterRepository.save(WaterMeter);
		}
		return WaterMeters;
	}

	public File generateWaterForm() throws IOException, ParseException {

		FileInputStream excelFile = new FileInputStream(new File(downloadFormFileName));
		XSSFWorkbook templateWorkbook = new XSSFWorkbook(excelFile);
		XSSFWorkbook workbook = new XSSFWorkbook();

		ExcelUtil.copySheet(templateWorkbook, workbook, sheetName);

		XSSFSheet sheet = workbook.getSheet(sheetName);

		List<WaterMeter> waterMeterList = waterMeterRepository.findAll();
		ExcelUtil.createCellValue(sheet, lastNoteDateRownum, lastNoteDateCellnum, waterMeterList.get(0).getNoteDate());

		// all border style
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);

		int rownum = startRowNum;
		for (WaterMeter waterMeter : waterMeterList) {
			Unit unit = unitRepository.findOne(waterMeter.getUnitId());
			sheet.createRow(rownum);

			ExcelUtil.createCellValue(sheet, rownum, noCellnum, unit.getNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, roomNoCellnum, unit.getRoomNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, meterNoCellnum, waterMeter.getNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, meterUnitCellnum, waterMeter.getCurrentUnit(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, currentUnitCellnum, "", cellStyle);

			rownum++;

		}

		String outputFileName = "Water-" + DateUtil.formatForFileName(new Date()) + ".xlsx";

		// Write over to the new file
		FileOutputStream fileOut = new FileOutputStream(tempDir + "/" + outputFileName);
		workbook.write(fileOut);
		templateWorkbook.close();
		workbook.close();
		fileOut.close();

		return new File(tempDir + "/" + outputFileName);

	}

	public void importWaterForm(File waterUsageFile) throws Exception {

		FileInputStream excelFile = null;
		XSSFWorkbook workbook = null;
		try {
			excelFile = new FileInputStream(waterUsageFile);
			workbook = new XSSFWorkbook(excelFile);

			XSSFSheet sheet = workbook.getSheet(sheetName);

			Date noteDate = DateUtil.parseDate((String) ExcelUtil.getCellValue(sheet, currentNoteDateRownum, currentNoteDateCellnum), "dd/MM/yyyy");
			Date today = new Date();

			WaterMeter tempMeter = waterMeterRepository.findOne(1l);
			if (DateUtil.isSameMonth(noteDate, tempMeter.getNoteDate())) {
				throw new ImportTwiceException();

			}

			RunningId runningId = runningIdRepository.findByIdType(IdType.invoice);
			double pricePerUnit = numericConstantRepository.findByConstantKey("water.priceperunit").getValue();

			// Copy content
			for (int rowNumber = startRowNum; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				WaterMeter waterMeter = waterMeterRepository.findByNo((String) ExcelUtil.getCellValue(sheet, rowNumber, meterNoCellnum));
				WaterUsageHistory history = new WaterUsageHistory();
				history.setMeterId(waterMeter.getId());
				history.setPreviousNoteDate(waterMeter.getNoteDate());
				history.setNoteDate(noteDate);
				history.setPreviousUnit(waterMeter.getPreviousUnit());

				double currentUnit = (double) ExcelUtil.getCellValue(sheet, rowNumber, currentUnitCellnum);
				history.setCurrentUnit((int) currentUnit);
				history.setWaterUsage(history.getCurrentUnit() - history.getPreviousUnit());

				waterMeter.setPreviousUnit(history.getPreviousUnit());
				waterMeter.setCurrentUnit(history.getPreviousUnit());
				waterMeter.setPreviousNoteDate(waterMeter.getNoteDate());
				waterMeter.setNoteDate(noteDate);

				waterMeterRepository.save(waterMeter);
				waterUsageHistoryRepository.save(history);

				// generate invoice
				Invoice invoice = new Invoice();
				invoice.setInvoiceType(InvoiceType.water);
				invoice.setInvoiceStatus(InvoiceStatus.unpaid);
				invoice.setInvoiceNo(runningId.generate());
				invoice.setReferenceId(history.getId());
				invoice.setUnitId(waterMeter.getUnitId());
				invoice.setInvoiceDate(today);

				InvoiceItem invoiceItem = new InvoiceItem();
				
				String description = textConstantRepository.findByConstantKey("invoice.water.description.pattern").getValue();
				description = StringUtils.replace(description, "${from}", DateUtil.formatDate(history.getPreviousNoteDate(), "dd/MM/yyyy"));
				description = StringUtils.replace(description, "${unitFrom}", String.valueOf(history.getPreviousUnit()));
				description = StringUtils.replace(description, "${to}", DateUtil.formatDate(history.getNoteDate(), "dd/MM/yyyy"));
				description = StringUtils.replace(description, "${unitTo}", String.valueOf(history.getCurrentUnit()));
				
				
				invoiceItem.setDescription(description);
				invoiceItem.setUnitPrice(pricePerUnit);
				invoiceItem.setUnit(history.getWaterUsage());
				invoiceItem.setAmount(invoiceItem.getUnit() * invoiceItem.getUnitPrice());

				invoice.setTotalAmount(invoiceItem.getAmount());
				invoiceRepository.save(invoice);

				// generate pdf
				// if (invoice.getUnitId() == 66) {
				generateWaterPdf(invoice, invoiceItem, history);
				// }

				invoiceItem.setInvoiceId(invoice.getId());
				invoiceItemRepository.save(invoiceItem);

			}

		} catch (Exception e) {
			throw e;
		} finally {
			workbook.close();
			excelFile.close();
		}

	}

	private void generateWaterPdf(Invoice invoice, InvoiceItem invoiceItem, WaterUsageHistory history) throws JRException {

		// Compile jrxml file.
		JasperReport jasperReport = JasperCompileManager.compileReport(waterInvioceJasper);
		
		


		// Parameters for report
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("juristic.name.th", textConstantRepository.findByConstantKey("juristic.name.th").getValue());
		parameters.put("juristic.name.en", textConstantRepository.findByConstantKey("juristic.name.en").getValue());
		parameters.put("juristic.address.th", textConstantRepository.findByConstantKey("juristic.address.th").getValue());
		parameters.put("juristic.address.en", textConstantRepository.findByConstantKey("juristic.address.en").getValue());
		parameters.put("juristic.tel", textConstantRepository.findByConstantKey("juristic.tel").getValue());
		parameters.put("juristic.fax", textConstantRepository.findByConstantKey("juristic.fax").getValue());
		parameters.put("juristic.email", textConstantRepository.findByConstantKey("juristic.email").getValue());
		parameters.put("juristic.taxid", textConstantRepository.findByConstantKey("juristic.taxid").getValue());
		
		
		parameters.put("invoiceNo", invoice.getInvoiceNo());

		Unit unit = unitRepository.findOne(invoice.getUnitId());
		Owner owner = ownerRepository.findByUnitIdAndActive(unit.getId(), true);

		parameters.put("owner", owner.getName());
		parameters.put("no", unit.getNo());
		parameters.put("roomNo", unit.getRoomNo());
		parameters.put("lastNoteDate", DateUtil.formatThaiDate(history.getPreviousNoteDate(), "dd MMM yyyy"));
		parameters.put("lastUnit", JasperUtil.formatNumber(history.getPreviousUnit()));
		parameters.put("currentNoteDate", DateUtil.formatThaiDate(history.getNoteDate(), "dd MMM yyyy"));
		parameters.put("currentUnit", JasperUtil.formatNumber(history.getCurrentUnit()));
		parameters.put("usage", JasperUtil.formatNumber(invoiceItem.getUnit()));
		parameters.put("unitPrice", JasperUtil.formatCurrency(invoiceItem.getUnitPrice()));
		parameters.put("amount", JasperUtil.formatCurrency(invoice.getTotalAmount()));
		parameters.put("amountText", "-" + BahtText.getBahtText(BigDecimal.valueOf(invoice.getTotalAmount())) + "-");
		
		
		parameters.put("invoice.cashpayment.label", textConstantRepository.findByConstantKey("invoice.cashpayment.label").getValue());
		parameters.put("invoice.transferpayment.label", textConstantRepository.findByConstantKey("invoice.transferpayment.label").getValue());
		

		// DataSource
		// This is simple example, no database.
		// then using empty datasource.
		JRDataSource dataSource = new JREmptyDataSource();

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		invoice.setPdf(invoiceDir + "/" + invoice.getInvoiceNo() + "-water-" + DateUtil.formatDate(new Date(), "yyyy-mm") + "-" + unit.getRoomNo() + ".pdf");
		// Export to PDF.
		JasperExportManager.exportReportToPdfFile(jasperPrint, invoice.getPdf());

		System.out.println("Done!");
	}

	public File mergeWaterInvoice(String month) throws Exception {
		List<Invoice> invoices = invoiceRepository.findByInvoiceTypeAndMonth(InvoiceType.water, month);
		List<InputStream> inputPdfList = new ArrayList<InputStream>();

		for (Invoice invoice : invoices) {
			inputPdfList.add(new FileInputStream(invoice.getPdf()));
		}

		String mergeFileName = tempDir + "/" + "water-" + month + ".pdf";

		// Prepare output stream for merged pdf file.
		OutputStream outputStream = new FileOutputStream(mergeFileName);

		PDFMerger.mergePdfFiles(inputPdfList, outputStream);

		return new File(mergeFileName);

	}

	private Pageable createPageRequest(Criteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
