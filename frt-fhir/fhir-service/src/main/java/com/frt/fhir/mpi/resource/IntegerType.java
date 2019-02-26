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

public class IntegerType extends DataType<Integer> {
	private static final long serialVersionUID = 1L;
	
	public IntegerType() {		
	}
	
	public IntegerType(Integer theInteger) {
		setValue(theInteger);
		setValueAsString(theInteger.toString());		
	}	
	
	public IntegerType(String theInteger) {
		setValueAsString(theInteger);
		setValue(Integer.parseInt(theInteger));
	}	
}
