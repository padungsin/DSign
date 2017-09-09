package com.openfog.condo.dsign.watermeter.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class WaterMeter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String no;

	@Column
	private int currentUnit;
	
	@Column
	private int previousUnit;
	
	@Column
	private Long unitId;

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date noteDate;

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date previousNoteDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public int getCurrentUnit() {
		return currentUnit;
	}

	public void setCurrentUnit(int currentUnit) {
		this.currentUnit = currentUnit;
	}

	public int getPreviousUnit() {
		return previousUnit;
	}

	public void setPreviousUnit(int previousUnit) {
		this.previousUnit = previousUnit;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	public Date getPreviousNoteDate() {
		return previousNoteDate;
	}

	public void setPreviousNoteDate(Date previousNoteDate) {
		this.previousNoteDate = previousNoteDate;
	}

	

}
