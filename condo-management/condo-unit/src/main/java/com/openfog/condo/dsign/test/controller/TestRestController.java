package com.openfog.condo.dsign.test.controller;

import java.io.File;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria;
import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria.SearchBy;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionType;

@RestController
@RequestMapping("/test")
public class TestRestController {

	@RequestMapping(value = "/testHashMap", method = RequestMethod.GET)
	public TransactionCriteria testHashMap() {
		TransactionCriteria criteria = new TransactionCriteria();
		criteria.setSearchBy(SearchBy.dateAndtype);
		criteria.getParameters().put("transactionDate", "2017-08-28");
		criteria.getParameters().put("transactionType", TransactionType.income);

		return criteria;
	}
	

	

}
