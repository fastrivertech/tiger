/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.model.base;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.frt.dr.model.ResourceComplexType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "PATIENT_CONTACT")
@SequenceGenerator(name = "PATIENT_CONTACT_SEQ", sequenceName = "PATIENT_CONTACT_SEQ", allocationSize = 1)
@XmlRootElement
public class PatientContact implements Serializable, ResourceComplexType {
	private static final long serialVersionUID = -4321293485415818761L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_CONTACT_SEQ")
	@Basic(optional = false)
	@NotNull(message = "Contact physical Id cannot be Null")
	@Column(name = "contact_id")
	private BigInteger contactId;

	@JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
	@ManyToOne(optional = false)
	private Patient patient;

	@Size(max = 32)
	@Column(name = "gender")
	private String gender;

	//@Lob
	@Size(max = 256)
	@Column(name = "relationship")
	private String relationship;

	//@Lob
	@Size(max = 1024)
	@Column(name = "name")
	private String name;

	//@Lob
	@Size(max = 2048)
	@Column(name = "telecom")
	private String telecom;

	//@Lob
	@Size(max = 2048)
	@Column(name = "address")
	private String address;

	@Lob
	@Column(name = "period")
	private String period;

	@Lob
	@Column(name = "organization")
	private String organization;

  //private List<PatientExtension> extensions;
  //private List<PatientElementExtension> elementExtensions;

	public PatientContact() {
	}

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public BigInteger getContactId() {
		return contactId;
	}

	public void setContactId(BigInteger contactId) {
		this.contactId = contactId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelecom() {
		return telecom;
	}

	public void setTelecom(String telecom) {
		this.telecom = telecom;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public void setPath(String path) {
	}

	/**
	 * Return string representation for narrative 
	 */	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(this.getName())
		.append(",").append("\\n")
		.append("Relationship: ").append(this.getRelationship())
		.append(",").append("\\n")
		.append("Addr: ").append(this.getAddress())
		.append(",").append("\\n")
		.append("Telecom: ").append(this.getTelecom());
		return sb.toString();
	}
}
