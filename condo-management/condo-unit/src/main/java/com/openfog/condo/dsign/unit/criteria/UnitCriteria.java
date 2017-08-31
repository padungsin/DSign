package com.openfog.condo.dsign.unit.criteria;

import com.openfog.condo.dsign.criteria.Criteria;

public class UnitCriteria extends Criteria {

	public enum SearchBy {
		no, roomNo, floor
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
