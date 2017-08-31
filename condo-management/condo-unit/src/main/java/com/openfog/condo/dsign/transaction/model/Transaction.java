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
		expense, income
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
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Column
	@Enumerated(EnumType.STRING)
	private TransactionStatus transactionStatus;

	
	@Column
	private Long accountId;
	
	
	@Column
	private double balanceBefore;

	@Column
	private double balanceAfter;
	
	@Column
	private Long receiptId;


	
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
	
	

	public double getBalanceBefore() {
		return balanceBefore;
	}

	public void setBalanceBefore(double balanceBefore) {
		this.balanceBefore = balanceBefore;
	}

	public double getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(double balanceAfter) {
		this.balanceAfter = balanceAfter;
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

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
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
