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
package com.frt.dl;

/**
 * DataLakeServiceException class
 * @author cqye
 */
public class DataLakeServiceException extends RuntimeException {

	/**
	 * DataLakeServiceException Constructor
	 */
	public DataLakeServiceException() {
		super();
	}

	/**
	 * DataLakeServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public DataLakeServiceException(String m) {
		super(m);
	}

	/**
	 * DataLakeServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public DataLakeServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * DataLakeServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public DataLakeServiceException(Throwable t) {
		super(t);
	}

}
