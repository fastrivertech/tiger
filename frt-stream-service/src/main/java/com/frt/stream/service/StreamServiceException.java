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
package com.frt.stream.service;

/*
 * StreamServiceException class
 * @author chaye
 */
public class StreamServiceException extends Exception {

	/**
	 * StreamServiceException Constructor
	 */
	public StreamServiceException() {
		super();
	}

	/**
	 * StreamServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public StreamServiceException(String m) {
		super(m);
	}

	/**
	 * StreamServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public StreamServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * StreamServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public StreamServiceException(Throwable t) {
		super(t);
	}

}
