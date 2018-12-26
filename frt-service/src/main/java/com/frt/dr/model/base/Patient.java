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

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import com.frt.dr.model.DomainResource;

@Entity
@Table(name = "PATIENT")
@SequenceGenerator(name = "PATIENT_SEQ", sequenceName = "PATIENT_SEQ", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "getPatientById", query = "SELECT P FROM Patient P WHERE P.patientId = :patientId")
})
public class Patient extends DomainResource implements Serializable {
    private static final long serialVersionUID = -8321293485415818761L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Patient logical Id cannot be Null")
    @Column(name = "patient_id", updatable=false)
    private Long patientId;
    
    @NotNull(message = "Domain resource logical Id cannot be Null")
    @Column(name = "domain_resource_id")    
    private Long domainResourceId;

    @NotNull(message = "Active cannot be Null")
    @Column(name = "active")        
    private Boolean active;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("humannameId ASC")
    private List<PatientHumanName> names;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("identifierId ASC")
    private List<PatientIdentifier> identifiers;

    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("addressId ASC")
    private List<PatientAddress> addresses;

    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("contactId ASC")
    private List<PatientContact> contacts;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("attachmentId ASC")
    private List<PatientAttachment> photos;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("contactpointId ASC")
    private List<PatientContactPoint> telecoms;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private PatientCodeableConcept maritalStatus;

    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private PatientReference managingOrganization;

    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private PatientAnimal animal;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("communicationId ASC")
    private List<PatientCommunication> communications;

	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @OrderBy("linkId ASC")
    private List<PatientLink> links;

	@Size(max = 32)
    @Column(name = "gender")            
    private String gender;

    @Column(name = "birthDate")                
    private Date birthDate;

    @Column(name = "deceasedBoolean")                    
    private Boolean deceasedBoolean;

	@Column(name = "deceasedDateTime")                        
    private Timestamp deceasedDateTime;
    
	@Column(name = "multipleBirthBoolean")                        
    private Boolean multipleBirthBoolean;
    
	@Column(name = "multipleBirthInteger")                        
    private Integer multipleBirthInteger;
    
	/*
	private List<PatientExtension> extensions;
    
    private List<PatientElementExtension> elementExtensions;
    */
	
    public Patient() {    	
    }
    
    public Long getPatientId() {
    	return this.patientId;
    }
    
    public void setPatientId(Long patientId) {
    	this.patientId = patientId;
    }

    public List<PatientIdentifier> getIdentifiers() {
    	if (this.identifiers==null) {
    		this.identifiers = new ArrayList<PatientIdentifier>();
    	}
    	return this.identifiers;
    }
    
    public void setIdentifiers(List<PatientIdentifier> identifiers) {
    	this.identifiers = identifiers;
    }
    
    public Boolean getActive() {
    	return this.active;
    }
    
    public void setActive(Boolean active) {
    	this.active = active;
    }
    
    public String getGender() {
    	return this.gender;
    }
    
    public void setGender(String gender) {
    	this.gender = gender;
    }
    
    public List<PatientAddress> getAddresses() {
    	if (addresses==null) {
    		this.addresses = new ArrayList<PatientAddress>();
    	}
		return addresses;
	}

	public void setAddresses(List<PatientAddress> addresses) {
		this.addresses = addresses;
	}

    public PatientCodeableConcept getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(PatientCodeableConcept maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

    public Date getBirthDate() {
    	return this.birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
    	this.birthDate = birthDate;
    }

    public Boolean getDeceasedBoolean() {
		return deceasedBoolean;
	}

    public Timestamp getDeceasedDateTime() {
		return deceasedDateTime;
	}

	public void setDeceasedDateTime(Timestamp deceasedDateTime) {
		this.deceasedDateTime = deceasedDateTime;
	}

	public void setDeceasedBoolean(Boolean deceasedBoolean) {
		this.deceasedBoolean = deceasedBoolean;
	}

    public Boolean getMultipleBirthBoolean() {
		return multipleBirthBoolean;
	}

	public void setMultipleBirthBoolean(Boolean multipleBirthBoolean) {
		this.multipleBirthBoolean = multipleBirthBoolean;
	}

    public Integer getMultipleBirthInteger() {
		return multipleBirthInteger;
	}

	public void setMultipleBirthInteger(Integer multipleBirthInteger) {
		this.multipleBirthInteger = multipleBirthInteger;
	}

    public List<PatientHumanName> getNames() {
    	if (names == null ) {
    		names = new ArrayList<PatientHumanName>();
    	}
    	return this.names;
    }

    public void setNames(List<PatientHumanName> names) {
    	this.names = names;
    }
    
    public List<PatientAttachment> getPhotos() {
    	if (photos == null ) {
    		photos = new ArrayList<PatientAttachment>();
    	}
		return photos;
	}

	public void setPhotos(List<PatientAttachment> photos) {
		this.photos = photos;
	}

    public List<PatientContactPoint> getTelecoms() {
    	if (telecoms==null) {
    		telecoms = new ArrayList<PatientContactPoint>();
    	}
		return telecoms;
	}

	public void setTelecoms(List<PatientContactPoint> telecoms) {
		this.telecoms = telecoms;
	}

	public PatientReference getManagingOrganization() {
		return managingOrganization;
	}

	public void setManagingOrganization(PatientReference managingOrganization) {
		this.managingOrganization = managingOrganization;
	}

    public PatientAnimal getAnimal() {
		return animal;
	}

	public void setAnimal(PatientAnimal animal) {
		this.animal = animal;
	}

	public List<PatientCommunication> getCommunications() {
		if (communications==null) {
			communications = new ArrayList<PatientCommunication>();
		}
		return communications;
	}

	public void setCommunications(List<PatientCommunication> communications) {
		this.communications = communications;
	}

	public List<PatientLink> getLinks() {
		if (links==null) {
			links = new ArrayList<PatientLink>();
		}
		return links;
	}

	public void setLinks(List<PatientLink> links) {
		this.links = links;
	}

    public List<PatientContact> getContacts() {
    	if (contacts==null) {
    		contacts = new ArrayList<PatientContact>();
    	}
		return contacts;
	}

    public void setContacts(List<PatientContact> contacts) {
		this.contacts = contacts;
	}
}
