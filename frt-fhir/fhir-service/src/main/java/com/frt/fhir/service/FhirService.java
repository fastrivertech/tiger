/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.service;

import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.model.ResourceMapperFactory;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.MapperException;

/**
 * RepositoryApplication class
 * @author chaye
 */
public class FhirService {

	public <R extends DomainResource> R create(String type, R resource) 
		throws FhirServiceException {	
		try {
			
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);		
			return null;
		} catch (MapperException mex) {
			throw new FhirServiceException(mex);
		}
	}

	public <R extends DomainResource> R findById(String type, Long Id) 
		throws FhirServiceException {
		try {
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);		
			return null;
		} catch (MapperException mex) {
			throw new FhirServiceException(mex);
		}		
	}
	
}
