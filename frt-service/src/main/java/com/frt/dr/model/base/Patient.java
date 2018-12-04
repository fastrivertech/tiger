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
import java.util.Comparator;
import java.util.Date;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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

    /*
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")    
    @OneToMany(mappedBy = "PATIENT", cascade = CascadeType.ALL)
    @OrderBy("identifier_id ASC")        
    private List<PatientIdentifier> identifiers;
    */
    
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
    
}
