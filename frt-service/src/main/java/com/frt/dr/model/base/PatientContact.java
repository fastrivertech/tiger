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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_CONTACT")
@SequenceGenerator(name = "PATIENT_CONTACT_SEQ", sequenceName = "PATIENT_CONTACT_SEQ", allocationSize=1)
@XmlRootElement
public class PatientContact implements Serializable, ResourceComplexType {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_CONTACT_SEQ")
    @Basic(optional = false)
    @NotNull(message = "Contact logical Id cannot be Null")
    @Column(name = "contact_id")    
    private Long contactId;
        
    @Size(max = 32)    
    @Column(name = "gender")                                                
    private String gender;

    @Lob
    @Column(name = "relationship")                                                        
    private String relationship;
    
    @Lob
    @Column(name = "name")                                                        
    private String name;
    
    @Lob
    @Column(name = "telecom")                                                        
    private String telecom;

    @Lob
    @Column(name = "address")                                                        
    private String address;
    
    @Lob
    @Column(name = "period")                                                        
    private String period;
    
    @Lob
    @Column(name = "organization")                                                        
    private String organization;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;
 
    /*
    private List<PatientExtension> extensions;
    
    private List<PatientElementExtension> elementExtensions;
    */
    
    public PatientContact(){    	
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

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
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

}