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
package com.frt.fhir.mpi.resource;

import org.hl7.fhir.r4.model.Patient;

public class ResourceType extends DataType<Patient> {
	private static final long serialVersionUID = 1L;
	
	public ResourceType() {		
	}
	
	public ResourceType(Patient thePatient) {
		setValue(thePatient);
	}	
	
	public ResourceType(String thethePatient) {
		setValueAsString(thethePatient);
	}		
}
