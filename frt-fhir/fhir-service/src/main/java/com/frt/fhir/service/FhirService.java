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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedMap;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Bundle;
import com.frt.fhir.model.map.MapperException;
import com.frt.fhir.model.map.ResourceMapperFactory;
import com.frt.fhir.model.map.ResourceMapperInterface;
import com.frt.fhir.service.validation.ValidationService;
import com.frt.fhir.service.validation.ValidatorException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.dr.service.RepositoryApplication;
import com.frt.dr.service.RepositoryContext;
import com.frt.dr.service.RepositoryContextException;
import com.frt.dr.service.RepositoryServiceException;
import com.frt.dr.service.query.CompositeParameter;
import com.frt.dr.service.query.QueryOption;
import com.frt.dr.service.query.ResourceQueryUtils;
import com.frt.dr.service.query.QueryCriteria;

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
	
	public <R extends DomainResource> Optional<R> create(@Nonnull String type, 
														 @Nonnull R hapiResource) 
		throws FhirServiceException {	
		try {
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);		
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			
			Object frtResource = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map((Object)hapiResource);
			repository.create(resourcePair.getFrt(), frtResource);
			
			return Optional.of(hapiResource);
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}
	}

	public <R extends DomainResource> Optional<List<R>> read(@Nonnull String type,
												  			QueryCriteria criterias,
												  			QueryOption options) 
		throws FhirServiceException {
		try {
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			
			Optional<List<com.frt.dr.model.DomainResource>> frtResources = repository.read(resourcePair.getFrt(), criterias);
			Optional<List<R>> hapiResources = Optional.empty();
			if (frtResources.isPresent()) {
				hapiResources = Optional.of(new ArrayList());
				for (com.frt.dr.model.DomainResource frtResource : frtResources.get()) {
					R hapiResource = (R)mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object) frtResource);
					hapiResources.get().add(hapiResource);
				}
			}
			return hapiResources;
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}		
	}

	public <R extends DomainResource> Optional<R> read(@Nonnull String type, 
													   @Nonnull String id,
													   QueryOption options) 
		throws FhirServiceException {
		Optional<R> retVal = Optional.empty();
		try {
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			
			Optional<com.frt.dr.model.DomainResource> frtResource = repository.read(resourcePair.getFrt(), id);
			if (frtResource.isPresent()) {
				R hapiResource = (R)mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object)frtResource.get());
				retVal = Optional.of(hapiResource);
			}
			
			return retVal;
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}		
	}
	
	public <R extends DomainResource> R update(@Nonnull String type, 
											   String id, 
			 								   R hapiResource) 
		throws FhirServiceException, ValidatorException {
		try {						
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);		
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);			
			Object frtResource = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map((Object)hapiResource);
			
			ValidationService.getInstance().validate(id, (com.frt.dr.model.Resource)frtResource);
			
			com.frt.dr.model.DomainResource updatedfrtResource = repository.update(resourcePair.getFrt(), id, frtResource);									
			R updatedhapiResource = (R)mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object)updatedfrtResource);
			return updatedhapiResource;			
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}		
	}
	
	public void delete(@Nonnull String type, 
					   @Nonnull String id) 
	    throws FhirServiceException {
		try {
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);		
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			repository.delete(resourcePair.getFrt(), id);
		} catch (RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}
	}	
		
	public <R extends DomainResource> Optional<List<R>> history(@Nonnull String type, 
			   											        @Nonnull String id,
			   											        QueryOption options) 
		throws FhirServiceException {
		try {
			ResourceMapperInterface mapper = ResourceMapperFactory.getInstance().create(type);
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(type);
			Optional<List<com.frt.dr.model.DomainResource>> frtResources = repository.history(resourcePair.getFrt(), id);
			Optional<List<R>> hapiResources = Optional.empty();
			if (frtResources.isPresent()) {
				hapiResources = Optional.of(new ArrayList());
				for (com.frt.dr.model.DomainResource frtResource : frtResources.get()) {
					R hapiResource = (R)mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object) frtResource);
					hapiResources.get().add(hapiResource);
				}				
			}			
			return hapiResources;
		} catch (MapperException | RepositoryServiceException ex) {
			throw new FhirServiceException(ex);
		}				
		
	}

}
