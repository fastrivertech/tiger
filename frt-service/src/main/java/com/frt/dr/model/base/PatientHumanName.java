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
import com.frt.dr.SqlHelper;
import com.frt.dr.model.ResourceComponent;

@Entity
@Table(name = "PATIENT_HUMANNAME")
@SequenceGenerator(name = "PATIENT_HUMANNAME_SEQ", sequenceName = "PATIENT_HUMANNAME_SEQ", allocationSize=1)
@XmlRootElement
public class PatientHumanName implements Serializable, ResourceComponent {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_HUMANNAME_SEQ")
    @Basic(optional = false)
    @NotNull(message = "Humanname logical Id cannot be Null")
    @Column(name = "humanname_id")    
    private Long humannameId;
        
    @Size(max = 128)    
    @Column(name = "path")                                                
    private String path;
    
    @Size(max = 32)    
    @Column(name = "use")                                                
    private String use;

    @Size(max = 2048)    
    @Column(name = "txt")                                                    
    private String txt;
    
    @Size(max = 32)    
    @Column(name = "family")                                                    
    private String family;

    @Column(name = "given")                                                        
    private Clob given;
    
    @Column(name = "prefix")                                                        
    private Clob prefix;
    
    @Column(name = "suffix")                                                        
    private Clob suffix;
    
    @Column(name = "period")                                                        
    private Clob period;
    
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;
 
    /*
    private List<PatientExtension> extensions;
    
    private List<PatientElementExtension> elementExtensions;
    */
    
    public PatientHumanName(){    	
    }
    
    public Long getHumannameId() {
    	return this.humannameId;
    }
    
    public void setHumannameId(Long humannameId) {
    	this.humannameId = humannameId;
    }

    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }
    
    public String getPath() {
    	return this.path;
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
    
    public String getTxt() {
    	return this.txt;
    }
    
    public void setTxt(String txt) {
    	this.txt = txt;
    }
    
    public String getFamily() {
    	return this.family;
    }
    
    public void setFamily(String family) {
    	this.family = family;
    }
        
    public String getGiven() {
    	return SqlHelper.toString(this.given);
    }

    public void setGiven(Clob given) {
    	this.given = given;
    }
    
    public String getPrefix() {
    	return SqlHelper.toString(this.prefix);
    }

    public void setPrefix(Clob prefix) {
    	this.prefix = prefix;
    }
    
    public String getSuffix() {
    	return SqlHelper.toString(this.suffix);
    }

    public void setSuffix(Clob suffix) {
    	this.suffix = suffix;
    }
    
    public String getPeriod() {
    	return SqlHelper.toString(this.period);
    }

    public void setPeriod(Clob period) {
    	this.period = period;
    }
    
}
