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

/**
 * ObjectValueException class
 * @author cqye
 */
public class ObjectValueException extends RuntimeException {

	/**
	 * ObjectValueException Constructor
	 */
	public ObjectValueException() {
		super();
	}

	/**
	 * ObjectValueException Constructor
	 * 
	 * @param m Message string
	 */
	public ObjectValueException(String m) {
		super(m);
	}

	/**
	 * ObjectValueException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public ObjectValueException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * ObjectValueException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public ObjectValueException(Throwable t) {
		super(t);
	}

}	
