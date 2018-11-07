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
import com.frt.fhir.model.ResourceDictionary;
import com.frt.dr.model.base.Patient;
import com.frt.dr.service.RepositoryApplication;
import com.frt.dr.service.RepositoryContext;
import com.frt.dr.service.RepositoryContextException;

/**
 * RepositoryApplication class
 * @author chaye
 */
public class FhirService {

	private RepositoryApplication repositoryApplication;
	
	public FhirService() 
		throws FhirServiceException {
		try {
			RepositoryContext context = new RepositoryContext(RepositoryApplication.class); 			
			repositoryApplication = (RepositoryApplication)context.getBean(RepositoryApplication.class);			
		} catch (RepositoryContextException rcex) {
			throw new FhirServiceException(rcex);
		}						
	}
	
	public <R extends DomainResource> R create(String type, R resource) 
		throws FhirServiceException {	
		try {
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);		
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map((Object)resource);
			
			return null;
		} catch (MapperException mex) {
			throw new FhirServiceException(mex);
		}
	}

	public <R extends DomainResource> R read(String type, Long Id) 
		throws FhirServiceException {
		try {
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);		
			return null;
		} catch (MapperException mex) {
			throw new FhirServiceException(mex);
		}		
	}
	
}
