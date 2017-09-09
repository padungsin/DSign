package com.openfog.condo.dsign.transaction.model;

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
public class Transaction implements Serializable {

	public enum TransactionType {
		expense, income, holdingTax
	}

	public enum TransactionStatus {
		normal, voidTransaction, voidTransactionRef
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date transactionDate;

	@Column
	private String detail;

	
	@Column
	private double amount;
	
	
	@Column
	private double fullAmount;
	
	
	@Column
	private double holdingTaxtAmount;
	
	@Column
	private boolean holdingTax;
	
	

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date revenueDepartmentDate;


	@Column
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Column
	@Enumerated(EnumType.STRING)
	private TransactionStatus transactionStatus;

	
	@Column
	private Long accountId;

	
	@Column
	private Long paymentId;

	@Column
	private Long referenceTransaction;
	
	@Column
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getFullAmount() {
		return fullAmount;
	}

	public void setFullAmount(double fullAmount) {
		this.fullAmount = fullAmount;
	}

	public double getHoldingTaxtAmount() {
		return holdingTaxtAmount;
	}

	public void setHoldingTaxtAmount(double holdingTaxtAmount) {
		this.holdingTaxtAmount = holdingTaxtAmount;
	}

	public boolean isHoldingTax() {
		return holdingTax;
	}
	
	public boolean getHoldingTax() {
		return holdingTax;
	}

	public void setHoldingTax(boolean holdingTax) {
		this.holdingTax = holdingTax;
	}

	public Date getRevenueDepartmentDate() {
		return revenueDepartmentDate;
	}

	public void setRevenueDepartmentDate(Date revenueDepartmentDate) {
		this.revenueDepartmentDate = revenueDepartmentDate;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}



	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Long getReferenceTransaction() {
		return referenceTransaction;
	}

	public void setReferenceTransaction(Long referenceTransaction) {
		this.referenceTransaction = referenceTransaction;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
