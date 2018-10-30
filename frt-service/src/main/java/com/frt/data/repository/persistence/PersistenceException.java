/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.data.repository.persistence;

/**
 * PersistenceException class
 * 
 * @author cqye
 */
public class PersistenceException extends Exception {

	/**
	 * PersistenceException Constructor
	 */
	public PersistenceException() {
		super();
	}

	/**
	 * PersistenceException Constructor
	 * 
	 * @param m Message string
	 */
	public PersistenceException(String m) {
		super(m);
	}

	/**
	 * PersistenceException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public PersistenceException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * PersistenceException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public PersistenceException(Throwable t) {
		super(t);
	}

}
