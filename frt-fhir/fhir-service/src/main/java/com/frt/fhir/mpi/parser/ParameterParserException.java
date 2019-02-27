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

public class ParameterParserException extends RuntimeException {

	/**
	 * ParserException Constructor
	 */
	public ParameterParserException() {
		super();
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param m Message string
	 */
	public ParameterParserException(String m) {
		super(m);
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public ParameterParserException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * ParserException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public ParameterParserException(Throwable t) {
		super(t);
	}
}
