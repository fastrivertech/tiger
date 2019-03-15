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
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;

import com.frt.dr.model.base.Patient;
import com.frt.dr.service.query.QueryOption;
import com.frt.fhir.model.BundleBuilder;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * HistoryResourceOperation class
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class HistoryResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(HistoryResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
		
	@Context
	private UriInfo uriInfo;
	private FhirService fhirService;	
	private JsonParser parser;
	
	public HistoryResourceOperation(){
		parser = new JsonParser();
		fhirService = new FhirService();
	}
	
	/**
	 * Retrieve the history of a resource by its logical Id
	 * GET [base]/frt-fhir-rest/API/[type]/[id]/_history{?_format=[mime-type]}
	 * @param type Resource type, e.g., Patient
	 * @param id Resource logical id, e.g., 1356
	 * @param _format json or xml, default json and json supported
	 * @return bundle bundle of resource history
	 * @status 200 OK
 	 * @status 400 Bad request
	 * @status 404 Not found 
	 * @status 500 Internal server error
	 */
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH + ResourcePath.HISTORY_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Retrieve History of A Patient", description= "Retrieve the history of a patient resource by its logical Id",
    tags = {ResourceOperation.READ},
    responses = {
            @ApiResponse(description = "FHIR DomainResource: Bundle of Resource History",
                    content = @Content(mediaType = "application/fhir+json",
                            schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "200", description = "OK, Resource History Retrieved Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Resource Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
            })
	public <R extends DomainResource> Response history(
			@PathParam("type") final String type, 
			@PathParam("id") final String id,
			@QueryParam("_format") @DefaultValue("json") final String _format) 
	{
		
		logger.info(localizer.x("FHR_I005: HistoryResourceOperation retrieves the hsitory of resource {0} by its id {1}", type, id));										
		
		try {
			QueryOption options = new QueryOption();
			Optional<List<R>> history = fhirService.history(type, id, options);
			if (history.isPresent()) {
				Bundle bundle = BundleBuilder.create(BundleType.HISTORY, history.get(), uriInfo, Status.OK);
				Bundle.BundleLinkComponent link = bundle.addLink();
				link.setRelation("self");
				link.setUrl(uriInfo.getRequestUri().toString());					
				String resourceInJson = parser.serialize(bundle);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1", uriInfo.getRequestUri().toString(), MimeType.APPLICATION_FHIR_JSON);
			}
			
			String error = id != null ? "invalid domain resource logical id '" + id + "'" : "resource search result in 0 results."; 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_FOUND, "", MimeType.APPLICATION_FHIR_JSON);				
						
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
