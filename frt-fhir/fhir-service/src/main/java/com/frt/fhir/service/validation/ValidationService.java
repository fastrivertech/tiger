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

import java.util.List;
import java.util.ArrayList;
import com.frt.dr.model.Resource;

public class ValidationService {

	private List<Validator> validators = new ArrayList<>();
	private static ValidationService instance;
	
    private ValidationService() 
    	throws ValidatorException {
    	validators.add(ValidatorFactory.createValidator(IdValidator.class));
    }

    public static ValidationService getInstance() {
    	if (instance == null) {
    		instance = new ValidationService();
    	}
        return instance;
    }

    public void validate(String id, Resource resource) {
    	validators.forEach(validator->{
    		if (validator instanceof IdValidator) {
        	   ((IdValidator)validator).validate(id, resource);    			
    		} 
    	});
    }
	
}
