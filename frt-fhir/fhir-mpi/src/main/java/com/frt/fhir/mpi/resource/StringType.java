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

public class StringType extends DataType<String> {
	private static final long serialVersionUID = 1L;
	
	public StringType() {		
	}
	
	public StringType(String theString) {
		setValue(theString);
		setValueAsString(theString);		
	}		
}
