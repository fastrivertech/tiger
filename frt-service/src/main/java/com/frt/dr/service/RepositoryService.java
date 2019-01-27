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
import javax.ws.rs.core.MultivaluedMap;
import com.frt.dr.model.DomainResource;

/**
 * RepositoryService Interface
 * @author chaye
 */
public interface RepositoryService {
	<R extends DomainResource> R read(java.lang.Class<?> resourceClazz, String id) 
		throws RepositoryServiceException;
	
	<R extends DomainResource> List<R> query(java.lang.Class<?> resourceClazz, MultivaluedMap params) 
			throws RepositoryServiceException;

	<R extends DomainResource> R save(java.lang.Class<?> resourceClazz, R resource)
	   throws RepositoryServiceException;
}
