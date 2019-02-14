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
import java.util.Optional;
import com.frt.dr.model.DomainResource;
import com.frt.dr.service.query.QueryCriteria;;

/**
 * RepositoryService Interface
 * @author chaye
 */
public interface RepositoryService {
	
	<R extends DomainResource> Optional<R> read(java.lang.Class<?> resourceClazz, String id) 
		throws RepositoryServiceException;
	
	<R extends DomainResource> Optional<List<R>> query(java.lang.Class<?> resourceClazz, QueryCriteria criterias) 
		throws RepositoryServiceException;

	<R extends DomainResource> R save(java.lang.Class<?> resourceClazz, R resource)
	   throws RepositoryServiceException;
	
	<R extends DomainResource> R update(java.lang.Class<?> resourceClazz, String id, R resource)
		throws RepositoryServiceException;	

	<R extends DomainResource> Optional<R> delete(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException;
	
	<R extends DomainResource> Optional<List<R>> history(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException;

	<R extends DomainResource> Optional<R> vRead(java.lang.Class<?> resourceClazz, String id, String vid) 
		throws RepositoryServiceException;
		
}
