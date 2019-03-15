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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;
import com.frt.dr.model.base.Patient;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.service.validation.IdValidatorException;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * CreateResourceInteraction class
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
	
	/**
	 * UpdateResourceOperation 
	 */
	public UpdateResourceOperation() {
		parser = new JsonParser();
		fhirService = new FhirService();
	}
	
	/**
	 * Create a new current version for an existing resource or create an initial version if no resource exists for the give Id
	 * PUT [base]/[type]/[id] {?_format=[mime-type]}
	 * @param type Resource type, e.g., Patient
	 * @param id Resource logical id, e.g., 1356
	 * @param _format json or xml, default josn and json supported
	 * @param body resource for updating
	 * @return resource updated
	 * @status 200 Updated Success
	 * @status 201 Created Success
	 * @status 404 Not found
	 * @status 400 Bad request
	 * @status 500 Internal server error
	 */	
	@PUT
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Update an existing Patient",
    tags = {ResourceOperation.UPDATE},
    responses = {
            @ApiResponse(
               content = @Content(mediaType = "application/fhir+json",
								  schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
            @ApiResponse(responseCode = "404", description = "Not found - Resource type not supported, or not a FHIR end-point"),
            @ApiResponse(responseCode = "500", description = "Server internal error") 
		}
	)
	public <R extends DomainResource> Response update(
			@Parameter(description = "FHIR Resource Type, the type of the resource to be updated, e.g. Patient", required = true) @PathParam("type") final String type,
			@Parameter(description = "FHIR Resource Id, it is the logical ID of the resource, e.g. Patient MRN", required = false) @PathParam("id") final String id,
			@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) @QueryParam("_format") @DefaultValue("json") final String _format, 
			@Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) final String body) {		
		logger.info(localizer.x("FHR_I006: UpdateResourceOperatio updates the resource {0} by its id {1}", type, id));										
		CacheService.getInstance().createCache();
		try {
			OperationValidator.validateFormat(_format);			
			R resource = parser.deserialize(type, body);	
			R updated = fhirService.update(type, id, resource);			

			String resourceInJson = parser.serialize(updated);
			
			Optional<NamedCache> cache = CacheService.getInstance().getCache();
			String action = (String)cache.get().get(NamedCache.ACTION_CODE);
			String location = uriInfo.getAbsolutePath().getPath() + "/_history/" + updated.getMeta().getVersionId();				
			if (action.equalsIgnoreCase("U")) {
				// resource updated
				return ResourceOperationResponseBuilder.build(resourceInJson, 
															  Status.OK, 
													          updated.getMeta().getVersionId(), 
													          location,
													          MimeType.APPLICATION_FHIR_JSON);				
			} else if (action.equalsIgnoreCase("C")) {
				// resource created 
				return ResourceOperationResponseBuilder.build(resourceInJson, 
															  Status.CREATED, 
															  updated.getMeta().getVersionId(), 
															  location,
															  MimeType.APPLICATION_FHIR_JSON);						
			} else {
				// resource no changed  
				return ResourceOperationResponseBuilder.build(resourceInJson, 
															  Status.NOT_MODIFIED, 
															  updated.getMeta().getVersionId(), 
															  location,
															  MimeType.APPLICATION_FHIR_JSON);									
			}								
		} catch (IdValidatorException iex) {
			String error = "invalid id: " + iex.getMessage(); 			
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);										
		} catch (OperationValidatorException vx) {
			String error = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);				
		} catch (JsonFormatException jfx) {
			String error = "invalid resource: " + jfx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 
		} catch (FhirServiceException ex) {								
			String error = "service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 			 			 
		} finally {
			CacheService.getInstance().destroyCache();			
		}
	}
}
