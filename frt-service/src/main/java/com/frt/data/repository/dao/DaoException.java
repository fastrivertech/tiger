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
package com.frt.data.repository.dao;

public class DaoException extends Exception {

	/**
	 * DaoException Constructor
	 */
	public DaoException() {
		super();
	}

	/**
	 * DaoException Constructor
	 * 
	 * @param m Message string
	 */
	public DaoException(String m) {
		super(m);
	}

	/**
	 * DaoException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public DaoException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * DaoException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public DaoException(Throwable t) {
		super(t);
	}
}
