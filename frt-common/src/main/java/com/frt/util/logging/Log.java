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
package com.frt.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	private final Logger delegate;
	
	private Log(Logger delegate) {
		this.delegate = delegate;
	}
	
	public static Log getLog(String name) {
		return new Log(Logger.getLogger(name));
	}
	
	public void debug(String msg) {		
		delegate.log(Level.FINEST, msg);
	}

	public void debug(String msg, Throwable thrown) {		
		delegate.log(Level.FINEST, msg, thrown);
	}
	
	public void info(String msg) {
		delegate.log(Level.INFO, msg);		
	}

	public void info(String msg, Throwable thrown) {
		delegate.log(Level.INFO, msg, thrown);		
	}
	
	public void warn(String msg) {		
		delegate.log(Level.WARNING, msg);		
	}

	public void warn(String msg, Throwable thrown) {		
		delegate.log(Level.WARNING, msg, thrown);		
	}
	
	public void error(String msg) {
		delegate.log(Level.SEVERE, msg);				
	}

	public void error(String msg, Throwable thrown) {
		delegate.log(Level.SEVERE, msg, thrown);				
	}
	
}
