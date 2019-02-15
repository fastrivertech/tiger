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

@Path(ResourcePath.BASE_PATH)
@PermitAll
public class ExecutionResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(ExecutionResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	
	public ExecutionResourceOperation() {
		parser = new JsonParser();	
	}
	
	/**
	 * Patient MPI operations
	 * POST [base]/Patient/$match, [base]/Patient/$merge, [base]/Patient/$link
	 * @param operation $match, $merge and $unmerge, $link and $unlink 
	 * @param _format json or xml, json supported
	 * @return operation result 
	 */
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.OPERATION_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response execute(@PathParam("operation") final String operation,
						    @QueryParam("_format") @DefaultValue("json") final String _format) {
		
		logger.info(localizer.x("FHR_I008: ExecutionResourceOperation executes the command ${0}", operation));										
		
		String message = "Patient resource operation $" + operation + " not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = parser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);
		
	}
	
}
