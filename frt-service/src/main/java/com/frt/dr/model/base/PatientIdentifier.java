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
import java.math.BigInteger;
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
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_IDENTIFIER")
@SequenceGenerator(name = "PATIENT_IDENTIFIER_SEQ", sequenceName = "PATIENT_IDENTIFIER_SEQ", allocationSize=1)
@XmlRootElement
public class PatientIdentifier implements Serializable, ResourceComplexType {
    private static final long serialVersionUID = -8321293485415816561L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_IDENTIFIER_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Identifier physical Id cannot be Null")
    @Column(name = "identifier_id")    
    private BigInteger identifierId;
    
	@JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
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
    private String type;

    @Size(max = 128)    
    @Column(name = "system")                                
    private String system;

    @Size(max = 128)    
    @Column(name = "value")                            
    private String value;

    @Lob
    @Column(name = "period")                        
    private String period;
    
    @Lob
	@Column(name = "assigner")                    
    private String assigner;
 
  //private List<PatientExtension> extensions;
  //private List<PatientElementExtension> elementExtensions;
    
	public PatientIdentifier() {    	
    }
    
    public BigInteger getIdentifierId() {
    	return this.identifierId;
    }
    
    public void setIdentifierId(BigInteger identifierId) {
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

    public String getType() {
		return this.type;
	}

    public void setType(String type) {
		this.type = type;
	}

    public String getPeriod() {
		return this.period;
	}

    public void setPeriod(String period) {
		this.period = period;
	}
    public String getAssigner() {
		return this.assigner;
	}

    public void setAssigner(String assigner) {
		this.assigner = assigner;
	}

	// added for auto narrative gen, does not handle extension
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Use: ").append(this.getUse())
		.append(",").append("\\n")
		.append("Type: ").append(this.getType())
		.append(",").append("\\n")
		.append("Value: ").append(this.getValue());
		return sb.toString();
	}
}
