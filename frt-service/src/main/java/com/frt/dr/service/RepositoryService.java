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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frt.dr.model.DomainResource;

public interface RepositoryService {

	<R extends DomainResource> R findById(Class<R> resourceClazz, String id) 
		throws RepositoryServiceException;
	
	<R extends DomainResource> R save(R resource)
	   throws RepositoryServiceException;
	
}
