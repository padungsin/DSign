package com.openfog.condo.condounit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Unit implements Serializable {

	public enum LegalStatus {
		normal, legalExecution, undified
	}

	public enum LivingStatus {
		rented, stay, empty, undified
	}

	public enum FacilityPaymentStatus {
		paid, pending, overdue
	}

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
	private String floor;

	@Column
	private double area;
	
	@Column
	@Enumerated(EnumType.STRING)
	private LegalStatus legalStatus;

	@Column
	private Date firstOwnershipTransferDate;

	@Column
	@Enumerated(EnumType.STRING)
	private LivingStatus livingStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private FacilityPaymentStatus facilityPaymentStatus;


}
