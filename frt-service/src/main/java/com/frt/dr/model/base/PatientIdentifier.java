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
import java.sql.Clob;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.frt.dr.SqlHelper;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_IDENTIFIER")
@SequenceGenerator(name = "PATIENT_IDENTIFIER_SEQ", sequenceName = "PATIENT_IDENTIFIER_SEQ", allocationSize=1)
@XmlRootElement
public class PatientIdentifier implements Serializable, ResourceComplexType {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_IDENTIFIER_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Identifier logical Id cannot be Null")
    @Column(name = "identifier_id")    
    private Long identifierId;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;

    @Size(max = 128)    
    @Column(name = "path")                                            
    private String path;

    @Size(max = 32)    
    @Column(name = "use")                                        
    private String use;

    @Lob
    @Column(name = "type")                                    
//    private Clob type;
    private String type;

    @Size(max = 128)    
    @Column(name = "system")                                
    private String system;

    @Size(max = 128)    
    @Column(name = "value")                            
    private String value;

    @Lob
    @Column(name = "period")                        
//    private Clob period;
    private String period;
    
    @Lob
	@Column(name = "assigner")                    
//    private Clob assigner;
    private String assigner;
 
//    private List<PatientExtension> extensions;
//    
//    private List<PatientElementExtension> elementExtensions;
    
	public PatientIdentifier() {    	
    }
    
    public Long getIdentifierId() {
    	return this.identifierId;
    }
    
    public void setIdentifierId(Long identifierId) {
    	this.identifierId = identifierId;
    }

    public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

    public String getUse() {
    	return this.use;
    }
    
    public void setUse(String use) {
    	this.use = use;
    }
    
    public String getSystem() {
    	return this.system;
    }
    
    public void setSystem(String system) {
    	this.system = system;
    }

    public String getValue() {
    	return this.value;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

//    public String getType() {
//		return SqlHelper.toString(this.type);
//	}

//    public Clob getType() {
    public String getType() {
		return this.type;
	}

//    public void setType(Clob type) {
    public void setType(String type) {
		this.type = type;
	}

//    public String getPeriod() {
//		return SqlHelper.toString(this.period);
//	}

//    public Clob getPeriod() {
    public String getPeriod() {
		return this.period;
	}

//    public void setPeriod(Clob period) {
    public void setPeriod(String period) {
		this.period = period;
	}

//    public String getAssigner() {
//		return SqlHelper.toString(this.assigner);
//	}

//    public Clob getAssigner() {
    public String getAssigner() {
		return this.assigner;
	}

//    public void setAssigner(Clob assigner) {
    public void setAssigner(String assigner) {
		this.assigner = assigner;
	}

}
