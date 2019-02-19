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
package com.frt.dr.service.search;

public class SearchParameterException extends RuntimeException {
	private static final long serialVersionUID = -8321293485415818762L;
	
	/**
	 * SearchParameterException Constructor
	 */
	public SearchParameterException() {
		super();
	}

	/**
	 * SearchParameterException Constructor
	 * 
	 * @param m Message string
	 */
	public SearchParameterException(String m) {
		super(m);
	}

	/**
	 * SearchParameterException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public SearchParameterException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * SearchParameterException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public SearchParameterException(Throwable t) {
		super(t);
	}
	
}
