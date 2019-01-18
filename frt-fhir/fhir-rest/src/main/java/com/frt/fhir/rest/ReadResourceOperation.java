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
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.ValidationException;
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
public class ReadResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(ReadResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance();
		
	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	private FhirService fhirService;
	private StreamService streamService;
	
	public ReadResourceOperation() 
		throws RuntimeException {
		try {
			parser = new JsonParser();
			fhirService = new FhirService();
			streamService = StreamService.getInstance();
			if (streamService.enabled()) {
				logger.info(localizer.x("fhir streaming enabled"));		
			} else {
				logger.info(localizer.x("fhir streaming disabled"));
			}
		} catch (StreamServiceException ssex) {
			throw new RuntimeException(ssex);
		}		
	}	
	
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public <R extends DomainResource> Response read(@PathParam("type") final String type,
											  @PathParam("id") final String id,
											  @QueryParam("_format") @DefaultValue("json") final String _format,
											  @QueryParam("_summary") @DefaultValue("false") final String _summary) {
		try {
			logger.info(localizer.x("ReadResourceOperation reads a current resource"));		
			// Request
			
			// Response includes ETag with versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 
			OperationValidator.validateFormat(_format);
			OperationValidator.validateSummary(_summary);
			
			String message;
			if (streamService.enabled()) {
				logger.info(localizer.x("write 'read " + type + " operation' message to fhir stream"));
				streamService.write( "GET " + uriInfo.getPath(),  id);
				List<String> bodys = streamService.read();
				logger.info(localizer.x("read 'read " + type + " operation' message from fhir stream"));				
				message = bodys.get(0);
			} else {
				message = id;
			}			
			
			logger.info(localizer.x("read a " + type + " by its id[" + message + "] ..."));		
			Optional<R> found = fhirService.read(type, message);			
			if (found.isPresent()) {
				String resourceInJson = parser.serialize(found.get());      
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
			} else {
				String error = "invalid domain resource logical id '" + id + "'" ; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																								  OperationOutcome.IssueSeverity.ERROR, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_FOUND, "", MediaType.APPLICATION_JSON);				
			}
		} catch (ValidationException vx) {
			String error = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MediaType.APPLICATION_JSON);				
		}  catch (FhirServiceException | StreamServiceException ex) {
			String error = "\"service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.INTERNAL_SERVER_ERROR, "", MediaType.APPLICATION_JSON);							
		}
	}
	
}
