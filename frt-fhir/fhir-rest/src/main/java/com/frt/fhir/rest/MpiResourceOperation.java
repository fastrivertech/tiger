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

import com.frt.fhir.model.BundleBuilder;
import com.frt.fhir.mpi.MpiService;
import com.frt.fhir.mpi.MpiServiceImpl;
import com.frt.fhir.mpi.MpiServiceException;
import com.frt.fhir.mpi.parser.ParameterParser;
import com.frt.fhir.mpi.parser.ParameterParserException;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;
import com.frt.fhir.parser.JsonParser;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

@Path(ResourcePath.BASE_PATH)
@PermitAll
public class MpiResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(MpiResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser jsonParser;
	private ParameterParser paramParser;
	private MpiService mpiService;
	
	public MpiResourceOperation() {
		jsonParser = new JsonParser();	
		paramParser = new ParameterParser();
		mpiService = new MpiServiceImpl();
	}
	
  /**
	* Match domain resource with golden domain resources in the MPI database based on the MPI provider 
	* POST [base]/Patient/$match
	* @param body match parameters containing domain resources and match options
	* resource: patient which includes match fields
	* onlyCertainMatch: if the flag sets true, only matched record with highest score return
	* 				 	if the flag sets false, all matches records with match score return 
	* 					onlyCertainMatch is similar with oneExactMacth  
	* count: maximum number of records that is above match thresholds 
	* @param _format json or xml, default json and json supported
	* @return Bundle contains a set of matched golden domain resources or potentially matched records 
	* resource: a list of golden patients matching above threshold 
	* search:	match-grade extension with mode and score, mode specifies 'match' or 'potential match'
	* operation outcome: operation result code
	* @status 200 Matched
	* @status 201 No match and created
	* @status 400 Bad request
	* @status 422 Not processable resource 
	* @status 500 Internal server error
	*/	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_MATCH_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response match(@QueryParam("_format") @DefaultValue("json") final String _format,
						  final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "match"));		
		try {
			Parameters params = paramParser.deserialize(body);			
			Bundle bundle = mpiService.match(params);
			Bundle.BundleLinkComponent link = bundle.addLink();
			link.setRelation("self");
			link.setUrl(uriInfo.getRequestUri().toString());					
			String resourceInJson = jsonParser.serialize(bundle);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1", MimeType.APPLICATION_FHIR_JSON);			
		} catch (MpiServiceException | ParameterParserException ex) {
			String message = "Patient resource operation macth invalid"; 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);		
			String resourceInJson = jsonParser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);					
		}		
	}
	
	/**
	 * Probabilistic search golden domain resources based on search criteria and options
	 * @param body search parameters containing domain resource and search options
	 * resource: patient which include search fields (can be extended to support range search)
	 * option: vendor-specific such as different search types: BLOCKER-SEARCH/ALPHA-SEARCH/PHONETIC-SEARCH  
	 * threshold: match threshold 
	 * @param _format json or xml, default json and json supported  			
	 * @return Bundle contains a set of matched golden domain resources with score above the match threshold
	 * resource:  a list of patients matching above threshold
	 * search:	match-grade extension with mode and score, mode specifies 'match'
	 * @status 200 Matched
 	 * @status 400 Bad request
	 * @status 422 Not processable resource 
	 * @status 500 Internal server error
	 */	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_SEARCH_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response search(@QueryParam("_format") @DefaultValue("json") final String _format,
						   final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "search"));													
		String message = "Patient resource operation search not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}

	/**
	 * Merge a source domain resource with the sourceId to a target domain resource with the targetId.
	 * FHIR patient merge operation maps to HL7 ADT A40, identifier maps to PID-3 (Patient Identifier List) 
	 * FHIR Identifier	=> HL7 PID-3 CX
	 * 		use
	 * 		type	    =>	   Identifier Type code
	 * 		system 	    =>	   Assigning Authority 
	 * 		value	    =>	   id
	 * 		period
	 *  	assigner    =>	   Assigning Authority
	 * FHIR Merge operation is global merge or enterprise merge in MPI perspective, 
	 * plus local merge or domain resource merge. After operation, the subsumed domain resource is not visible. 
	 * @param body merge parameters
	 * 		  sourceId the surviving unique identifier of the source domain resource
	 * 		  targetId the subsumed unique identifier of the target domain resource
	 *        options list of vendor specific options such as CalculateOnly = true / false
	 * @return Bundle a set of merged domain resource and survived domain resource
	 * operation outcome: operation result code
	 * @status 201 Merged
	 * @status 202 Has been merged
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error
	 */	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_MERGE_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response merge(@QueryParam("_format") @DefaultValue("json") final String _format,
						  final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "merge"));												
		String message = "Patient resource operation merge not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}
	
	/**
	 * Unmerge a survived domain resource
	 * @param body unmerge parameters
	 * 		  resourceId the global unique identifier of the survived domain resource
	 * 	      options list of options vendor specific options such as CalculateOnly = true / false
	 * @param _format json or xml, default json and json supported
	 * @return Bundle a set of source domain resource and target domain resource
	 * operation outcome: operation result code
	 * @status 201 Unmerged
	 * @status 202 Has been unmerged
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error	 
	 */	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_UNMERGE_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response unmerge(@QueryParam("_format") @DefaultValue("json") final String _format,
						    final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "unmerge"));													
		String message = "Patient resource operation unmerge not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}

	/**
	 * Link two domain resources,  FHIR link operation is global merge or enterprise merge 
	 * in MPI perspective, but does not perform local merge or domain resource merge.
	 * @param body link parameters
	 * 		  sourceId the unique identifier of the source domain resource from the domain
	 * 		  targetId the unique identifier of the target domain resource from the domain
	 * 		  list of options vendor specific options such as CalculateOnly = true / false
	 * @param _format json or xml, default json and json supported
	 * @return Bundle a set of linked domain resources
	 * @status 201 Linked
	 * @status 202 Has been linked
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error
	 */	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_LINK_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response link(@QueryParam("_format") @DefaultValue("json") final String _format,
						 final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "link"));													
		String message = "Patient resource operation link not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}
	
	/**
	 * Un-merge a survived domain resource 
	 * @param body unlink parameters
	 * 		  resourceId the unique identifier of the survived domain resource from the domain
	 *        options list of options vendor specific options such as CalculateOnly = true / false
	 * @param _format json or xml, default json and json supported
	 * @return Bundle a set of merged domain resource and survived domain resource
	 * @status 201 Unlinked
	 * @status 202 Has been unlinked
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error
	 */	
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_UNLINK_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	public Response unlink(@QueryParam("_format") @DefaultValue("json") final String _format,
						   final String body) {		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "unlink"));													
		String message = "Patient resource operation unlink not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}
		
}

