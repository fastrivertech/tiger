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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirConformanceService;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.stream.service.StreamServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class DeleteResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(DeleteResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;	
	private FhirService fhirService;

	public DeleteResourceOperation() { 
		parser = new JsonParser();		
		fhirService = new FhirService();
	}
	
	/**
	 * Delete a resource by its id
	 * DELETE [base]/frt-fhir-rest/1.0/[type]/[id]
	 * @param type resource type
	 * @param id resource logical Id
	 * @return status 200 OK
	 */
	@DELETE
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	public <R extends DomainResource> Response read(@PathParam("type") final String type,
												    @PathParam("id") final String id) {
		try {
			logger.info(localizer.x("FHR_I004: DeleteResourceOperation deletes a resource {0} by its id {1}", type, id));										
			OperationValidator.validateId(Optional.ofNullable(id));
			
			Optional<R> deleted = fhirService.delete(type, id);		

			if (deleted.isPresent()) {				
				String resourceInJson = parser.serialize(deleted.get());      
				return ResourceOperationResponseBuilder.build(resourceInJson, 
															  Status.OK, 
															  deleted.get().getMeta().getVersionId(), 
														      uriInfo.getAbsolutePath(),
														      MimeType.APPLICATION_FHIR_JSON);
			} else {
				String resourceInJson = "";
				return ResourceOperationResponseBuilder.build(resourceInJson, 
						  									  Status.NO_CONTENT, 
						  									  "", 
						  									  uriInfo.getAbsolutePath(),
						  									  MimeType.APPLICATION_FHIR_JSON);				
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
		} catch (FhirServiceException ex) {								
			String error = "service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 			 			 
		} 		
	}	
	
}
