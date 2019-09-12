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
package com.frt.dr.service.query;

import java.io.Serializable;

/**
 * QueryOption class
 * @author cqye
 */
public class QueryOption implements Serializable {

	private boolean summary = false;
	private MediaType format = MediaType.JSON;
	private StatusType status = StatusType.ALL;

	public enum StatusType {
		
		ACTIVE("Active"),
		INACTIVE("Inactive"),
		ALL("All");		
		private final String type;
		
		StatusType(String type) {
			this.type = type;
		}
		
		@Override 
		public String toString() {
			return this.type;
		}
		
		public static StatusType value(String text) {
			for (StatusType t : StatusType.values()) {
				if (t.toString().equals(text)) {
					return t;
				}
			}
			return null;
		}
		
	}
	
	public enum MediaType {
		JSON("json"),
		APPLICATION_JSON("application/json"),	
		APPLICATION_FHIR_JSON("application/fhir+json"),
		XML("xml"),
		TEXT_XML("text/xml"),
		APPLICATION_XML("application/xml"),		
		APPLICATION_FHIR_XML("application/fhir+xml");
	
		private final String type;
		
		MediaType(String type) {
			this.type = type;
		}
		
		@Override 
		public String toString() {
			return this.type;
		}
		
		public static MediaType value(String text) {
			for (MediaType t : MediaType.values()) {
				if (t.toString().equals(text)) {
					return t;
				}
			}
			return null;
		}
	}
	
	public QueryOption() {	
	}
	
	public void setSummary(boolean summary) {
		this.summary = summary;
	}
	
	public boolean getSummary() {
		return this.summary;
	}
	
	public void setFormat(MediaType format) {
		this.format = format;
	}
	
	public MediaType getFormat() {
		return this.format;
	}
	
	public void setStatus(StatusType status) {
		this.status = status;
	}
	
	public StatusType getStatus() {
		return this.status;
	}
	
}
