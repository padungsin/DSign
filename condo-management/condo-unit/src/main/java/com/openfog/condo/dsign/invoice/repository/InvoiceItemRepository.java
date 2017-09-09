package com.openfog.condo.dsign.invoice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.invoice.model.InvoiceItem;

public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Long>, PagingAndSortingRepository<InvoiceItem, Long> {

	public List<InvoiceItem> findAllByInvoiceId(Long invoiceId);

}
