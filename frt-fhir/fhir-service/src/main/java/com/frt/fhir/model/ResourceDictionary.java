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

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import com.frt.dr.model.DomainResource;
import com.frt.dr.model.ResourceComplexType;

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
		resourcePairs.put(ResourceMapper.PATIENT, new ResourcePair(org.hl7.fhir.dstu3.model.Patient.class, com.frt.dr.model.base.Patient.class));
		resourcePairs.put(ResourceMapper.PATIENT_HUMANNAME, new ResourcePair(org.hl7.fhir.dstu3.model.HumanName.class, com.frt.dr.model.base.PatientHumanName.class));		
		resourcePairs.put(ResourceMapper.PATIENT_IDENTIFIER, new ResourcePair(org.hl7.fhir.dstu3.model.Identifier.class, com.frt.dr.model.base.PatientIdentifier.class));		
		resourcePairs.put(ResourceMapper.PATIENT_ADDRESS, new ResourcePair(org.hl7.fhir.dstu3.model.Address.class, com.frt.dr.model.base.PatientAddress.class));		
		resourcePairs.put(ResourceMapper.PATIENT_REFERENCE, new ResourcePair(org.hl7.fhir.dstu3.model.Reference.class, com.frt.dr.model.base.PatientReference.class));		
		resourcePairs.put(ResourceMapper.PATIENT_CODEABLECONCEPT, new ResourcePair(org.hl7.fhir.dstu3.model.CodeableConcept.class, com.frt.dr.model.base.PatientCodeableConcept.class));		
		resourcePairs.put(ResourceMapper.PATIENT_ATTACHMENT, new ResourcePair(org.hl7.fhir.dstu3.model.Attachment.class, com.frt.dr.model.base.PatientAttachment.class));		
		resourcePairs.put(ResourceMapper.PATIENT_CONTACTPOINT, new ResourcePair(org.hl7.fhir.dstu3.model.ContactPoint.class, com.frt.dr.model.base.PatientContactPoint.class));		
	}
	
	static Hashtable<String, Class> resources = new Hashtable<String, Class>();

	static {
		resources.put(ResourceMapper.PATIENT, com.frt.dr.model.base.Patient.class);
	}

	static Hashtable<String, Class> complextypes = new Hashtable<String, Class>();

	static {
		complextypes.put(ResourceMapper.PATIENT_HUMANNAME, com.frt.dr.model.base.PatientHumanName.class);		
		complextypes.put(ResourceMapper.PATIENT_IDENTIFIER, com.frt.dr.model.base.PatientIdentifier.class);		
		complextypes.put(ResourceMapper.PATIENT_ADDRESS, com.frt.dr.model.base.PatientAddress.class);		
		complextypes.put(ResourceMapper.PATIENT_REFERENCE, com.frt.dr.model.base.PatientReference.class);		
		complextypes.put(ResourceMapper.PATIENT_CODEABLECONCEPT, com.frt.dr.model.base.PatientCodeableConcept.class);		
		complextypes.put(ResourceMapper.PATIENT_ATTACHMENT, com.frt.dr.model.base.PatientAttachment.class);		
		complextypes.put(ResourceMapper.PATIENT_CONTACTPOINT, com.frt.dr.model.base.PatientContactPoint.class);		
	}

	public static DomainResource getResourceInstance(String type) {
		DomainResource instance = null;
		try {
			instance = (DomainResource)(resources.get(type).getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// come back and throw a wrapped FRT exception
			e.printStackTrace();
		}
		return instance;
	}

	public static <T extends ResourceComplexType> T getComplexInstance(String type) {
		ResourceComplexType instance = null;
		try {
			instance = (ResourceComplexType)(complextypes.get(type).getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// come back and throw a wrapped FRT exception
			e.printStackTrace();
		}
		return (T)instance;
	}
	
	public static ResourcePair get(String key) {
		return resourcePairs.get(key.toUpperCase());
	}
	
}
