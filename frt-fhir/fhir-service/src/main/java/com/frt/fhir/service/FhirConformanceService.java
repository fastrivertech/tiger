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
package com.frt.fhir.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;

/**
 * FhirConformanceService class
 * @author chaye
 */
public class FhirConformanceService {

	private final static String CAPABILITY_STATEMENT_PATH = "./capability_statement.json";
	private CapabilityStatement cs;
	
	public FhirConformanceService() {		
	}
	
	public CapabilityStatement getCapabilityStatement() 
		throws FhirServiceException {
		if (cs == null) {
			load();
		}
		return cs;
	}
	
	private void load() 
		throws FhirServiceException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(CAPABILITY_STATEMENT_PATH)) {
			FhirContext context = FhirContext.forDstu3();
			JsonParser parser = (JsonParser)context.newJsonParser();
			cs = parser.parseResource(CapabilityStatement.class, new InputStreamReader(is));
		} catch (FileNotFoundException fnfex) {
			throw new FhirServiceException(fnfex);
		} catch (IOException ioex) {
			throw new FhirServiceException(ioex);
		} 				
	}
}
