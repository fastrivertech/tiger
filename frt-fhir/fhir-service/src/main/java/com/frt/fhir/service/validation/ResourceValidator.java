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

import java.util.Set;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import com.frt.dr.model.Resource;

public class ResourceValidator implements Validator<Resource> {
	private javax.validation.Validator delegate; 
	
	public ResourceValidator() {
        ValidatorFactory validationFactory = Validation.buildDefaultValidatorFactory();
        delegate = validationFactory.getValidator();		
	}
	
	@Override
	public boolean supports(Class<Resource> clazz) {
		return Resource.class.equals(clazz);
	}
    
	@Override
	public void validate(Resource resource)
		throws ResourceValidatorException {
		StringBuilder errorMessage = new StringBuilder();
		Set<ConstraintViolation<Resource>> violates = delegate.validate(resource);	
		violates.forEach(violate->{
			if (errorMessage.length() != 0) {
				errorMessage.append("\n");
			}
			errorMessage.append(violate.getMessage());			
		});
		if (errorMessage.length() > 0) {
			throw new ResourceValidatorException(errorMessage.toString());
		}
	}
	
}
