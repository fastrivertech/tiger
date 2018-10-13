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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.Resource;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.InteractionValidator;
import com.frt.fhir.rest.validation.ValidationException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class ReadResourceInteraction extends ResourceInteraction {
	private static Logger logger = Logger.getLog(ReadResourceInteraction.class.getName());
	private static Localization localizer = Localization.getInstance();
		
	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	
	public ReadResourceInteraction() {
		parser = new JsonParser();
	}	
	
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public <R extends Resource> Response read(@PathParam("type") final String type,
											  @PathParam("id") final String id,
											  @QueryParam("_format") @DefaultValue("json") final String _format,
											  @QueryParam("_summary") @DefaultValue("false") final String _summary) {
		try {
			logger.info(localizer.x("CreateResourceInteraction reads a current resource"));		
			// Request
			
			// Response includes ETag wuth versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 
			InteractionValidator.validateFormat(_format);
			InteractionValidator.validateSummary(_summary);
			
			R resource = (R)new org.hl7.fhir.dstu3.model.Patient();
			
			Response.ResponseBuilder responseBuilder = Response.status(Status.OK).entity(resource);        
        return responseBuilder.build();		
		} catch (ValidationException vx) {
			 throw new ResourceInteractionException(vx, Response.Status.BAD_REQUEST,
					 								Response.Status.BAD_REQUEST.toString(), "invalid parameter: " + vx.getMessage(),
					 								uriInfo.getAbsolutePath().getRawPath(), null);			
		}
	}
	
}
