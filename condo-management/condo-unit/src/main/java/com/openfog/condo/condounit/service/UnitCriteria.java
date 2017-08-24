package com.openfog.condo.condounit.service;

import java.io.Serializable;

import org.springframework.data.domain.Sort;

public class UnitCriteria implements Serializable{
	
	public enum SearchBy{no, roomno, floor}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchBy searchBy;
	private String searchText;
	
	private int itemPerPage;
	private int page;
	
	private Sort sort;

	public SearchBy getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(SearchBy searchBy) {
		this.searchBy = searchBy;
	}
	
	

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
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
