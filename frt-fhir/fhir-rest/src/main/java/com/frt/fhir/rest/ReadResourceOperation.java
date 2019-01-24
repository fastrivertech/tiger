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
import java.util.Set;

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
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;

import com.frt.fhir.model.base.BaseMapper;
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
	
	/**
	 * Get FHIR Resource by its Id
	 * GET [base]/frt-fhir-rest/1.0/Patient?_id=[id]
	 * @param type Resource type, e.g., Patient
	 * @param _id Resource logical id, e.g., 1356
	 * @param _format json or xml, json supported
	 * @param _summary true for retrieving summary of Resource, false for retrieving entire Resource; default false
	 * @return FHIR Resource retrieved
	 * @status 200 Retrieved Success
     * @status 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
	 */
	@GET
	@Path(ResourcePath.TYPE_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public <R extends DomainResource> Response read(@PathParam("type") final String type,
			  @PathParam("_id") final String _id,
			  @QueryParam("_format") @DefaultValue("json") final String _format,
			  @QueryParam("_summary") @DefaultValue("false") final String _summary,
			  @Context UriInfo uriInfo) {
		return readResource(type, _id, _format, _summary, uriInfo);
	}
	
	/**
	 * Get FHIR Resource by its Id
	 * GET [base]/frt-fhir-rest/1.0/Patient/[id]
	 * @param type Resource type, e.g., Patient
	 * @param id Resource logical id, e.g., 1356
	 * @param _format json or xml, default josn and json supported
	 * @param _summary true for retrieving summary of Resource, false for retrieving entire Resource; default false
	 * @return FHIR Resource retrieved
	 * @status 200 Retrieved Success
     * @status 400 Bad Request - Resource could not be parsed or failed basic FHIR validation rules
	 */	
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public <R extends DomainResource> Response read(@PathParam("type") final String type,
											  @PathParam("id") final String id,
											  @QueryParam("_format") @DefaultValue("json") final String _format,
											  @QueryParam("_summary") @DefaultValue("false") final String _summary) {
		
		return readResource(type, id, _format, _summary, null);
	}

	private <R extends DomainResource> Response readResource(String type, String id, String _format, String _summary, UriInfo uriInfo) {
		try {
			logger.info(localizer.x("ReadResourceOperation reads a current resource"));		
			// Request
			Map<String, String> parameters = null;
			MultivaluedMap params = uriInfo!=null?uriInfo.getQueryParameters():null;
			if (params!=null) {
				parameters = trimParams(params);
			}
			// Response includes ETag with versionId and Last-Modified
			// 410 Gone - Resource deleted 
			// 404 Not Found - Unknown resource 
			OperationValidator.validateFormat(_format);
			OperationValidator.validateSummary(_summary);
			// for now, id and params can not be both null/empty
			// either id not null - a fetch by id, might with restriction expressed in params
			// or params not empty - a search, might be with id in params : e.g. id=909901
			OperationValidator.validateParameters(id, parameters);
			
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
			
			String resourceInJson = null;
			if (message!=null) {
				logger.info(localizer.x("read a " + type + " by its id[" + message + "] ..."));		
				Optional<R> found = fhirService.read(type, message);
				if (found.isPresent()) {
					resourceInJson = parser.serialize(found.get());      
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
				}
			}
			else {
				logger.info(localizer.x("search resource of type: " + type + " with parameters [" + params.toString() + "] ..."));		
				Optional<List<R>> found = fhirService.read(type, parameters);
				if (found.isPresent()) {
					StringBuilder sb = new StringBuilder();
					List<R> rsl = found.get();
					sb.append(BaseMapper.ARRAY_BEGIN);
					boolean first = true;
					for (R r: rsl) {
						if (!first) {
							sb.append(BaseMapper.VAL_DEL);
						}
						else {
							first = false;
						}
						sb.append(parser.serialize(r));
					}
					sb.append(BaseMapper.ARRAY_END);
					return ResourceOperationResponseBuilder.build(sb.toString(), Status.OK, "1.0", MediaType.APPLICATION_JSON);
				}
			}

			// report error
			String error = message!=null?"invalid domain resource logical id '" + id + "'" : "resource search result in 0 results."; 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			resourceInJson = parser.serialize(outcome);
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

	private Map<String, String> trimParams(MultivaluedMap params) {
		Map<String, String> ret = new HashMap<String, String>();
		Iterator pit = params.keySet().iterator();
		while (pit.hasNext()) {
			Object key = pit.next();
			Object value = params.get(key);
			if (value instanceof LinkedList<?>) {
				// debugging shows value could be LinkedList
				ret.put(key.toString(), ((LinkedList<?>)value).get(0).toString());
			}
			else {
				// assume it is string or primitive
				ret.put(key.toString(), value.toString());
			}
		}
		return ret;
	}
}
