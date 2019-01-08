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
import javax.xml.bind.annotation.XmlRootElement;

import com.frt.dr.model.ResourceComplexType;

/**********
 * Name Flags Card. Type Description & Constraintsdoco
 * ---------------------------------------------------------------------------------------------------------
 * ContactPoint ΣI Element Details of a Technology mediated contact point
 * (phone, fax, email, etc.) + A system is required if a value is provided.
 * Elements defined in Ancestors: id, extension system ΣI 0..1 code phone | fax
 * | email | pager | url | sms | other ContactPointSystem (Required) value Σ
 * 0..1 string The actual contact point details use ?!Σ 0..1 code home | work |
 * temp | old | mobile - purpose of this contact point ContactPointUse
 * (Required) rank Σ 0..1 positiveInt Specify preferred order of use (1 =
 * highest) period Σ 0..1 Period Time period when the contact point was/is in
 * use
 * 
 * @author JIMFUQIAN
 *
 */

@Entity
@Table(name = "PATIENT_CONTACTPOINT")
@SequenceGenerator(name = "PATIENT_CONTACTPOINT_SEQ", sequenceName = "PATIENT_CONTACTPOINT_SEQ", allocationSize = 1)
@XmlRootElement
public class PatientContactPoint implements Serializable, ResourceComplexType {
	private static final long serialVersionUID = -8321293485415816190L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_CONTACTPOINT_SEQ")
	@Basic(optional = false)
	@NotNull(message = "ContactPoint physical Id cannot be Null")
	@Column(name = "contactpoint_id")
	private BigInteger contactpointId;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
	@ManyToOne(optional = false)
	private Patient patient;

	@Size(max = 128)
	@Column(name = "path")
	private String path;

	@Size(max = 128)
	@Column(name = "system")
	private String system;

	@Size(max = 2048)
	@Column(name = "value")
	private String value;

	@Size(max = 32)
	@Column(name = "use")
	private String use;

	@Column(name = "rank")
	private Integer rank;

	@Lob
	@Column(name = "period")
	private String period;

	public PatientContactPoint() {
	}

	public BigInteger getContactpointId() {
		return contactpointId;
	}

	public void setContactpointId(BigInteger contactpointId) {
		this.contactpointId = contactpointId;
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

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
