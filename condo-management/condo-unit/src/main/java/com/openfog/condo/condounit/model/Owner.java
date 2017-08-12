package com.openfog.condo.condounit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Owner implements Serializable {

	public enum ContactStatus {
		canContact, cannotContact
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;

	@Column
	private String contactAddress1;

	@Column
	private String contactAddress2;

	@Column
	private String contactAddress3;

	@Column
	private String phone;

	@Column
	private String email;

	@Column
	@Enumerated(EnumType.STRING)
	private ContactStatus contactStatus;

}
