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
 * IdValidator class
 * @author cqye
 */
public class IdValidator implements Validator<String> {

	public IdValidator() {
	}
	
	@Override
	public boolean supports(Class<String> clazz) {
		return Resource.class.equals(clazz);		
	}
    
	@Override
	public void validate(String id)
	    throws IdValidatorException {
		if (id == null || id.isEmpty()) {
			throw new IdValidatorException("resource logical id is not allowed NULL");
		}
	}

	public void validate(String id, Resource resource)
	    throws IdValidatorException {		
		this.validate(id);
		if (!id.equals(resource.getId())) {
			throw new IdValidatorException("given resource Id shall be equivalent to the resource logical id of the message body");			
		}		
	}
	
}
