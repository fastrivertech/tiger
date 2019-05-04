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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.OperationOutcome;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;
import com.frt.dr.model.base.Patient;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.service.query.QueryOption;
import com.frt.fhir.model.BundleBuilder;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.stream.service.StreamService;
import com.frt.stream.service.StreamServiceException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * SearchResourceOperation class
 * 
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class SearchResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(SearchResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
		
	@Context
	private UriInfo uriInfo;

	private JsonParser parser;
	private FhirService fhirService;
	private StreamService streamService;
	
	/**
	 * SearchResourceOperation Constructor
	 * @throws RuntimeException
	 */
	public SearchResourceOperation() 
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
	 * GET [base]/frt-fhir-rest/1.0/[type]{?[parameters]{&_format=[mime-type]}} 
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
	@POST
	@Path(ResourcePath.TYPE_PATH + ResourcePath.SEARCH_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Search Patient", 
			   description= "Search Patient Resource Using Query Parameters",
			   tags = {ResourceOperation.READ},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Patient that satisfied query parameters",
										 content = @Content(mediaType = "application/fhir+json",
				                         schema = @Schema(implementation = Patient.class))),
				            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "410", description = "Gone - Resource deleted"),
				            @ApiResponse(responseCode = "404", description = "Not found - Unknown resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
							}
			   )
	public <R extends DomainResource> Response read(@Parameter(description = "FHIR Resource Type, the type of the resource to be retrieved, e.g. Patient", required = true) 
													@PathParam("type") final String type, 
													@Parameter(description = "FHIR Resource Id, it is the logical ID of the resource, e.g. Patient MRN", required = true) 
													@PathParam("id") final String _id, 
													@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
													@QueryParam("_format") @DefaultValue("json") final String _format,
													@Parameter(description = "_summary parameter requests the server to return a subset of the resource", required = false) 
													@QueryParam("_summary") @DefaultValue("false") final String _summary,		
													@Parameter(description = "Map of query parameters", required = true) @Context UriInfo uInfo) {
		
		String id = _id;
		if (uriInfo.getQueryParameters().get("_id") != null ) {
			id = uriInfo.getQueryParameters().get("_id").get(0);
		}
		return readResource(type, id, _format, _summary, uInfo);
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
     * @status 400 Bad request - Resource could not be parsed or failed basic FHIR validation rules
     * @status 410 Gone - Resource deleted
     * @status 404 Not found - Unknown resource 
	 * @status 400 Bad request
	 * @status 500 Internal server error
	 */	
	@POST
	@Path(ResourcePath.TYPE_PATH + ResourcePath.SEARCH_PATH + ResourcePath.ID_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Get Patient", 
			   description= "Get Patient Resource By Logical ID",
			   tags = {ResourceOperation.READ},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Patient",
										 content = @Content(mediaType = "application/fhir+json",
				                         schema = @Schema(implementation = Patient.class))),
				            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "410", description = "Gone - Resource deleted"),
				            @ApiResponse(responseCode = "404", description = "Not found - Unknown resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
        					}
			)			
	public <R extends DomainResource> Response read(@Parameter(description = "FHIR Resource Type, the type of the resource to be retrieved, e.g. Patient", required = true) 
													@PathParam("type") final String type,
													@Parameter(description = "FHIR Resource Id, it is the logical ID of the resource, e.g. Patient MRN", required = true) 
													@PathParam("id") final String id, 
													@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
													@QueryParam("_format") @DefaultValue("json") final String _format,
													@Parameter(description = "_summary parameter requests the server to return a subset of the resource", required = false) 
													@QueryParam("_summary") @DefaultValue("false") final String _summary) {		
		return readResource(type, id, _format, _summary, null);
	}

	private <R extends DomainResource> Response readResource(String type, 
															 String id, 
															 String format, 
															 String summary, 
															 UriInfo uInfo) {
		try {			
			CacheService.getInstance().createCache();
			
			//ToDo: more validations and more concise validation implementation 			
			OperationValidator.validateFormat(format);
			OperationValidator.validateSummary(summary);
			OperationValidator.validateQueryParameters(Optional.ofNullable(id), uInfo);
			
			//ToDo: add more options
			QueryOption options = new QueryOption();
			options.setSummary(Boolean.parseBoolean(summary));
			options.setFormat(QueryOption.MediaType.value(format));
			
			QueryCriteria criterias = new QueryCriteria();
			if (uInfo != null) {
				criterias.setParams(uInfo.getQueryParameters());	
			}			
						
			// Response includes ETag with versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 

			if (streamService.enabled()) {
				logger.info(localizer.x("write [" + type + "] SearchOperation message to fhir stream"));
			    streamService.write( "GET [base]/" + type + "/" + id,  id);
				List<String> bodys = streamService.read();
				logger.info(localizer.x("read [" + type + "] SearchOperation message from fhir stream"));								
			} 		
			
			if (id != null) {				
				logger.info(localizer.x("FHR_I001: SearchResourceOperation reads a current resource by its logical Id {0}", id));				
				Optional<R> found = fhirService.read(type, id, options);
				if (found.isPresent()) {
					String resourceInJson = parser.serialize(found.get());  
					String location = uriInfo.getAbsolutePath().getPath() + "/_history/" + found.get().getMeta().getVersionId();
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1", location, MimeType.APPLICATION_FHIR_JSON);
				}
			} else {
				logger.info(localizer.x("FHR_I002: SearchResourceOperation reads a current resource by criteria {0}", criterias.getParams().toString()));										
				Optional<List<R>> resources = fhirService.read(type, criterias, options);
				if (resources.isPresent()) {
					Bundle bundle = BundleBuilder.create(Bundle.BundleType.SEARCHSET, resources.get(), uriInfo, Status.OK);
					Bundle.BundleLinkComponent link = bundle.addLink();
					link.setRelation("self");
					link.setUrl(uriInfo.getRequestUri().toString());					
					String resourceInJson = parser.serialize(bundle);
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MimeType.APPLICATION_FHIR_JSON);
				} 
			}

			boolean deleted = false;
			Optional<NamedCache> cache = CacheService.getInstance().getCache();			
			if (cache.isPresent() &&
				cache.get().get(NamedCache.ACTION_CODE) != null) {
				String action = (String)cache.get().get(NamedCache.ACTION_CODE);
				if (action.equalsIgnoreCase("D")) {
					deleted = true;
				}	
			}		
			if (deleted) {
				String message = "domain resource logical id '" + id + "' deleted."; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																								  OperationOutcome.IssueSeverity.INFORMATION, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.GONE, "", MimeType.APPLICATION_FHIR_JSON);				
			} else {
				String error = id != null ? "invalid domain resource logical id '" + id + "'" : "resource search result in 0 results."; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																								  OperationOutcome.IssueSeverity.ERROR, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = parser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_FOUND, "", MimeType.APPLICATION_FHIR_JSON);
			}
		} catch (OperationValidatorException vx) {
			String error = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);				
		}  catch (FhirServiceException | StreamServiceException ex) {
			String error = "\"service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = parser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.INTERNAL_SERVER_ERROR, "", MimeType.APPLICATION_FHIR_JSON);							
		} finally {
			CacheService.getInstance().destroyCache();						
		}
		
	}

}
