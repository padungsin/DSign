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
public class Ownership implements Serializable {

	public enum LegalStatus {
		normal, legalExecution, undified
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Unit unit;

	@Column
	private Owner owner;

	@Column
	@Enumerated(EnumType.STRING)
	private LegalStatus legalStatus;

	@Column
	private Date startDate;

}
