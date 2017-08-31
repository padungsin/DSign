package com.openfog.condo.dsign.criteria;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.data.domain.Sort;

public class Criteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> parameters = new HashMap<String, Object>();

	private int itemPerPage;
	private int page;

	private Sort sort;

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	public int getItemPerPage() {
		return itemPerPage;
	}

	public void setItemPerPage(int itemPerPage) {
		this.itemPerPage = itemPerPage;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

}
