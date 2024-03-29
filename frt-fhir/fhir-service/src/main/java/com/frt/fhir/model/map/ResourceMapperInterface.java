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
package com.frt.fhir.model.map;

/**
 * ResourceMapper Interface
 * @author chaye
 */
public interface ResourceMapperInterface {
	
	// FHIR resources
	public static final String PATIENT = "PATIENT";
	// FHIR complex types
	public static final String PATIENT_ADDRESS = "PATIENT_ADDRESS";
	public static final String PATIENT_CONTACTPOINT = "PATIENT_CONTACTPOINT";
	public static final String PATIENT_CONTACT = "PATIENT_CONTACT";
	public static final String PATIENT_CODEABLECONCEPT = "PATIENT_CODEABLECONCEPT";
	public static final String PATIENT_ATTACHMENT = "PATIENT_ATTACHMENT";
	public static final String PATIENT_HUMANNAME = "PATIENT_HUMANNAME";
	public static final String PATIENT_IDENTIFIER = "PATIENT_IDENTIFIER";
	public static final String PATIENT_REFERENCE = "PATIENT_REFERENCE";
	public static final String PATIENT_EXTENSION = "PATIENT_EXTENSION";
	public static final String PATIENT_ANIMAL = "PATIENT_ANIMAL";
	public static final String PATIENT_COMMUNICATION = "PATIENT_COMMUNICATION";
	public static final String PATIENT_LINK = "PATIENT_LINK";
	public static final String DOMAINRESOURCE = "DOMAINRESOURCE";
	public static final String DOMAINRESOURCE_TEXT = "DOMAINRESOURCE_TEXT";
	public static final String DOMAINRESOURCE_CONTAINED = "DOMAINRESOURCE_CONTAINED";
	public static final String RESOURCE = "RESOURCE";
	public static final String RESOURCE_META = "RESOURCE_META";

	ResourceMapperInterface from(Class source);
	
	ResourceMapperInterface to(Class target);
	
	Object map(Object source) throws MapperException;
	
	Object map(Object source, Object target) throws MapperException;
	
}
