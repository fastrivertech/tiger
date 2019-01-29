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
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_REFERENCE")
@SequenceGenerator(name = "PATIENT_REFERENCE_SEQ", sequenceName = "PATIENT_REFERENCE_SEQ", allocationSize = 1)
@XmlRootElement
public class PatientReference implements Serializable, ResourceComplexType {
	private static final long serialVersionUID = -8321293485415810061L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_REFERENCE_SEQ")
	@Basic(optional = false)
	@NotNull(message = "Identifier physical Id cannot be Null")
	@Column(name = "reference_id")
	private BigInteger referenceId;

	@JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
	@ManyToOne(optional = false)
	private Patient patient;

	@Size(max = 128)
	@Column(name = "path")
	private String path;

	@Size(max = 2048)
	@Column(name = "reference")
	private String reference;

//	@Lob
	@Size(max = 4096)
	@Column(name = "identifier")
	private String identifier;

	@Size(max = 2048)
	@Column(name = "display")
	private String display;

	public PatientReference() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public BigInteger getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(BigInteger referenceId) {
		this.referenceId = referenceId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}
