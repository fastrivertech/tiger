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

/**
 * MpiServiceException class
 * @author cqye
 */
public class MpiServiceException extends RuntimeException {

	/**
	 * MpiServiceException Constructor
	 */
	public MpiServiceException() {
		super();
	}

	/**
	 * MpiServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public MpiServiceException(String m) {
		super(m);
	}

	/**
	 * MpiServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public MpiServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * MpiServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public MpiServiceException(Throwable t) {
		super(t);
	}

}
