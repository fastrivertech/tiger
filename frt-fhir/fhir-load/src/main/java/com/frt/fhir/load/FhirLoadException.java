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
package com.frt.fhir.load;

public class FhirLoadException extends RuntimeException {
  
    /**
     * FhirLoadException Constructor
     */
    public FhirLoadException() {
        super();
    }

    /**
     * FhirLoadException Constructor
     * @param m Message string
     */
    public FhirLoadException(String m) {
        super(m);
    }

    /**
     * FhirLoadException Constructor
     * @param m Message string
     * @param t Throwable inherited
     */
    public FhirLoadException(String m, Throwable t) {
        super(m, t);
    }

    /**
     * FhirLoadException Constructor
     * @param t Throwable inherited
     */
    public FhirLoadException(Throwable t) {
        super(t);
    }

}
