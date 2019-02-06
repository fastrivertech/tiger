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
package com.frt.dr.transaction;

public class TransactionServiceException extends RuntimeException {

	/**
	 * TransactionServiceException Constructor
	 */
	public TransactionServiceException() {
		super();
	}

	/**
	 * TransactionServiceException Constructor
	 * 
	 * @param m Message string
	 */
	public TransactionServiceException(String m) {
		super(m);
	}

	/**
	 * TransactionServiceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public TransactionServiceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * TransactionServiceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public TransactionServiceException(Throwable t) {
		super(t);
	}
}
