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
public class WaterUsageHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long meterId;

	@Column
	private int currentUnit;
	
	@Column
	private int previousUnit;
	
	@Column
	private int waterUsage;

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date noteDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMeterId() {
		return meterId;
	}

	public void setMeterId(Long meterId) {
		this.meterId = meterId;
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


	public int getWaterUsage() {
		return waterUsage;
	}

	public void setWaterUsage(int waterUsage) {
		this.waterUsage = waterUsage;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	
	
	

}
