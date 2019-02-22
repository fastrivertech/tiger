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

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.service.FhirServiceException;
import com.frt.fhir.model.BundleBuilder;

/**
 * BundleBuidler class
 * @author cqye
 */
public class BundleBuilder {

	/** 
	 * create a FHIR bundle resource
	 * @param list a list of FHIR domain resources
	 * @return bundle resource
	 * @throws FhirServiceException
	 */
	public static <R extends DomainResource> Bundle create(Bundle.BundleType bundleType, List<R> list, UriInfo uriInfo, Status status) 
		throws FhirServiceException {		
		
		Bundle bundle = new Bundle();		
		bundle.setId("id" + System.currentTimeMillis());		
		bundle.setType(bundleType);
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.setLastUpdated(new Date());
		bundle.setMeta(meta);
		final String baseUrl = uriInfo.getAbsolutePath().toString();
		String[] urlParts = null;
		if (bundleType==BundleType.HISTORY) {
			urlParts = uriInfo.getAbsolutePath().toString().split("_history");
		}
		final String urlBase = urlParts!=null?urlParts[0]:baseUrl;
		list.forEach(resource->{
			BundleEntryComponent entry = bundle.addEntry();
			entry.setFullUrl(urlBase + "/" + resource.getIdElement().getIdPart());
			entry.setResource(resource);
			if (bundleType==BundleType.HISTORY) {
				Bundle.BundleEntryRequestComponent reqComp = new Bundle.BundleEntryRequestComponent();
				reqComp.setMethod(Bundle.HTTPVerb.GET);
				reqComp.setUrl(resource.getIdElement().getIdPart());
				Bundle.BundleEntryResponseComponent respComp = new Bundle.BundleEntryResponseComponent();
				respComp.setStatus(status.toString());
				entry.setRequest(reqComp);
				entry.setResponse(respComp);
			}
		});	
		bundle.setTotal(list.size());
		return bundle;
	}

}
