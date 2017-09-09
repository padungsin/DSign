package com.openfog.condo.dsign.payment.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Payment implements Serializable {
	public enum PaymentStatus {
		normal, cancelled
	}

	public enum PaymentType {
		full, partial
	}

	public enum PaymentMethod {
		cash, bankTransfer, cheque
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	
	@Column
	private String receiptNo;

	@Column
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Column
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column
	private String chequeBankName;

	@Column
	private String chequeNo;

	@Column
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date chequeDate;

	@Column
	private String transferBankName;

	@Column
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date transferDate;

	@Column
	private Long invoiceId;

	@Column
	private double amount;
	
	@Column
	private String reciept;
	
	@Column
	private String tempReciept;
	
	@Column
	private Long accountId;

	@Column
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date paymentDate;

	@Column
	private Long payerId;

	@Column
	private String payerName;

	@Column
	private String payerAddress;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}


	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}

	public String getTempReciept() {
		return tempReciept;
	}

	public void setTempReciept(String tempReciept) {
		this.tempReciept = tempReciept;
	}



	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getChequeBankName() {
		return chequeBankName;
	}

	public void setChequeBankName(String chequeBankName) {
		this.chequeBankName = chequeBankName;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getTransferBankName() {
		return transferBankName;
	}

	public void setTransferBankName(String transferBankName) {
		this.transferBankName = transferBankName;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public Long getPayerId() {
		return payerId;
	}

	public void setPayerId(Long payerId) {
		this.payerId = payerId;
	}

	public String getPayerAddress() {
		return payerAddress;
	}

	public void setPayerAddress(String payerAddress) {
		this.payerAddress = payerAddress;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
	

}
