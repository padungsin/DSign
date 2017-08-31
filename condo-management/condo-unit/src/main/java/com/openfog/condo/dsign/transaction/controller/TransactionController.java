package com.openfog.condo.dsign.transaction.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria;
import com.openfog.condo.dsign.transaction.model.Transaction;
import com.openfog.condo.dsign.transaction.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Transaction> list() {
		try {
			return transactionService.list();
		} catch (Exception e) {
			return new ArrayList<Transaction>();
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public Page<Transaction> search(@RequestBody TransactionCriteria criteria) {
		try {
			return transactionService.search(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/{transactionId}", method = RequestMethod.GET)
	public Transaction get(@PathVariable Long transactionId) {
		try {
			return transactionService.get(transactionId);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public Transaction save(@RequestBody Transaction transaction) {
		try {
			return transactionService.save(transaction);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/cancel/{transactionId}", method = RequestMethod.GET)
	public void voidTransaction(@PathVariable Long transactionId) {
		try {
			transactionService.cancelTransaction(transactionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	@RequestMapping(value = "/adjust", method = RequestMethod.PUT)
	public Transaction adjustTransaction(@RequestBody Transaction transaction) {
		try {
			return transactionService.adjustTransaction(transaction);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
