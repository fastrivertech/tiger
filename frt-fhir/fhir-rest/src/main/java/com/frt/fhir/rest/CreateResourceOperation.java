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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.annotation.security.PermitAll;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import com.frt.fhir.parser.JsonParser;
import com.frt.dr.model.base.Patient;
import com.frt.fhir.parser.JsonFormatException;
import com.frt.fhir.rest.validation.OperationValidator;
import com.frt.fhir.rest.validation.OperationValidatorException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.headers.*;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.servers.*;


import com.frt.fhir.service.FhirService;
import com.frt.fhir.service.FhirServiceException;
import com.frt.stream.service.StreamService;
import com.frt.stream.service.StreamServiceException;

/**
 * CreateResourceInteraction class
 * 
 * @author cqye
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Fast River Technologies FHIR REST API",
                version = "1.0",
                description = "FHIR Compliant and Streaming-Enabled REST API https://www.hl7.org/fhir",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "http://fastrivertech.com", name = "info", email = "info@fastrivertech.com")
        ),
        tags = {
                @Tag(name = "HL7 FHIR 4.0", description = "HL7 FHIR 4.0", externalDocs = @ExternalDocumentation(description = "FHIR 4.0 Spec", url="https://www.hl7.org/fhir/")),
                @Tag(name = "Fast River Technologies", description = "Enabling Big Data Based AI Streaming Analytics In Healthcare", externalDocs = @ExternalDocumentation(description = "Fast River Technologies", url="http://fastrivertech.com/")),
        },
        externalDocs = @ExternalDocumentation(description = "HL7 FHIR Overview"),
        security = {
                @SecurityRequirement(name = "req 1", scopes = {"a", "b"}),
                @SecurityRequirement(name = "req 2", scopes = {"b", "c"})
        },
        servers = {
               @Server(
                    description = "FHIR Resource Server: Local Deployment",
                    url = "http://{host}:{port}",
                    variables = {
                            @ServerVariable(name = "host", description = "Host name where FHIR Resource Services Deployed", defaultValue = "localhost", allowableValues = {"localhost", "ec2-54-202-187-87.us-west-2.compute.amazonaws.com"}),
                            @ServerVariable(name = "port", description = "Port where FHIR Resource Services Deployed", defaultValue = "8080", allowableValues = {"8080", "8088"})
                    })
        }
)
@Path(ResourcePath.BASE_PATH)
@PermitAll
@SecurityScheme(name = "FastRiverTechFHIRServiceOauth2Security",
	type = SecuritySchemeType.OAUTH2,
	in = SecuritySchemeIn.HEADER,
	flows = @OAuthFlows(
        implicit = @OAuthFlow(authorizationUrl = "http://ec2-54-202-187-87.us-west-2.compute.amazonaws.com:8088/auth",
                scopes = @OAuthScope(name = "write:patients", description = "Create resource in your account"))))
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
	 * @status 201 Created success
     * @status 400 Bad request - Resource could not be parsed or failed basic FHIR validation rules
	 * @status 404 Not found - Resource type not supported, or not a FHIR end-point
	 * @status 422 Unprocessable entity
	 */
	@POST
	@Path(ResourcePath.TYPE_PATH)
	@Consumes({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Produces({MimeType.APPLICATION_FHIR_JSON, MimeType.APPLICATION_JSON})
	@Operation(summary = "Create a new Patient",
    tags = {"Patients"},
//    security = @SecurityRequirement(
//                            name = "petstore-auth",
//                            scopes = "write:pets"),
    responses = {
            @ApiResponse(
               content = @Content(mediaType = "application/json",
                       schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Resource could not be parsed or failed basic FHIR validation rules"),
            @ApiResponse(responseCode = "404", description = "Not found - Resource type not supported, or not a FHIR end-point"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity") }
)	public <R extends DomainResource> Response create(@PathParam("type") final String type,
						   						      @QueryParam("_format") @DefaultValue("json") final String _format, 
						   						      final String body) {
		try {
			logger.info(localizer.x("CreateResourceInteraction creates a new resource"));
			// Request includes resource, but no need id; id shall be ignored if given. versionId and lastUpdated shall be ignored 
			// if meta provided.			
			// Response includes Location header: Location: [base]/[type]/[id]/_history/[vid]
			//                   ETag header: versionId and Last-Modified			
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
				String location = uriInfo.getAbsolutePath().getPath() + "_history/" + created.get().getMeta().getVersionId();
				return ResourceOperationResponseBuilder.build(resourceInJson, Status.CREATED, "1", location, MimeType.APPLICATION_FHIR_JSON);
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
