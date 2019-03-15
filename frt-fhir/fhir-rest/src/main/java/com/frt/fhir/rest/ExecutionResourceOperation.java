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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.PathParam;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.fhir.parser.JsonParser;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path(ResourcePath.BASE_PATH)
@PermitAll
public class ExecutionResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(ExecutionResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;	
	private JsonParser jsonParser;
	
	public ExecutionResourceOperation() {
		jsonParser = new JsonParser();	
	}

	/**
	 * Operations on any resource
	 * @param resource resource type
	 * @param operation operation 
	 * @param body operation parameters
	 * @param _format json or xml, default json and json supported  	
	 * @return operation result
	 * @status 201 operated 
 	 * @status 400 Bad request
	 * @status 422 Not processable resource 
	 * @status 500 Internal server error 
	 */
	@POST
	@Path(ResourcePath.RESOURCE_PATH + ResourcePath.OPERATION_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Operation(summary = "Operations on any resource", description="Operations on any resource",
    tags = {ResourceOperation.EXECUTE},
    responses = {
            @ApiResponse(
               content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "201", description = "OK, Operation completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "422", description = "Not processable resource"),
            @ApiResponse(responseCode = "500", description = "Server Internal Error") })
	public Response execute(
			@Parameter(description = "FHIR Resource Type, the type of target resource, e.g. Patient", required = true) @PathParam("resource") final String resource, 
			@Parameter(description = "FHIR Resource Operation executed, the operation executed on the resource", required = true) @PathParam("operation") final String operation,
			@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) @QueryParam("_format") @DefaultValue("json") final String _format,
			@Parameter(description = "Operation Parameters: the parameters required to perform the operation.", required = false) final String body) {		
		logger.info(localizer.x("FHR_I008: ExecutionResourceOperation executes the POST command ${0} on resource {1}", operation, resource));												
		String message = "Patient resource operation $" + operation + " not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}
	
}
