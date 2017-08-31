package com.openfog.condo.dsign.owner.criteria;

import com.openfog.condo.dsign.criteria.Criteria;

public class OwnerCriteria extends Criteria {

	public enum SearchBy {
		active, name, unit, unitActive
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
