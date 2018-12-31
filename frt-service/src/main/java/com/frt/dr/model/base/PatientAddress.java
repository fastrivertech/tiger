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
import java.util.List;

import javax.persistence.FetchType;
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

import com.frt.dr.SqlHelper;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_ADDRESS")
@SequenceGenerator(name = "PATIENT_ADDRESS_SEQ", sequenceName = "PATIENT_ADDRESS_SEQ", allocationSize=1)
@XmlRootElement
public class PatientAddress implements Serializable, ResourceComplexType {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_ADDRESS_SEQ")  
    @Basic(optional = false)
    @NotNull(message = "Identifier logical Id cannot be Null")
    @Column(name = "address_id")    
    private String addressId;
    
	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    @ManyToOne(optional = false)
    private Patient patient;

    @Size(max = 128)    
    @Column(name = "path")                                            
    private String path;

    @Size(max = 32)    
    @Column(name = "use")                                        
    private String use;

    @Size(max = 32)    
    @Column(name = "type")                                        
    private String type;

	@Size(max = 2048)    
    @Column(name = "txt")                                    
    private String txt;

    @Size(max = 2048)    
    @Column(name = "line")                                
    private String line;

    @Size(max = 32)    
    @Column(name = "city")                            
    private String city;

    @Size(max = 32)    
    @Column(name = "district")                            
    private String district;

    @Size(max = 32)    
    @Column(name = "state")                            
    private String state;

    @Size(max = 32)    
    @Column(name = "postalcode")                            
    private String postalcode;

    @Size(max = 32)    
    @Column(name = "country")                            
    private String country;

    @Lob
    @Column(name = "period")                        
    private String period;
    
  //private List<PatientExtension> extensions;
        
  //private List<PatientElementExtension> elementExtensions;
    
	public PatientAddress() {    	
    }
    
    
    public Patient getPatient() {
    	return this.patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

    public String getAddressId() {
		return addressId;
	}


	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getUse() {
		return use;
	}


	public void setUse(String use) {
		this.use = use;
	}

    public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	public String getTxt() {
		return txt;
	}


	public void setTxt(String txt) {
		this.txt = txt;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getPostalcode() {
		return postalcode;
	}


	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}

//    public String getPeriod() {
//		return SqlHelper.toString(this.period);
//	}
//
//	public void setPeriod(Clob period) {
//		this.period = period;
//	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
