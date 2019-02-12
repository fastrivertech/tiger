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
 * ResourceValidationException class
 * @author cqye
 */
public class ResourceValidatorException extends ValidatorException {

    public ResourceValidatorException() {
        super();
    }

    public ResourceValidatorException(String m) {
        super(m);
    }

    public ResourceValidatorException(String m, Throwable t) {
        super(m,t);
    }

    public ResourceValidatorException(Throwable t) {
        super(t);
    }
}
