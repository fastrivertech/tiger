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
package com.frt.dr.transaction.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Meta class
 * @author cqye
 */
public class Meta implements Serializable{	
	static final long serialVersionUID = -8321293485415819089L;	
	
	private String id;
	private ZonedDateTime lastUpdated;
	
	public Meta() {
		id = "1"; 
		lastUpdated = ZonedDateTime.now();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setLastUpdated(ZonedDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public ZonedDateTime getLastUpdated() {
		return this.lastUpdated;
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{\n");
		strBuilder.append("\"versionId\" : \"").append(id).append("\",\n");
		strBuilder.append("\"lastUpdated\" : \"").append(lastUpdated.toString()).append("\"\n");	
		strBuilder.append("}");		
		return strBuilder.toString();
	}
	
}
