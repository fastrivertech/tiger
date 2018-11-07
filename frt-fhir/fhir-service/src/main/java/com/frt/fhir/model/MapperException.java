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
package com.frt.fhir.model;

/**
 * MapperException Interface
 * @author chaye
 */
public class MapperException extends Exception {

	/**
	 * MapperException Constructor
	 */
	public MapperException() {
		super();
	}

	/**
	 * MapperException Constructor
	 * 
	 * @param m Message string
	 */
	public MapperException(String m) {
		super(m);
	}

	/**
	 * MapperException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public MapperException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * MapperException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public MapperException(Throwable t) {
		super(t);
	}

}
