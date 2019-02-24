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
package com.frt.fhir.mpi;

import org.hl7.fhir.dstu3.model.Bundle;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;

/**
 * FHIR Mpi Service
 * @author cqye
 */
public interface MpiService<T> {

	Bundle match(Parameters parameters) 
		throws MpiServiceException;
	
	void search()
		throws MpiServiceException;
	
	Bundle merge(String sourceId, String targetId) 
		throws MpiServiceException;
	
	Bundle unmerge(String resourceId) 
		throws MpiServiceException;
	
	Bundle link(String sourceId, String targetId) 
		throws MpiServiceException;
	
	Bundle unlink(String resourceId) 
		throws MpiServiceException;
	
}
