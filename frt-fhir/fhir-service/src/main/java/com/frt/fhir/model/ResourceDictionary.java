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
	
	static { // this map table seems only used at recource level
		resourcePairs.put("PATIENT", new ResourcePair(org.hl7.fhir.dstu3.model.Patient.class, com.frt.dr.model.base.Patient.class));
		resourcePairs.put("PATIENT_HUMANNAME", new ResourcePair(org.hl7.fhir.dstu3.model.HumanName.class, com.frt.dr.model.base.PatientHumanName.class));		
		resourcePairs.put("PATIENT_IDENTIFIER", new ResourcePair(org.hl7.fhir.dstu3.model.Identifier.class, com.frt.dr.model.base.PatientIdentifier.class));		
		resourcePairs.put("PATIENT_ADDRESS", new ResourcePair(org.hl7.fhir.dstu3.model.Address.class, com.frt.dr.model.base.PatientAddress.class));		
		resourcePairs.put("PATIENT_REFERENCE", new ResourcePair(org.hl7.fhir.dstu3.model.Reference.class, com.frt.dr.model.base.PatientReference.class));		
		resourcePairs.put("PATIENT_CODEABLECONCEPT", new ResourcePair(org.hl7.fhir.dstu3.model.CodeableConcept.class, com.frt.dr.model.base.PatientCodeableConcept.class));		
		resourcePairs.put("PATIENT_ATTACHMENT", new ResourcePair(org.hl7.fhir.dstu3.model.Attachment.class, com.frt.dr.model.base.PatientAttachment.class));		
		resourcePairs.put("PATIENT_CONTACTPOINT", new ResourcePair(org.hl7.fhir.dstu3.model.ContactPoint.class, com.frt.dr.model.base.PatientContactPoint.class));		
	}
	
	public static ResourcePair get(String key) {
		return resourcePairs.get(key.toUpperCase());
	}
	
}
