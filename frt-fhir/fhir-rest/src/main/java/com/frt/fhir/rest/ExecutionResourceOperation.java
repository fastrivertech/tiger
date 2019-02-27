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
import javax.ws.rs.PUT;
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
import org.hl7.fhir.dstu3.model.Bundle;
import com.frt.fhir.mpi.MpiService;
import com.frt.fhir.mpi.MpiServiceImpl;
import com.frt.fhir.mpi.parser.ParameterParser;
import com.frt.fhir.mpi.parser.ParameterParserException;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;
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
	
	private JsonParser jsonParser;
	private ParameterParser paramParser;
	private MpiService mpiService;
	
	public ExecutionResourceOperation() {
		jsonParser = new JsonParser();	
		paramParser = new ParameterParser();
		mpiService = new MpiServiceImpl();
	}
	
	/**
	 * Patient MPI operations
	 * POST [base]/Patient/$match, [base]/Patient/$merge, [base]/Patient/$link
	 * @param operation $match, $merge and $unmerge, $link and $unlink 
	 * @param _format json or xml, json supported
	 * @param body match message body
	 * @return operation result 
	 */
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_POST_OPERATION_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response mpiPost(@PathParam("operation") final String operation,
						    @QueryParam("_format") @DefaultValue("json") final String _format,
						    final String body) {
		
		logger.info(localizer.x("FHR_I008: ExecutionResourceOperation executes the POST command ${0}", operation));										
		if ("match".equals(operation)) {
			Parameters params = paramParser.deserialize(body);
			Bundle bundle = mpiService.match(params);			
		} else if ("search".equals(operation)) {
			Parameters params = paramParser.deserialize(body);
			Bundle bundle = mpiService.search(params);
		} else {
			String message = "Patient resource operation $" + operation + "invalid"; 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);		
			String resourceInJson = jsonParser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
			
		}		
		String message = "Patient resource operation $" + operation + " not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);		
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}

	@PUT
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_PUT_OPERATION_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response mpiPut(@PathParam("operation") final String operation,
						   @QueryParam("_format") @DefaultValue("json") final String _format,
						   final String body) {
		
		logger.info(localizer.x("FHR_I009: ExecutionResourceOperation executes the PUTcommand ${0}", operation));										
		
		String message = "Patient resource operation $" + operation + " not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}

	
}
