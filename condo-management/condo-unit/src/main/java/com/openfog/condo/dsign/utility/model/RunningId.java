package com.openfog.condo.dsign.utility.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;



@Entity
public class RunningId implements Serializable {

	public enum IdType {
		invoice, receipt
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	@Enumerated(EnumType.STRING)
	private IdType idType;

	@Column
	private int year;

	@Column
	private Long currentId;

	@Column
	private int digit;
	
	@Column
	private String prefix;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Long getCurrentId() {
		return currentId;
	}

	public void setCurrentId(Long currentId) {
		this.currentId = currentId;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String generate(){
		int currentYear =Calendar.getInstance().get(Calendar.YEAR);
		
		if(currentYear > year ){
			year = currentYear;
			currentId = 1l;
		}else{
			currentId++;
		}
		
		return prefix + "-" + year + "-" + StringUtils.leftPad(String.valueOf(currentId), digit, '0');
		
	}
	
}
