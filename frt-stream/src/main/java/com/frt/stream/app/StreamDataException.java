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
package com.frt.stream.app;

/*
 * StreamDataException class
 * @author chaye
 */
public class StreamDataException extends Exception {

	/**
	 * StreamDataException Constructor
	 */
	public StreamDataException() {
		super();
	}

	/**
	 * StreamDataException Constructor
	 * 
	 * @param m Message string
	 */
	public StreamDataException(String m) {
		super(m);
	}

	/**
	 * StreamDataException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public StreamDataException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * StreamDataException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public StreamDataException(Throwable t) {
		super(t);
	}

}
