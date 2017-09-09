package com.openfog.condo.dsign.invoice.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.invoice.model.Invoice;
import com.openfog.condo.dsign.invoice.repository.InvoiceRepository;

@Service
@Transactional
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	public Invoice get(Long id) {
		return invoiceRepository.findOne(id);
	}

}
