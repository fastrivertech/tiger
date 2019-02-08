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

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;

import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.ValidationException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class UpdateResourceOperation extends ResourceOperation {	
	
	private static Logger logger = Logger.getLog(UpdateResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
	
	@Context
	private UriInfo uriInfo;
	 
	private JsonParser parser;
	private FhirService fhirService;
	
	public UpdateResourceOperation() {
		parser = new JsonParser();
		fhirService = new FhirService();
	}
	
	@PUT
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Consumes(MediaType.APPLICATION_JSON)	
	@Produces(MediaType.APPLICATION_JSON)	
	public <R extends DomainResource> Response update(@PathParam("type") final String type,
						   @PathParam("id") final String id,
						   @QueryParam("_format") @DefaultValue("json") final String _format, 
						   final String body) {
		
		logger.info(localizer.x("FHR_I006: UpdateResourceOperatio updates the resource {0} by its id {1}", type, id));										
		try {
			OperationValidator.validateFormat(_format);
			R resource = parser.deserialize(type, body);	
			R updated = fhirService.update(type, id, resource);
			String resourceInJson = parser.serialize(updated);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
		} catch (ValidationException vx) {
			String error = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MediaType.APPLICATION_JSON);				
		} catch (JsonFormatException jfx) {
			String error = "invalid resource: " + jfx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MediaType.APPLICATION_JSON);							 
		} catch (FhirServiceException ex) {								
			String error = "service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MediaType.APPLICATION_JSON);							 			 			 
		} 
	}
}
