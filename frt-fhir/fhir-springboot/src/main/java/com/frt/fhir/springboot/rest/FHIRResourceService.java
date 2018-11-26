package com.frt.fhir.springboot.rest;

import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.ResourceOperationResponseBuilder;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.ValidationException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

@Component
public class FHIRResourceService {
	private static Logger logger = Logger.getLog(FHIRResourceService.class.getName());
	private static Localization localizer = Localization.getInstance();

	private FhirService fhirService;
	private JsonParser parser;
	
	public FHIRResourceService() {
		fhirService = new FhirService();
		parser = new JsonParser();
	}
	
	public <R extends DomainResource> String read(String type, String id, String _format, String _summary) {
		try {
			logger.info(localizer.x("ReadResourceOperation reads a current resource"));		
			// Request
			
			// Response includes ETag with versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 
			OperationValidator.validateFormat(_format);
			OperationValidator.validateSummary(_summary);
			Optional<R> found = fhirService.read(type, Long.valueOf(id));
			if (found.isPresent()) {
				return parser.serialize(found.get());
			} else {
				return parser.serialize(ResourceOperationResponseBuilder.buildOperationOutcome("invalid domain resource logical id '" + id + "'", 
						OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.PROCESSING));
			}
		} catch (ValidationException vx) {
			return parser.serialize(ResourceOperationResponseBuilder.buildOperationOutcome("invalid parameter: " + vx.getMessage(), 
				  OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.PROCESSING));
							
		}  catch (FhirServiceException fsx) {
			return parser.serialize(ResourceOperationResponseBuilder.buildOperationOutcome("\"service failure: " + fsx.getMessage(), 
				  OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.PROCESSING));
		}
	}

	public <R extends DomainResource> ResponseEntity<String> create(String type, String _format, final String body) {
		try {
			logger.info(localizer.x("FHIRResourceService.create(type, _format, body) creating a new resource"));
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
			R resource = parser.deserialize(type, body);	
			Optional<R> created = fhirService.create(type, resource);
			if (created.isPresent()) {
				String resourceInJson = parser.serialize(created.get());      
				return ResponseEntityBuilder.build(resourceInJson, HttpStatus.OK, "1.0", MediaType.APPLICATION_JSON);
			} else {		
				String message = "failed to create domain resource '" + type + "'"; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																								  OperationOutcome.IssueSeverity.ERROR, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResponseEntityBuilder.build(resourceInJson, HttpStatus.BAD_REQUEST, "", MediaType.APPLICATION_JSON);
			}
		} catch (ValidationException vx) {
			String message = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResponseEntityBuilder.build(resourceInJson, HttpStatus.BAD_REQUEST, "", MediaType.APPLICATION_JSON);				
		} catch (JsonFormatException jfx) {
			String message = "invalid resource: " + jfx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResponseEntityBuilder.build(resourceInJson, HttpStatus.NOT_ACCEPTABLE, "", MediaType.APPLICATION_JSON);							 
		} catch (FhirServiceException fsx) {								
			String message = "service failure: " + fsx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResponseEntityBuilder.build(resourceInJson, HttpStatus.NOT_ACCEPTABLE, "", MediaType.APPLICATION_JSON);							 			 			 
		}
	}

}