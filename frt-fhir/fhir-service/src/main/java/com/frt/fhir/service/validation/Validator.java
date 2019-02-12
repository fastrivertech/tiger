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
 * Validator interface 
 * @author cqye
 * @param <T>
 */
public interface Validator<T> {

	boolean supports(Class<T> clazz);
	    
	void validate(T object)
	     throws ValidatorException;
	
}
