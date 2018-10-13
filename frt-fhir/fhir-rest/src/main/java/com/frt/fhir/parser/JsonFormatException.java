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
package com.frt.fhir.parser;

/**
 * JsonFormatException class
 * 
 * @author cqye
 */
public class JsonFormatException extends RuntimeException {
  
    /**
     * JsonFormatException Constructor
     */
    public JsonFormatException() {
        super();
    }

    /**
     * JsonFormatException Constructor
     * @param m Message string
     */
    public JsonFormatException(String m) {
        super(m);
    }

    /**
     * JsonFormatException Constructor
     * @param m Message string
     * @param t Throwable inherited
     */
    public JsonFormatException(String m, Throwable t) {
        super(m, t);
    }

    /**
     * JsonFormatException Constructor
     * @param t Throwable inherited
     */
    public JsonFormatException(Throwable t) {
        super(t);
    }
    
}