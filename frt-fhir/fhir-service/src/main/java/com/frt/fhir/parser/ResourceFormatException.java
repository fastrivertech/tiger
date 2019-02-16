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
package com.frt.fhir.parser;

public class ResourceFormatException extends RuntimeException {
	private static final long serialVersionUID = -8321293485415818762L;
	
	/**
	 * ResourceFormatException Constructor
	 */
	public ResourceFormatException() {
		super();
	}

	/**
	 * ResourceFormatException Constructor
	 * 
	 * @param m Message string
	 */
	public ResourceFormatException(String m) {
		super(m);
	}

	/**
	 * ResourceFormatException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public ResourceFormatException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * ResourceFormatException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public ResourceFormatException(Throwable t) {
		super(t);
	}

}
