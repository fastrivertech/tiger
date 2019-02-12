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
package com.frt.dr.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.frt.dr.model.base.PatientExtension;

/**
 *  DomainResource class
 * @author chaye
 */
@Entity
@DiscriminatorValue("DOMAIN_RESOURCE")
@Table(name = "DOMAIN_RESOURCE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="DOMAIN_RESOURCE_TYPE") 
//uncomment Seq Generator will cause Resource insert does not accept NULL error
//@SequenceGenerator(name = "DOMAIN_RESOURCE_SEQ", sequenceName = "DOMAIN_RESOURCE_SEQ", allocationSize=1)
@NamedQueries({
    @NamedQuery(name = "getDomainResourceById", query = "SELECT DR FROM DomainResource DR WHERE DR.id = :id")
})
public class DomainResource extends Resource {
    private static final long serialVersionUID = -8321293485415818761L;

    //uncomment Seq Generator will cause Resource insert does not accept NULL error
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "DOMAIN_RESOURCE_SEQ")  
    @Column(name = "domain_resource_id", insertable = false, updatable=false)
    private BigInteger domainResourceId;
	
    //@Lob
    @Size(max=2048)
    @Column(name = "txt")                        
	private String txt;
	
    @Lob
    @Column(name = "contained")                        
	private String contained;

  //private List<PatientExtension> extension;
  //private List<PatientExtension> modifierExtension;
	
	public BigInteger getDomainResourceId() {
		return domainResourceId;
	}

	public void setDomainResourceId(BigInteger domainResourceId) {
		this.domainResourceId = domainResourceId;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public String getContained() {
		return contained;
	}

	public void setContained(String contained) {
		this.contained = contained;
	}

    public <R extends Extension> List<R> getExtensions() {
    	return new ArrayList<R>();
    }	
    
}
