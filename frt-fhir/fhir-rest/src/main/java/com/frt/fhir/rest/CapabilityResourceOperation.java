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
package com.frt.fhir.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.service.FhirConformanceService;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * CreateResourceInteraction class
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class CapabilityResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(CapabilityResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	private FhirConformanceService conformance;
	
	public CapabilityResourceOperation() { 
		parser = new JsonParser();
		conformance = new FhirConformanceService();
	}
	
	/**
	 * Retrieve the FHIR server capability statement resource
	 * GET [base]/frt-fhir-rest/1.0/metadata{?mode=[mode]} {&_format=[mime-type]}
	 * @param mode return information mode: full, normative or terminology 
	 * @param _format mime-type json or xml, default josn and json supported
	 * @return the FHIR server capability statement resource
	 */
	@GET
	@Path(ResourcePath.METADATA_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@QueryParam("mode") @DefaultValue("normative") final String mode,
						 @QueryParam("_format") @DefaultValue("json") final String _format) {	
		
		logger.info(localizer.x("FHR_I003: CapabilityResourceOperation reads the capability statement by mode {0}", mode));										
		
		CapabilityStatement cs = conformance.getCapabilityStatement();
		String resourceInJson = parser.serialize(cs);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
	}
	
}
