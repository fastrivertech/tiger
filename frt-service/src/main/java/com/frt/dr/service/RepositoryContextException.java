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
package com.frt.dr.service;

/**
 * RepositoryContextException class
 * @author chaye
 */
public class RepositoryContextException extends Exception {

	/**
	 * RepositoryContextException Constructor
	 */
	public RepositoryContextException() {
		super();
	}

	/**
	 * RepositoryContextException Constructor
	 * 
	 * @param m Message string
	 */
	public RepositoryContextException(String m) {
		super(m);
	}

	/**
	 * RepositoryContextException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public RepositoryContextException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * RepositoryContextException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public RepositoryContextException(Throwable t) {
		super(t);
	}
	
}
