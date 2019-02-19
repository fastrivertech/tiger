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

import java.io.Serializable;

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
import javax.persistence.MappedSuperclass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DomainExtension class
 * @author chaye
 */
//@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public abstract class Extension implements Serializable {
	private static final long serialVersionUID = 1L;	
	
    @Size(max = 128)    
    @Column(name = "path")                                                	
	private String path;
	
    @Size(max = 128)    
    @Column(name = "url", nullable = false)                                                    
	private String url;
	
    @Lob 
    @Column(name = "value")                                                    
	private String value;

    public String getPath() {
    	return this.path;
    }
    
    public void setPath(String path) {
    	this.path = path;
    }
    
    public String getUrl() {
    	return this.url;
    }
    
    public void setUrl(String url) {
    	this.url = url;
    }

    public String getValue() {
    	return this.value;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public abstract <R extends DomainResource> void setResource(R resource);

    public abstract <R extends DomainResource> R getResource();
    
}
