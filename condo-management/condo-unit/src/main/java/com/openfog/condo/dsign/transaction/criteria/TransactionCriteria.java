package com.openfog.condo.dsign.transaction.criteria;

import com.openfog.condo.dsign.criteria.Criteria;

public class TransactionCriteria extends Criteria {

	public enum SearchBy {
		date, month, year, dateAndtype, monthAndType, yearAndType
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchBy searchBy;

	public SearchBy getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(SearchBy searchBy) {
		this.searchBy = searchBy;
	}

}
