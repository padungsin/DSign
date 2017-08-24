package com.openfog.condo.condounit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Unit implements Serializable {
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
	private String roomNo;

	@Column
	private int floor;
	
	@Column
	private int floor2;

	@Column
	private double area;

	@Column
	private Date firstTransferDate;

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

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}


	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getFloor2() {
		return floor2;
	}

	public void setFloor2(int floor2) {
		this.floor2 = floor2;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public Date getFirstTransferDate() {
		return firstTransferDate;
	}

	public void setFirstTransferDate(Date firstTransferDate) {
		this.firstTransferDate = firstTransferDate;
	}
	

}
