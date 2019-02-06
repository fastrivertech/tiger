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

import org.hl7.fhir.dstu3.model.CapabilityStatement;

/**
 * FhirConformanceService class
 * @author chaye
 */
public class FhirConformanceService {

	public FhirConformanceService() {		
	}
	
	public CapabilityStatement getCapabilityStatement() {
		return new CapabilityStatement();
	}
}
