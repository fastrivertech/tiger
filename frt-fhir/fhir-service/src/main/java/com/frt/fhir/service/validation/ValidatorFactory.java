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

import com.frt.dr.model.Resource;

/**
 * ValidatorFactory class
 * @author cqye
 */
public class ValidatorFactory {

	public static Validator createValidator(Class clazz)
           throws ValidatorException {
        if (IdValidator.class.equals(clazz)) {
            return new IdValidator();
        } else if (ResourceValidator.class.equals(clazz)) {
            return new ResourceValidator();        	
        } else {
        	 throw new ValidatorException(clazz.getName() + " Validator not supported");        	
        }
	}
}
	
