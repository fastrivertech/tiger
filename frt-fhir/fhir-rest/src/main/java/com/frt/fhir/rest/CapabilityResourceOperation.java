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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import org.hl7.fhir.r4.model.CapabilityStatement;
import com.frt.dr.model.base.Patient;
import com.frt.fhir.parser.JsonParser;
import com.frt.fhir.service.FhirConformanceService;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;

/**
 * CreateResourceInteraction class
 * @author cqye
 */
@OpenAPIDefinition(
        info = @Info(
        			 title = "Fast River Technologies FHIR Service API",
            		 version = "",
            		 description = "FHIR Compliant and Streaming-Enabled REST API",
            		 contact = @Contact(url = "http://fastrivertech.com", name = "Fast River Tech Inc.")
        			 ),
        /*
        tags = {
        		@Tag(name = "Fast River FHIR API", 
        			 description = "Enabling Big Data Based AI Streaming Analytics In Healthcare", 
        			 externalDocs = @ExternalDocumentation(description = "Fast River Technologies", url="http://fastrivertech.com/")),
            	@Tag(name = "HL7 FHIR 4.0", 
            		 description = "HL7 FHIR 4.0", 
            		 externalDocs = @ExternalDocumentation(description = "FHIR 4.0 Spec", url="https://www.hl7.org/fhir/"))
               },
        externalDocs = @ExternalDocumentation(description = "HL7 FHIR Overview"),
        security = {
        			@SecurityRequirement(name = "req 1", scopes = {"a", "b"}),
                	@SecurityRequirement(name = "req 2", scopes = {"b", "c"})
                   },
        */
        servers = {
        			@Server(description = "FHIR Resource Server: FHIR Resource Services On Premises or On Cloud",
                	 	    url = "http://{host}:{port}/frt-fhir-rest/",
                		    variables = {
                		    			 @ServerVariable(name = "host", 
                		    					 		 description = "Host name where FHIR Resource Services Deployed", 
                		    					 		 defaultValue = "localhost",
                		    					 		 allowableValues = {"localhost", "ec2-54-202-187-87.us-west-2.compute.amazonaws.com"}),
                				   	     @ServerVariable(name = "port", 
                				   	     				 description = "Port where FHIR Resource Services Deployed", 
                				   	     				 defaultValue = "8080", 
                				   	     				 allowableValues = {"8080", "8088"})
                  						}
        				   )
        		  }
)

@Path(ResourcePath.BASE_PATH)
@PermitAll
public class CapabilityResourceOperation extends ResourceOperation {	
	private static Logger logger = Logger.getLog(CapabilityResourceOperation.class.getName());	
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	@Context
	private UriInfo uriInfo;
	
	private JsonParser parser;
	private FhirConformanceService conformance;
	
	public CapabilityResourceOperation() { 
		parser = new JsonParser();
		conformance = new FhirConformanceService();
	}
	
	/**
	 * Retrieve the FHIR server capability statement resource
	 * GET [base]/frt-fhir-rest/1.0/metadata{?mode=[mode]} {&_format=[mime-type]}
	 * @param mode return information mode: full, normative or terminology 
	 * @param _format mime-type json or xml, default josn and json supported
	 * @return the FHIR server capability statement resource
	 * @status 200 Request succeeded
	 * @status 500 Internal server error
	 */
	@GET
	@Path(ResourcePath.METADATA_PATH)
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Get CapabilityStatement", 
	           description= "Get CapabilityStatement Resource of the server", 
			   tags = {ResourceOperation.READ},
    		   responses = {
    				   	    @ApiResponse(description = "FHIR DomainResource: CapabilityStatement",
                    		  		     content = @Content(mediaType = "application/fhir+json",
                    		  		     schema = @Schema(implementation = Patient.class))),
    				   	    @ApiResponse(responseCode = "200", 
    				   	    			 description = "Resource (CapabilityStatement) retrieved successfully"),
    				   	    @ApiResponse(responseCode = "500", 
    				   	    			 description = "Internal server error")
    				   	    }
			   )
	public Response read(@Parameter(description = "Mode: return information mode: full, normative or terminology", required = false) 
						 @QueryParam("mode") @DefaultValue("normative") final String mode,
						 @Parameter(description = "FHIR Resource format, indicate the format of the returned resource (json/xml))", required = false) 
					     @QueryParam("_format") @DefaultValue("json") final String _format) {	
		
		logger.info(localizer.x("FHR_I003: CapabilityResourceOperation reads the capability statement by mode {0}", mode));		
		CapabilityStatement cs = conformance.getCapabilityStatement();
		String resourceInJson = parser.serialize(cs);
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1", MimeType.APPLICATION_FHIR_JSON);
	}
	
}
