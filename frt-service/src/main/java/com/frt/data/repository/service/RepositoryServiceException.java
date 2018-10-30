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
package com.frt.data.repository.service;

public class RepositoryServiceException extends Exception {

	/**
	 * RepositoryServiceException Constructor
	 */
	public RepositoryServiceException() {
		super();
	}

	/**
	 * RepositoryServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public RepositoryServiceException(String m) {
		super(m);
	}

	/**
	 * RepositoryServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public RepositoryServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * RepositoryServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public RepositoryServiceException(Throwable t) {
		super(t);
	}

}
