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
package com.frt.fhir.mpi;

public class MpiValidationException extends RuntimeException {

	/**
	 * MpiInvalidException Constructor
	 */
	public MpiValidationException() {
		super();
	}

	/**
	 * MpiInvalidException Constructor
	 * 
	 * @param m Message string
	 */
	public MpiValidationException(String m) {
		super(m);
	}

	/**
	 * MpiInvalidException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public MpiValidationException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * MpiInvalidException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public MpiValidationException(Throwable t) {
		super(t);
	}

}
