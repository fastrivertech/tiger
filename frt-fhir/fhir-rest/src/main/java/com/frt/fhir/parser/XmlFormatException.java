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
 * XmlFormatException class
 * 
 * @author cqye
 */
public class XmlFormatException extends RuntimeException {
  
    /**
     * XmlFormatException Constructor
     */
    public XmlFormatException() {
        super();
    }

    /**
     * XmlFormatException Constructor
     * @param m Message string
     */
    public XmlFormatException(String m) {
        super(m);
    }

    /**
     * XmlFormatException Constructor
     * @param m Message string
     * @param t Throwable inherited
     */
    public XmlFormatException(String m, Throwable t) {
        super(m, t);
    }

    /**
     * XmlFormatException Constructor
     * @param t Throwable inherited
     */
    public XmlFormatException(Throwable t) {
        super(t);
    }
    
}
