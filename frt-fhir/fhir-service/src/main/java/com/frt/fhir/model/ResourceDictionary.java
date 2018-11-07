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
package com.frt.fhir.model;

import java.util.Hashtable;

/**
 * ResourceMappingTable Interface
 * @author chaye
 */
public class ResourceDictionary {
	
	public static class ResourcePair {
		private Class fhir;
		private Class frt;
		
		public ResourcePair(Class fhir, Class frt) {
			this.fhir = fhir;
			this.frt = frt;
		}
		
		public void setFhir(Class fhir) {
			this.fhir = fhir;
		}
		
		public Class getFhir() {
			return this.fhir;
		}
		
		public void setFrt(Class frt) {
			this.frt = frt;
		}
		
		public Class getFrt() {
			return this.frt;
		}
	}
	
	static Hashtable<String, ResourcePair> resourcePairs = new Hashtable<String, ResourcePair>();
	
	static {
		resourcePairs.put("PATIENT", new ResourcePair(org.hl7.fhir.dstu3.model.Patient.class, com.frt.dr.model.base.Patient.class));
	}
	
	public static ResourcePair get(String key) {
		return resourcePairs.get(key.toUpperCase());
	}
	
}
