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

import java.io.Serializable;

public abstract class DataType<T> implements Serializable {
	private static final long serialVersionUID = 4623040030733049991L;
   /*
	* resource, valueInteger, valueString, valueBoolean
	*/
	private T typedValue;
	
	private String stringValue;
	
	public T getValue() {
		return this.typedValue;
	}
	
	public void setValue(T typedValue) {
		this.typedValue = typedValue;
	}
	
	public String getValueAsString() {
		return this.stringValue;
	}

	public void setValueAsString(String stringValue) {
		this.stringValue = stringValue;
	}
	
}
