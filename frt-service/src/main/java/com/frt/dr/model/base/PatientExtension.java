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
import com.frt.dr.model.Extension;

@Entity
@Table(name = "PATIENT_EXTENSION")
@SequenceGenerator(name = "PATIENT_EXTENSION_SEQ", sequenceName = "PATIENT_EXTENSION_SEQ", allocationSize=1)
public class PatientExtension extends Extension {
	private static final long serialVersionUID = -1L;
	 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_EXTENSION_SEQ")
    @Basic(optional = false)
    @NotNull(message = "patient extension logical Id cannot be Null")
    @Column(name = "patient_extension_id", nullable = false, updatable = true)    
	private String patientExtensionId;
	
//    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
//    @ManyToOne(optional = false)
//    private Patient patient;
	    
	@JoinColumn(name = "patient_id", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private Patient patient;

	public PatientExtension() { 	
    }
    
    
    public String getPatientExtensionId() {
    	return this.patientExtensionId;
    }
    
    public void getPatientExtensionId(String patientExtensionId) {
    	this.patientExtensionId = patientExtensionId;
    }
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }  
    
}
