package com.openfog.condo.dsign.payment.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.payment.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long>, PagingAndSortingRepository<Payment, Long> {

	public List<Payment> findAll();

	public List<Payment> findAllByInvoiceId(Long invoiceId);
	
	public Payment findByReceiptNo(String receiptNo);

}
