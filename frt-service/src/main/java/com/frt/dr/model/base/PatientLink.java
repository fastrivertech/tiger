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
@Table(name = "PATIENT_LINK")
@SequenceGenerator(name = "PATIENT_LINK_SEQ", sequenceName = "PATIENT_LINK_SEQ", allocationSize=1)
@XmlRootElement
public class PatientLink implements Serializable, ResourceComplexType, BackboneElement {
    private static final long serialVersionUID = -8821293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_LINK_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Link logical Id cannot be Null")
    @Column(name = "link_id")    
    private Long linkId;
    
//  @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
//  @ManyToOne(optional = false)
//  private Patient patient;

    @JoinColumn(name = "patient_id", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private Patient patient;

    @Lob
    @Column(name = "other")                        
    private String other;
    
    @Size(max = 32)    
    @Column(name = "type")                                            
    private String type;

//    private List<PatientExtension> extensions;
//    
//    private List<PatientElementExtension> elementExtensions;
    
	public PatientLink() {    	
    }
    
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public Long getLinkId() {
		return linkId;
	}


	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}


	public String getOther() {
		return other;
	}


	public void setOther(String other) {
		this.other = other;
	}


	@Override
	public void setPath(String path) {
		// TODO Auto-generated method stub
		
	}

}
