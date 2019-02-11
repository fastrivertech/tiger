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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.dr.service.query.CompositeParameter;
import com.frt.dr.service.query.ResourceQueryUtils;
import com.frt.dr.service.query.QueryOption;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.fhir.model.BundleBuilder;
import com.frt.fhir.model.map.base.BaseMapper;
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
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class ReadResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(ReadResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
		
	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	private FhirService fhirService;
	private StreamService streamService;
	
	/**
	 * ReadResourceOperation Constructor
	 * @throws RuntimeException
	 */
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
	
	/**
	 * Get FHIR Resource by its Id and search string 
	 * GET [base]/frt-fhir-rest/1.0[type]{?[parameters]{&_format=[mime-type]}} 
	 * GET [base]/frt-fhir-rest/1.0/Patient?_id=[id] or [base]/frt-fhir-rest/1.0/Patient/given=eve
	 * @param type Resource type, e.g., Patient
	 * @param _id Resource logical id, e.g., 1356
	 * @param _format json or xml, json supported
	 * @param _summary true for retrieving summary of Resource, false for retrieving entire Resource; default false
	 * @param uriInfo search string, [base]/Patient?given=eve, [base]/Patient?given=Allison&gender=female    
	 * @return FHIR Resource retrieved
	 * @status 200 Retrieved Success
     * @status 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
     * Response includes ETag with versionId and Last-Modified
     * 410 Gone - Resource deleted
     * 404 Not Found - Unknown resource 
	 */
	@GET
	@Path(ResourcePath.TYPE_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	public <R extends DomainResource> Response read(@PathParam("type") final String type, 
												    @QueryParam("_id") String _id,
												    @QueryParam("_format") @DefaultValue("json") final String _format,
												    @QueryParam("_summary") @DefaultValue("false") final String _summary,
													@Context UriInfo uriInfo) {		
		if (uriInfo.getQueryParameters().get("_id") != null ) {
			_id = uriInfo.getQueryParameters().get("_id").get(0);
		}
		return readResource(type, _id, _format, _summary, uriInfo);
	}
	
	/**
	 * Get FHIR Resource by its Id
	 * GET [base]/frt-fhir-rest/1.0/[type]/[id] {?_format=[mime-type]} {&_format=[mime-type]}
	 * @param type Resource type, e.g., Patient
	 * @param id Resource logical id, e.g., 1356
	 * @param _format json or xml, default josn and json supported
	 * @param _summary true for retrieving summary of Resource, false for retrieving entire Resource; default false
	 * @return FHIR Resource retrieved
	 * @status 200 Retrieved Success
     * @status 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
     * Response includes ETag with versionId and Last-Modified
     * 410 Gone - Resource deleted
     * 404 Not Found - Unknown resource 
	 */	
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	public <R extends DomainResource> Response read(@PathParam("type") final String type,
											  		@PathParam("id") final String id, 
												    @QueryParam("_format") @DefaultValue("json") final String _format,
												    @QueryParam("_summary") @DefaultValue("false") final String _summary) {		
		return readResource(type, id, _format, _summary, null);
	}

	private <R extends DomainResource> Response readResource(String type, 
															 String id, 
															 String format, 
															 String summary, 
															 UriInfo uriInfo) {
		try {			
			//ToDo: more validations and more concise validation implementation 			
			OperationValidator.validateId(Optional.ofNullable(id));
			OperationValidator.validateFormat(format);
			OperationValidator.validateSummary(summary);

			//ToDo: add more options
			QueryOption options = new QueryOption();
			options.setSummary(Boolean.parseBoolean(summary));
			options.setFormat(QueryOption.MediaType.value(format));
			
			QueryCriteria criterias = new QueryCriteria();
			if (uriInfo != null) {
				criterias.setParams(uriInfo.getQueryParameters());	
			}			
						
			// Response includes ETag with versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 

			if (streamService.enabled()) {
				logger.info(localizer.x("write [" + type + "] ReadOperation message to fhir stream"));
			    streamService.write( "GET [base]/" + type + "/" + id,  id);
				List<String> bodys = streamService.read();
				logger.info(localizer.x("read [" + type + "] ReadOperation message from fhir stream"));								
			} 		
			
			if (id != null) {				
				logger.info(localizer.x("FHR_I001: ReadResourceOperation reads a current resource by its logical Id {0}", id));				
				Optional<R> found = fhirService.read(type, id, options);
				if (found.isPresent()) {
					String resourceInJson = parser.serialize(found.get());      
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
				}
			} else {
				logger.info(localizer.x("FHR_I002: ReadResourceOperation reads a current resource by criteria {0}", criterias.getParams().toString()));										
				Optional<List<R>> resources = fhirService.read(type, criterias, options);
				if (resources.isPresent()) {
					Bundle bundle = BundleBuilder.create(resources.get(), uriInfo.getAbsolutePath().toString());
					Bundle.BundleLinkComponent link = bundle.addLink();
					link.setRelation("self");
					link.setUrl(uriInfo.getRequestUri().toString());					
					String resourceInJson = parser.serialize(bundle);
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
				}
			}

			String error = id != null ? "invalid domain resource logical id '" + id + "'" : "resource search result in 0 results."; 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_FOUND, "", MediaType.APPLICATION_JSON);				
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
