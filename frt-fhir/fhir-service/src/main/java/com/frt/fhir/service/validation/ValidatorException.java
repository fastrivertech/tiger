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
package com.frt.fhir.service.validation;

/**
 * ValidationException class
 * @author cqye
 */
public class ValidatorException extends RuntimeException {

    public ValidatorException() {
        super();
    }

    public ValidatorException(String m) {
        super(m);
    }

    public ValidatorException(String m, Throwable t) {
        super(m,t);
    }

    public ValidatorException(Throwable t) {
        super(t);
    }
    
}
