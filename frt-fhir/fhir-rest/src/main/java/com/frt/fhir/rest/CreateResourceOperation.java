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
import java.util.UUID;
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
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.stream.service.StreamService;
import com.frt.stream.service.StreamServiceException;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class CreateResourceOperation extends ResourceOperation {	
	private static Logger logger = Logger.getLog(CreateResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
	
    @Context
    private UriInfo uriInfo;

	private JsonParser parser;
	private FhirService fhirService;
	private StreamService streamService;
	
	public CreateResourceOperation() 
		throws RuntimeException {
		try {
			parser = new JsonParser();
			fhirService = new FhirService();
			streamService = StreamService.getInstance() ;
			if (streamService.enabled()) {
				logger.info(localizer.x("fhir streaming enabled"));		
			} else {
				logger.info(localizer.x("fhir streaming disabled"));
			}			
		} catch (StreamServiceException ssex) {
			throw new RuntimeException(ssex);
		}
	}
	
	/**
	 * Create a FHIR Resource
	 * POST [base]/frt-fhir-rest/1.0/[type] {?_format=[mime-type]}
	 * @param type Resource type, e.g., Patient
	 * @param _format json or xml, default josn and json supported
	 * @param body FHIR Resource
	 * @return FHIR Resource created
	 * @status 201 Created Success
     * @status 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
	 */
	@POST
	@Path(ResourcePath.TYPE_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
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
			OperationValidator.validateFormat(_format);
			
			String message;
			if (streamService.enabled()) {
				logger.info(localizer.x("write [" + type + "] CreateOperation message to fhir stream"));				
				streamService.write("POST [base]/" + type, body);
				List<String> bodys = streamService.read();
				logger.info(localizer.x("read [" + type + "] CreateOperation message from fhir stream"));				
				message = bodys.get(0);
			} else {
				message = body;
			}
			
			logger.info(localizer.x("create a new " + type + " ..."));										
			R resource = parser.deserialize(type, message);
			
			if (resource.getId() == null) {
				resource.setId(UUID.randomUUID().toString());
			}
			
			Optional<R> created = fhirService.create(type, resource);
			if (created.isPresent()) {
				String resourceInJson = parser.serialize(created.get());      
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.CREATED, "1.0", MimeType.APPLICATION_FHIR_JSON);
			} else {		
				String error = "failed to create domain resource '" + type + "'"; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																								  OperationOutcome.IssueSeverity.ERROR, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);
			}
			
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
		} catch (FhirServiceException | StreamServiceException ex) {								
			String error = "service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 			 			 
		} 
	}
	
}
