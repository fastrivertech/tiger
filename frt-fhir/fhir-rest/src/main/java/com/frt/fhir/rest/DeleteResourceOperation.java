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
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.service.FhirConformanceService;
import com.frt.fhir.service.FhirService;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class DeleteResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(DeleteResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private FhirService fhirService;

	public DeleteResourceOperation() { 
		fhirService = new FhirService();
	}
	
	@DELETE
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("type") final String type,
		 				 @PathParam("id") final String id) {
		
		logger.info(localizer.x("FHR_I004: DeleteResourceOperation deletes a resource {0} by its id {1}", type, id));										
		String resourceInJson = "not implemented yet";
		fhirService.delete(type, id);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
	}	
	
}
