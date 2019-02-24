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
import com.frt.fhir.mpi.resource.Parameters;

/**
 * MpiServiceImpl class
 * @author cqye
 */
public class MpiServiceImpl implements MpiService {

	public MpiServiceImpl() {
	}

	public Bundle match(Parameters parameters) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public void search() 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public Bundle merge(String sourceId, String targetId) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public Bundle unmerge(String resourceId) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public Bundle link(String sourceId, String targetId) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public Bundle unlink(String resourceId) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

}
