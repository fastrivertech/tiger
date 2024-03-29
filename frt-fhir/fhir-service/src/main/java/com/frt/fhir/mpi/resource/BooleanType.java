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

public class BooleanType extends DataType<Boolean> {
	private static final long serialVersionUID = 1L;
	
	public BooleanType() {		
	}
	
	public BooleanType(Boolean theBoolean) {
		setValue(theBoolean);
		setValueAsString(theBoolean.toString());		
	}	
	
	public BooleanType(String theBoolean) {
		setValueAsString(theBoolean);
		setValue(Boolean.valueOf(theBoolean));
	}	
}
