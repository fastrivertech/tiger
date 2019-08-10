/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.mpi.resource;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Match Resource
 * options for OHMPI: one exact match, the same system match, match w update, match threshold 
 * options for Fhir: onlyCertainMatches, count
 * @author cqye
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Parameters implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private String resourceType;
	
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private List<Parameter> parameters;
    
	public Parameters() {	
	}
	
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public String getResourceType() {
		return this.resourceType;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public List<Parameter> getParameters() {
		return this.parameters;
	}
	
	
}
