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

import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.model.ResourceMapperFactory;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.dr.model.base.Patient;
import com.frt.dr.service.RepositoryApplication;
import com.frt.dr.service.RepositoryContext;
import com.frt.dr.service.RepositoryContextException;
import com.frt.dr.service.RepositoryServiceException;

/**
 * RepositoryApplication class
 * @author chaye
 */
public class FhirService {

	private RepositoryApplication repository;
	
	public FhirService() 
		throws FhirServiceException {
		try {
			RepositoryContext context = new RepositoryContext(RepositoryApplication.class); 			
			repository = (RepositoryApplication)context.getBean(RepositoryApplication.class);			
		} catch (RepositoryContextException rcex) {
			throw new FhirServiceException(rcex);
		}						
	}
	
	public <R extends DomainResource> Optional<R> create(@Nonnull String type, @Nonnull R resource) 
		throws FhirServiceException {	
		try {
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);		
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map((Object)resource);
			repository.create(resourcePair.getFrt(), target);
			return Optional.of(resource);
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}
	}

	public <R extends DomainResource> Optional<R> read(@Nonnull String type, @Nonnull Long id) 
		throws FhirServiceException {
		try {
			ResourceMapper mapper = ResourceMapperFactory.getInstance().create(type);
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			Object resource = repository.read(resourcePair.getFrt(), id);
			Object target = mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object)resource);						
			return Optional.of((R)target);
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}		
	}
	
}
