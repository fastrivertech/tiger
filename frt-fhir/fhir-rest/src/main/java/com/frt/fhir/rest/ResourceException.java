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
package com.frt.fhir.rest;

/**
 * ResourceException class
 * 
 * @author cqye
 */
public class ResourceException extends RuntimeException {
  
    /**
     * ResourceException Constructor
     */
    public ResourceException () {
        super();
    }

    /**
     * ResourceException Constructor
     * @param m Message string
     */
    public ResourceException (String m) {
        super(m);
    }

    /**
     * ResourceExceptionConstructor
     * @param m Message string
     * @param t Throwable inherited
     */
    public ResourceException (String m, Throwable t) {
        super(m, t);
    }

    /**
     * ResourceException Constructor
     * @param t Throwable inherited
     */
    public ResourceException (Throwable t) {
        super(t);
    }
    
}
