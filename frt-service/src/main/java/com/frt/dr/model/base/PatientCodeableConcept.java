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
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.frt.dr.model.ResourceComponent;

@Entity
@Table(name = "PATIENT_CODEABLECONCEPT")
@SequenceGenerator(name = "PATIENT_CODEABLECONCEPT_SEQ", sequenceName = "PATIENT_CODEABLECONCEPT_SEQ", allocationSize=1)
@XmlRootElement
public class PatientCodeableConcept implements Serializable, ResourceComponent {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_CODEABLECONCEPT_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Identifier logical Id cannot be Null")
    @Column(name = "codeableconcept_id")    
    private Long codeableconceptId;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;

    @Size(max = 128)    
    @Column(name = "path")                                            
    private String path;

    @Size(max = 1024)    
    @Column(name = "coding_system")                                        
    private String coding_system;

    @Size(max = 1024)    
    @Column(name = "coding_version")                                    
    private String coding_version;

    @Size(max = 1024)    
    @Column(name = "coding_code")                                
    private String coding_code;

    @Size(max = 4096)    
    @Column(name = "coding_display")                            
    private String coding_display;

    @Size(max = 1024)    
    @Column(name = "coding_userselected")                        
    private String coding_userselected;
    
    @Size(max = 2048)    
    @Column(name = "txt")                            
    private String txt;

    public PatientCodeableConcept() {    	
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

	public Long getCodeableconceptId() {
		return codeableconceptId;
	}

	public void setCodeableconceptId(Long codeableconceptId) {
		this.codeableconceptId = codeableconceptId;
	}

	public String getCoding_system() {
		return coding_system;
	}

	public void setCoding_system(String coding_system) {
		this.coding_system = coding_system;
	}

	public String getCoding_version() {
		return coding_version;
	}

	public void setCoding_version(String coding_version) {
		this.coding_version = coding_version;
	}

	public String getCoding_code() {
		return coding_code;
	}

	public void setCoding_code(String coding_code) {
		this.coding_code = coding_code;
	}

	public String getCoding_display() {
		return coding_display;
	}

	public void setCoding_display(String coding_display) {
		this.coding_display = coding_display;
	}

	public String getCoding_userselected() {
		return coding_userselected;
	}

	public void setCoding_userselected(String coding_userselected) {
		this.coding_userselected = coding_userselected;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

}
