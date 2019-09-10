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
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.OperationOutcome;
import com.frt.fhir.mpi.MpiService;
import com.frt.fhir.mpi.MpiServiceException;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;
import com.frt.fhir.mpi.MpiValidationException;
import com.frt.fhir.mpi.MpiServiceImpl;
import com.frt.fhir.mpi.parser.ParameterParser;
import com.frt.fhir.mpi.parser.ParameterParserException;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.rest.validation.MpiOperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.fhir.service.FhirService;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * MpiResourceOperation class
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class MpiResourceOperation extends ResourceOperation {
	private static Logger logger = Logger.getLog(MpiResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser jsonParser;
	private ParameterParser paramParser;
	private FhirService fhirService;
	private MpiService mpiService;
	
	public MpiResourceOperation() {
		jsonParser = new JsonParser();	
		paramParser = new ParameterParser();
		fhirService = new FhirService();
		mpiService = new MpiServiceImpl(fhirService);
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
	@Operation(summary = "Match Domain Resource", 
			   description= "Match domain resource with golden domain resources in the MPI database based on the MPI provider.",
			   tags = {ResourceOperation.MPI},
			   responses = {
					   		@ApiResponse(description = "FHIR DomainResource: Bundle Contains A Set of Matched Golden Domain Resources or Potentially Matched Records",
					   					 content = @Content(mediaType = "application/fhir+json",
					   					 schema = @Schema(implementation = com.frt.dr.model.base.Patient.class))),
				            @ApiResponse(responseCode = "200", description = "Matched: Resources Matched Successfully"),
				            @ApiResponse(responseCode = "201", description = "No Match and Created"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "422", description = "Not Processable Resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
            				}
			 )
	public Response match(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
						  @QueryParam("_format") @DefaultValue("json") final String _format,
						  @Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
						 final String body) {
		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "match"));		
		try {
			com.frt.fhir.mpi.resource.Parameters params = paramParser.deserialize(body);			
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
	@Operation(summary = "Probabilistic search golden domain resources based on search criteria and options", 
			   description= "Probabilistic search golden domain resources based on search criteria and options.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle contains a set of matched golden domain resources with score above the match threshold",
				                         content = @Content(mediaType = "application/fhir+json")),
				            @ApiResponse(responseCode = "200", description = "Matched"),
				            @ApiResponse(responseCode = "400", description = "Bad request"),
				            @ApiResponse(responseCode = "422", description = "Not processable resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
            				}
			  )
	public Response search(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
						   @QueryParam("_format") @DefaultValue("json") final String _format,
						   @Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
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
	@SuppressWarnings("rawtypes")
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_MERGE_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Operation(summary = "Merge Two Domain Resource (Source & Target)", 
			   description= "Merge a source domain resource with the sourceId to a target domain resource with the targetId..",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle a Set of Merged Domain Resource and Survived Domain Resource",
				                         content = @Content(mediaType = "application/fhir+json",
				                         schema = @Schema(implementation = com.frt.dr.model.base.Patient.class))),
				            @ApiResponse(responseCode = "201", description = "Merged: Resources Merged Successfully"),
				            @ApiResponse(responseCode = "202", description = "Has Been Merged"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "404", description = "Not Found Resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
						  }
			   )
	public <R extends DomainResource> Response merge(@QueryParam("_format") @DefaultValue("json") final String _format,
						  						     @Parameter(description = "FHIR Parameters resource specifying source and target patients to be merged", required = false) final String body) {
		try {
			logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "merge"));														
			CacheService.getInstance().createCache();

			org.hl7.fhir.r4.model.Parameters parameters = jsonParser.deserialize("Parameters", body);			
			MpiOperationValidator.validateFormat(_format);
			MpiOperationValidator.validateParameters(parameters);			
			
			R merged = (R)mpiService.merge(parameters);		
			
			Optional<NamedCache<String, String>> cache = CacheService.getInstance().getCache();
			String action = (String)cache.get().get(NamedCache.ACTION_CODE);
			
			if (action.equalsIgnoreCase("Merged")) {		
					String resourceInJson = jsonParser.serialize(merged);      				
					String location = uriInfo.getAbsolutePath().getPath() + "_history/" + merged.getMeta().getVersionId();
					return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, 
																  merged.getMeta().getVersionId(), 
															      location, 
															      MimeType.APPLICATION_FHIR_JSON);
			} else if (action.equalsIgnoreCase("HasMerged")) {
				String resourceInJson = jsonParser.serialize(merged);      				
				String location = uriInfo.getAbsolutePath().getPath() + "_history/" + merged.getMeta().getVersionId();
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.ACCEPTED, 
															  merged.getMeta().getVersionId(), 
														      location, 
														      MimeType.APPLICATION_FHIR_JSON);				
			} else {
				String error = "failed to merge '" + body + "'"; 
				OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																								  OperationOutcome.IssueSeverity.ERROR, 
																								  OperationOutcome.IssueType.PROCESSING);
				String resourceInJson = jsonParser.serialize(outcome);
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);
			}		
		} catch (OperationValidatorException | MpiValidationException vx) {
			String error = "invalid parameter: " + vx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = jsonParser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.BAD_REQUEST, "", MimeType.APPLICATION_FHIR_JSON);				
		} catch (JsonFormatException fx) {
			String error = "invalid resource: " + fx.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = jsonParser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 
		} catch (MpiServiceException | UnsupportedOperationException ex) {								
			String error = "service failure: " + ex.getMessage(); 
			OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(error, 
																							  OperationOutcome.IssueSeverity.ERROR, 
																							  OperationOutcome.IssueType.PROCESSING);
			String resourceInJson = jsonParser.serialize(outcome);
			return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);							 			 			 
		} finally {
			CacheService.getInstance().destroyCache();						
		}
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
	@Operation(summary = "Unmerge A Survived Domain Resource", 
			   description= "Unmerge A Survived Domain Resource.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle of A Set Of Source Domain Resource And target Domain Resource",
				            		     content = @Content(mediaType = "application/fhir+json",
				                         schema = @Schema(implementation = com.frt.dr.model.base.Patient.class))),
				            @ApiResponse(responseCode = "201", description = "Unmerged"),
				            @ApiResponse(responseCode = "202", description = "Has Been Merged"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "404", description = "Not found - Unknown resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
            				}
			)
	public Response unmerge(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
							@QueryParam("_format") @DefaultValue("json") final String _format,
							@Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
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
	@Operation(summary = "Link Two Domain Resources", 
			   description= "Link two domain resources,  FHIR link operation is global merge or enterprise merge \r\n in MPI perspective, but does not perform local merge or domain resource merge.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle of A Set of Linked Domain Resources",
				            		     content = @Content(mediaType = "application/fhir+json",
				                         schema = @Schema(implementation = com.frt.dr.model.base.Patient.class))),
				            @ApiResponse(responseCode = "201", description = "Linked: Resources Linked Successfully"),
				            @ApiResponse(responseCode = "202", description = "Has Been Linked, Resources Has Been Linked"),
				            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
				            @ApiResponse(responseCode = "404", description = "Not found - Unknown resource"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
            				}
			 )
	public Response link(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
 						 @QueryParam("_format") @DefaultValue("json") final String _format,
						 @Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
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
	@Operation(summary = "Un-merge a survived domain resource", 
			   description= "Un-merge a survived domain resource.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle a set of merged domain resource and survived domain resource",
				            			 content = @Content(mediaType = "application/fhir+json")),
				            @ApiResponse(responseCode = "201", description = "Unlinked"),
				            @ApiResponse(responseCode = "202", description = "Has Been Unlinked"),
				            @ApiResponse(responseCode = "400", description = "Bad request"),
				            @ApiResponse(responseCode = "404", description = "Not found"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
							}
			 )
	public Response unlink(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
						   @QueryParam("_format") @DefaultValue("json") final String _format,
						   @Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
						   final String body) {		
		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "unlink"));													
		String message = "Patient resource operation unlink not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}
	

   /**
	 * Retrieve potential duplicates for the given patient identifier
	 * @param identifer resource identifier
	 * @param _format json or xml, default json and json supported
	 * @return Bundle a set of potential duplicate patient resources 
	 * @status 200 Success
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error
	 */	
	@GET
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_PD_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})	
	@Operation(summary = "Retrieve potential duplicates for the given patient identifier", 
			   description= "Retrieve potential duplicates for the given patient identifier.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle a set of potential duplicate patient resources",
				                         content = @Content(mediaType = "application/fhir+json")),
				            @ApiResponse(responseCode = "200", description = "Success"),
				            @ApiResponse(responseCode = "400", description = "Bad request"),
				            @ApiResponse(responseCode = "404", description = "Not found"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
							}
			 )
	public Response search_pd(@Parameter(description = "FHIR Resource Id, it is the logical ID of the resource, e.g. Patient MRN", required = true) 
 							  @QueryParam("identifier") final String identifier,	
							  @Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
							  @QueryParam("_format") @DefaultValue("json") final String _format) {
		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "search potentials"));													
		String message = "Patient resource operation search potential duplicates not implemented yet"; 
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message, 
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);		
	}

	/**
	 * Resolve two potential duplicates
	 * @param body parameters
	 * 		  identifier1 the unique identifier of potential duplicate domain resource
	 * 		  identifier2 the unique identifier of potential duplicate domain resource
	 * 		  list of options vendor specific options: action: merge or separate  	 
	 * @param _format json or xml, default json and json supported
	 * @return Bundle a set of potential duplicate patient resources
	 * @status 201 Resolved
	 * @status 400 Bad request
	 * @status 404 Not found
	 * @status 500 Internal server error
	 */
	@POST
	@Path(ResourcePath.PATIENT_PATH + ResourcePath.MPI_PD_PATH)
	@Consumes({ MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON })
	@Produces({ MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON })
	@Operation(summary = "Resolve two potential duplicates", 
			   description= "Resolve two potential duplicates.",
			   tags = {ResourceOperation.MPI},
			   responses = {
				            @ApiResponse(description = "FHIR DomainResource: Bundle a Set of Potential Duplicate Patient Resources",
				                         content = @Content(mediaType = "application/fhir+json")),
				            @ApiResponse(responseCode = "201", description = "Resolved"),
				            @ApiResponse(responseCode = "400", description = "Bad request"),
				            @ApiResponse(responseCode = "404", description = "Not Found"),
				            @ApiResponse(responseCode = "500", description = "Internal server error")
            				}
			)
	public Response resolve_pd(@Parameter(description = "FHIR Resource format, indicate the format of the returned resource", required = false) 
							   @QueryParam("_format") @DefaultValue("json") final String _format,
							   @Parameter(description = "FHIR Resource in json / xml string, json supported", required = true) 
							   final String body) {
		
		logger.info(localizer.x("FHR_I010: ExecutionResourceOperation executes the mpi {0} command", "resolve potentials"));
		String message = "Patient resource operation resolve potential duplicates not implemented yet";
		OperationOutcome outcome = ResourceOperationResponseBuilder.buildOperationOutcome(message,
																						  OperationOutcome.IssueSeverity.INFORMATION, 
																						  OperationOutcome.IssueType.INFORMATIONAL);
		String resourceInJson = jsonParser.serialize(outcome);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.NOT_ACCEPTABLE, "", MimeType.APPLICATION_FHIR_JSON);
	}

}

