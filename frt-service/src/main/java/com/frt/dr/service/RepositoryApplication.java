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
package com.frt.dr.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.frt.dr.model.DomainResource;
import com.frt.dr.service.query.QueryCriteria;

/**
 * RepositoryApplication class
 * @author chaye
 */
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan("com.frt.dr.service")})
public class RepositoryApplication {

	private RepositoryServiceImpl repositoryService;
	
	@Autowired
	public RepositoryApplication(RepositoryServiceImpl repositoryService) {
		this.repositoryService = repositoryService;
	}
	
    public <R extends DomainResource> void create(java.lang.Class<?> resourceClazz, Object object)
		throws RepositoryServiceException {
		R resource = (R)object;
	    this.repositoryService.save(resourceClazz,resource);
	}
     
    public <R extends DomainResource> Optional<R> read(java.lang.Class<R> resourceClazz, String id)
		throws RepositoryServiceException {
    	Optional<R> resource = this.repositoryService.read(resourceClazz, id);    	
		return resource;
	}
	
	public <R extends DomainResource> Optional<List<R>> read(Class<?> resourceClazz, QueryCriteria criterias)
		throws RepositoryServiceException {
		Optional<List<R>> resources = this.repositoryService.query(resourceClazz, criterias);    	
		return resources;
	}
	
	public <R extends DomainResource> R update(java.lang.Class<?> resourceClazz, String id, Object resource)
		throws RepositoryServiceException {
		return this.repositoryService.update(resourceClazz, id, (R)resource); 
	}
	
	public <R extends DomainResource> Optional<R> delete(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		return this.repositoryService.delete(resourceClazz, id); 
	}
	
	public <R extends DomainResource> Optional<List<R>> history(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		return this.repositoryService.history(resourceClazz, id);
	}
	
	public static void main(String[] args) {
		try {
			RepositoryContext context = new RepositoryContext(RepositoryApplication.class);			
			RepositoryApplication repositoryApplication = (RepositoryApplication)context.getBean(RepositoryApplication.class);	
		} catch (RepositoryContextException rsex) {
			rsex.printStackTrace();
		}				
	}
	
}
