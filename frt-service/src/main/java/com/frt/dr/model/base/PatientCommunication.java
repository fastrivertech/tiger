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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.frt.dr.model.BackboneElement;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_COMMUNICATION")
@SequenceGenerator(name = "PATIENT_COMMUNICATION_SEQ", sequenceName = "PATIENT_COMMUNICATION_SEQ", allocationSize=1)
@XmlRootElement
public class PatientCommunication implements Serializable, ResourceComplexType, BackboneElement {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_COMMUNICATION_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Communication logical Id cannot be Null")
    @Column(name = "communication_id")    
    private Long communicationId;
    
	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;

    @Column(name = "preferred")                                            
    private Boolean preferred;

    @Lob
    @Column(name = "language")                        
    private String language;
    
//    private List<PatientExtension> extensions;
//    
//    private List<PatientElementExtension> elementExtensions;
    
	public PatientCommunication() {    	
    }
    
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

	public Long getCommunicationId() {
		return communicationId;
	}


	public void setCommunicationId(Long communicationId) {
		this.communicationId = communicationId;
	}


	public Boolean getPreferred() {
		return preferred;
	}


	public void setPreferred(Boolean preferred) {
		this.preferred = preferred;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}

}