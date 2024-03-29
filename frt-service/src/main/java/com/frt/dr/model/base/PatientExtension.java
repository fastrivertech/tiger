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

import javax.persistence.Entity;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.frt.dr.model.DomainResource;
import com.frt.dr.model.Extension;

@Entity
@Table(name = "PATIENT_EXTENSION")
@SequenceGenerator(name = "PATIENT_EXTENSION_SEQ", sequenceName = "PATIENT_EXTENSION_SEQ", allocationSize=1)
@NamedQueries({
    @NamedQuery(name = "getPatientExtensionById", query = "SELECT PE FROM PatientExtension PE WHERE PE.patient.resourceId = :resourceId and PE.path = :path")
})
public class PatientExtension extends Extension {
	private static final long serialVersionUID = -1L;
	 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_EXTENSION_SEQ")
    @Basic(optional = false)
    @NotNull(message = "patient extension physical Id cannot be Null")
    @Column(name = "patient_extension_id", nullable = false, updatable = true)    
	private BigInteger patientExtensionId;
	
    @JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
    @ManyToOne(optional = false)
    private Patient patient;
	    
	public PatientExtension() { 	
    }
    
    public BigInteger getPatientExtensionId() {
    	return this.patientExtensionId;
    }
    
    public void setPatientExtensionId(BigInteger patientExtensionId) {
    	this.patientExtensionId = patientExtensionId;
    }
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }  
    
    @Override
    public <R extends DomainResource> void setResource(R resource) {
    	this.patient = (Patient)resource;
    }

    @Override
    public <R extends DomainResource> R getResource() {
    	return (R)this.patient;
    }
    
}
