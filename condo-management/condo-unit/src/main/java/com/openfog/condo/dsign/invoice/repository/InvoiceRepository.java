package com.openfog.condo.dsign.invoice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.openfog.condo.dsign.invoice.model.Invoice;
import com.openfog.condo.dsign.invoice.model.Invoice.InvoiceType;

public interface InvoiceRepository extends CrudRepository<Invoice, Long>, PagingAndSortingRepository<Invoice, Long> {

	public List<Invoice> findAll();

	public List<Invoice> findAllByUnitId(Long unitId);

	public Invoice findByInvoiceTypeAndReferenceId(InvoiceType invoiceType, Long referenceId);

	public Page<Invoice> findAll(Pageable pageable);
	
	
	@Query("Select i from Invoice i where i.invoiceType=:invoiceType and DATE_FORMAT(i.invoiceDate, '%Y-%m')=:invoiceMonth")
	public List<Invoice> findByInvoiceTypeAndMonth(@Param("invoiceType")InvoiceType invoiceType, @Param("invoiceMonth")String invoiceMonth);

}
