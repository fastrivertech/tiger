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
import java.sql.Date;
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

@Entity
@Table(name = "PATIENT")
@SequenceGenerator(name = "PATIENT_SEQ", sequenceName = "PATIENT_SEQ", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "getById", query = "SELECT P FROM PATIENT P WHERE P.patient_id = :id")
})
public class Patient implements Serializable {
    private static final long serialVersionUID = -8321293485415818761L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_SEQ")  
    @NotNull(message = "Patient logical Id cannot be Null")
    @Column(name = "patient_id", insertable = false, updatable = false)
    private Long patientId;
    
    @NotNull(message = "Domain resource logical Id cannot be Null")
    @Column(name = "domain_resource_id")    
    private Long domainResourceId;

    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")    
    @OneToMany(mappedBy = "PATIENT", cascade = CascadeType.ALL)
    @OrderBy("identifier_id ASC")        
    private List<PatientIdentifier> identifiers;

    @NotNull(message = "Active cannot be Null")
    @Column(name = "active")        
    private Boolean active;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")    
    @OneToMany(mappedBy = "PATIENT", cascade = CascadeType.ALL)
    @OrderBy("humanname_id ASC")    
    private List<PatientHumanName> names;

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
        
    private List<PatientExtension> extensions;
    
    private List<PatientElementExtension> elementExtensions;
        
    public Patient() {    	
    }
    
    public Long getPatientId() {
    	return this.patientId;
    }
    
    public void setPatientId(Long patientId) {
    	this.patientId = patientId;
    }

    public List<PatientIdentifier> getIdentifiers() {
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
    
    public List<PatientHumanName> getNames() {
    	return this.names;
    }

    public void setNames(List<PatientHumanName> names) {
    	this.names = names;
    }
    
}
