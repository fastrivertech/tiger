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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Meta class
 * @author cqye
 */
public class Meta implements Serializable{	
	static final long serialVersionUID = -8321293485415819089L;	
	
	private String versionId;
	private String lastUpdated;
	
	public Meta() {
		versionId = "1"; 				
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		lastUpdated = format.format(Calendar.getInstance().getTime());								
	}
	
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	public String getversionId() {
		return this.versionId;
	}
	
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public String getLastUpdated() {		
		return this.lastUpdated;
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{\n");
		strBuilder.append("\"versionId\":\"").append(versionId).append("\",\n");
		strBuilder.append("\"lastUpdated\":\"").append(lastUpdated.toString()).append("\"\n");	
		strBuilder.append("}");		
		return strBuilder.toString();
	}
	
}
