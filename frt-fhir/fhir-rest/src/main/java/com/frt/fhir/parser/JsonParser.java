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
package com.frt.fhir.parser;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Resource;
import ca.uhn.fhir.parser.DataFormatException;

/**
 * JsonParser class
 * 
 * @author cqye
 */
public class JsonParser implements Parser {
	private ca.uhn.fhir.parser.JsonParser parser;
	
	public JsonParser() {		
		FhirContext context = FhirContext.forR4();
		parser = (ca.uhn.fhir.parser.JsonParser)context.newJsonParser();
	}
		
	public <R extends Resource> String serialize(R resource) 
		throws JsonFormatException {
		try {
			return parser.encodeResourceToString(resource);
		} catch(DataFormatException dfex) {
			throw new JsonFormatException(dfex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <R extends Resource> R deserialize(String resourceName, String message) 
	    throws JsonFormatException {
		try {
			resourceName = Character.toUpperCase(resourceName.charAt(0)) + 
					       resourceName.substring(1).toLowerCase();
			resourceName = "org.hl7.fhir.r4.model." + resourceName;
			Class<R> resourceClz = (Class<R>)Class.forName(resourceName);
			R resource = parser.parseResource(resourceClz, message);
			return resource;
		} catch (DataFormatException | ClassNotFoundException ex) {
			throw new JsonFormatException(ex);
		}
	}
	
	public <R extends Resource> R deserialize(Class<R> resourceClz, String message) 
		throws JsonFormatException {
		try {
			R resource = parser.parseResource(resourceClz, message);
			return resource;
		} catch(DataFormatException dfex) {
			throw new JsonFormatException(dfex);
		}
	}
	
}
