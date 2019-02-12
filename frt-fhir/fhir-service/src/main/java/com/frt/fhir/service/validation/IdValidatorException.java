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
 * IdValidationException class
 * @author cqye
 */
public class IdValidatorException extends ValidatorException {

    public IdValidatorException() {
        super();
    }

    public IdValidatorException(String m) {
        super(m);
    }

    public IdValidatorException(String m, Throwable t) {
        super(m,t);
    }

    public IdValidatorException(Throwable t) {
        super(t);
    }	
    
}
