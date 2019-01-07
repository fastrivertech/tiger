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

import java.util.List;
import java.io.Serializable;
import java.sql.Clob;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
@DiscriminatorColumn(name="CONCRETE_RESOURCE_TYPE")
//@SequenceGenerator(name = "DOMAIN_RESOURCE_SEQ", sequenceName = "DOMAIN_RESOURCE_SEQ", allocationSize=1)
@NamedQueries({
    @NamedQuery(name = "getDomainResourceById", query = "SELECT DR FROM DomainResource DR WHERE DR.id = :id")
})
public class DomainResource extends Resource {
    private static final long serialVersionUID = -8321293485415818761L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "DOMAIN_RESOURCE_SEQ")  
//    @Basic(optional = false)
//    @NotNull(message = "DomainResource logical Id cannot be Null")
//    @Size(max = 64)    
//    @Column(name = "domain_resource_id", nullable = false, updatable=false)
//	private String domainResourceId;
//	
//    @NotNull(message = "Resource logical Id cannot be Null")
//    @Column(name = "resource_id")    
//	@JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
//    @OneToOne(mappedBy = "resource", cascade = CascadeType.ALL)
//	private Resource resource;
	
    @Lob
    @Column(name = "txt")                        
	private String txt;
	
    @Lob
    @Column(name = "contained")                        
	private String contained;

//	private List<PatientExtension> extension;
//
//	private List<PatientExtension> modifierExtension;
	
//	public String getDomainResourceId() {
//		return domainResourceId;
//	}
//
//	public void setDomainResourceId(String domainResourceId) {
//		this.domainResourceId = domainResourceId;
//	}
//
//	public Resource getResource() {
//		return resource;
//	}
//
//	public void setResource(Resource resource) {
//		this.resource = resource;
//	}

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
	
}
