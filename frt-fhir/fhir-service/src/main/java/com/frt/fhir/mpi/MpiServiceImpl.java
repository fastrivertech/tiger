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

import java.util.List;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;

/**
 * MpiServiceImpl class
 * @author cqye
 */
public class MpiServiceImpl implements MpiService<DomainResource> {

	public MpiServiceImpl() {
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#match(Parameters) 
	 */
	@Override
	public Bundle match(Parameters parameters) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#search(Parameters)
	 */
	@Override
	public Bundle search(Parameters parameters)
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#merge(String, String, List)
	 */
	@Override	
	public Bundle merge(String sourceId, String targetId, List<Parameter> options)
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unmerge(String, List)
	 */
	@Override		
	public Bundle unmerge(String resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#link(String, String, String, List)
	 */
	@Override		
	public Bundle link(String domain, String sourceId, String targetId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unlink(String, String, List)
	 */
	@Override		
	public Bundle unlink(String domain, String resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

}
