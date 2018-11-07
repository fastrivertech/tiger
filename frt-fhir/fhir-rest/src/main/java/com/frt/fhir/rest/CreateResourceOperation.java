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

import java.util.Optional;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.annotation.security.PermitAll;
import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.rest.validation.InteractionValidator;
import com.frt.fhir.rest.validation.ValidationException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class CreateResourceOperation extends ResourceOperation {	
	private static Logger logger = Logger.getLog(CreateResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance();
	
    @Context
    private UriInfo uriInfo;

	private JsonParser parser;
	private FhirService fhirService;
	
	public CreateResourceOperation() {
		parser = new JsonParser();
		fhirService = new FhirService();
	}
	
	@POST
	@Path(ResourcePath.TYPE_PATH)
	@Consumes(MediaType.APPLICATION_JSON)	
	@Produces(MediaType.APPLICATION_JSON)	
	public <R extends DomainResource> Response create(@PathParam("type") final String type,
						   						      @QueryParam("_format") @DefaultValue("json") final String _format, 
						   						      final String body) {
		try {
			logger.info(localizer.x("CreateResourceInteraction creates a new resource"));
			// Request includes resource, but no need id; id shall be ignored if given. versionId and lastUpdated shall be ignored 
			// if meta provided.
			
			// Response includes Location header: Location: [base]/[type]/[id]/_history/[vid]
			//                   ETag header: versionId and Last-Modified
			// 201 Created Success
			// 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
			// 404 Not Found - Resource type not supported, or not a FHIR end-point
			// 422 Unprocessable Entity - The proposed resource violated applicable FHIR profiles or server business rules. 
			//                            This should be accompanied by an OperationOutcome resource providing additional detail
			// 412 Precondition Failed - Conditional create not supported
			// 200 OK status - Ignore request if some condition not match for conditional create
			// Conditional create - Create a new resource only if some equivalent resource does not already exist on the server.
			
			InteractionValidator.validateFormat(_format);
			R resource = parser.deserialize(type, body);	
			Optional<R> created = fhirService.create(type, resource);
			if (created.isPresent()) {
				Response.ResponseBuilder responseBuilder = Response.status(Status.CREATED).entity(created.get());
				return responseBuilder.build();
			} else {
				Throwable t = new ResourceException("failed to create domain resource '" + type + "'");
				throw new ResourceOperationException(t, Response.Status.BAD_REQUEST,
							Response.Status.BAD_REQUEST.toString(), "failed to create domain resource: " + t.getMessage(),
							uriInfo.getAbsolutePath().getRawPath(), null);							
			}
		} catch (ValidationException vx) {
			 throw new ResourceOperationException(vx, Response.Status.BAD_REQUEST,
					 								Response.Status.BAD_REQUEST.toString(), "invalid parameter: " + vx.getMessage(),
					 								uriInfo.getAbsolutePath().getRawPath(), null);			
		} catch (JsonFormatException jfx) {
			 throw new ResourceOperationException(jfx, Response.Status.NOT_ACCEPTABLE,
												    Response.Status.NOT_ACCEPTABLE.toString(), "invalid resource: " + jfx.getMessage(),
												    uriInfo.getAbsolutePath().getRawPath(), null);						
		} catch (FhirServiceException fsx) {
			 throw new ResourceOperationException(fsx, Response.Status.INTERNAL_SERVER_ERROR,
					    Response.Status.INTERNAL_SERVER_ERROR.toString(), "service failure: " + fsx.getMessage(),
					    uriInfo.getAbsolutePath().getRawPath(), null);									
		}
	}
	
}
