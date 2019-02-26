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
package com.frt.fhir.mpi.parser;

public class ParserException extends RuntimeException {

	/**
	 * ParserException Constructor
	 */
	public ParserException() {
		super();
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param m Message string
	 */
	public ParserException(String m) {
		super(m);
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public ParserException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public ParserException(Throwable t) {
		super(t);
	}
}
