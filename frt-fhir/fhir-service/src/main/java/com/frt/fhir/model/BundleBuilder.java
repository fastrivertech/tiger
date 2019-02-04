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
package com.frt.fhir.model;

import java.util.List;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.service.FhirServiceException;

/**
 * BundleBuidler class
 * @author cqye
 */
public class BundleBuilder {

	/** 
	 * create a FHIR bundle resource
	 * @param resources a list of FHIR domain resources
	 * @return bundle resource
	 * @throws FhirServiceException
	 */
	public static <R extends DomainResource> Bundle create(List<R> resources) 
		throws FhirServiceException {		
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.SEARCHSET);
		resources.forEach(resource->{
			bundle.addEntry().setResource(resource);
		});	
		return bundle;
	}
	
}
