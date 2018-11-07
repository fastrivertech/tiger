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

/**
 * FhirServiceException class
 * @author chaye
 */
public class FhirServiceException extends Exception {

	/**
	 * FhirServiceException Constructor
	 */
	public FhirServiceException() {
		super();
	}

	/**
	 * FhirServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public FhirServiceException(String m) {
		super(m);
	}

	/**
	 * FhirServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public FhirServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * FhirServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public FhirServiceException(Throwable t) {
		super(t);
	}

}
