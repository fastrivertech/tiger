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

import java.util.List;
import java.util.Optional;

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

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;

import com.frt.dr.model.base.Patient;
import com.frt.dr.service.query.QueryOption;
import com.frt.fhir.model.BundleBuilder;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path(ResourcePath.BASE_PATH)
@PermitAll
public class vReadResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(vReadResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
		
	@Context
	private UriInfo uriInfo;
	
	private FhirService fhirService;	
	private JsonParser parser;
	
	public vReadResourceOperation(){
		parser = new JsonParser();
		fhirService = new FhirService();
	}
	
	/**
	 * Retrieve the resource version by its logical Id and version id
	 * GET [base]/frt-fhir-rest/API/[type]/[id]/_history/[vid]{?_format=[mime-type]}
	 * @param type Resource type, e.g., Patient
	 * @param id Resource logical id, e.g., 1356
	 * @param vid Resource version id, e.g., 1,3,5,6
	 * @param _format json or xml, default josn and json supported
	 * @return bundle bundle of resource history
	 * @status 200 Success
	 * @status 404 Not found
	 * @status 400 Bad request
	 * @status 500 Internal server error	 * 
	 */
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH + ResourcePath.HISTORY_PATH + ResourcePath.VID_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Retrieve Patient Version", description= "Retrieve the resource version by its logical Id and version id",
    tags = {ResourceOperation.READ},
    responses = {
            @ApiResponse(description = "FHIR DomainResource: Bundle of resource versions by its logical Id and version id",
                    content = @Content(mediaType = "application/fhir+json")),
            @ApiResponse(responseCode = "200", description = "Resource versions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found - Unknown resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
            })
	public <R extends DomainResource> Response read(
			@PathParam("type") final String type, 
			@PathParam("id") final String id,
			@PathParam("vid") final String vid,						 						    
			@QueryParam("_format") @DefaultValue("json") final String _format) 
	{
		
		logger.info(localizer.x("FHR_I007: vReadResourceOperation retrieves the resource {0} by its id {1} and version # {2}", type, id, vid));										
		
		try {
			OperationValidator.validateId(Optional.ofNullable(id));
			OperationValidator.validateId(Optional.ofNullable(vid));
			Optional<R> found = fhirService.vRead(type, id, vid);
			if (found.isPresent()) {
				String resourceInJson = parser.serialize(found.get());      
				String location = uriInfo.getAbsolutePath().getPath() + "/_history/" + found.get().getMeta().getVersionId();
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, found.get().getMeta().getVersionId(), location, MimeType.APPLICATION_FHIR_JSON);
			} else {
				String error = "invalid domain resource logical id '" + id + "'" +  " with version '" + vid + "'"; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							      OperationOutcome.IssueSeverity.ERROR, 
																							      OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_FOUND, "", MimeType.APPLICATION_FHIR_JSON);				
			}	
		} catch (OperationValidatorException vx) {
			String error = "invalid id: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);							
		}  catch (FhirServiceException ex) {
			String error = "\"service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.INTERNAL_SERVER_ERROR, "", MimeType.APPLICATION_FHIR_JSON);							
		}
	}

}
