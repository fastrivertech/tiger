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
package com.frt.dr.service.query;

public class QueryException extends RuntimeException {
	private static final long serialVersionUID = -8321293485415818762L;
	
	/**
	 * QueryException Constructor
	 */
	public QueryException() {
		super();
	}

	/**
	 * QueryException Constructor
	 * 
	 * @param m Message string
	 */
	public QueryException(String m) {
		super(m);
	}

	/**
	 * QueryException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public QueryException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * QueryException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public QueryException(Throwable t) {
		super(t);
	}
}
