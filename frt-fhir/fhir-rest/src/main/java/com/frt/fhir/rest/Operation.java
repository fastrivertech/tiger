/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Date:                             
 * $Revision:                         
 * $Author:                                         
 * $Id: 
 */
package com.frt.fhir.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import com.frt.stream.FhirMessageProducer;

@Path("/1.0")
public class Operation {

	private static FhirMessageProducer producer = new FhirMessageProducer();
	
	public Operation(){	
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response read() {
		producer.send("operation:read");
		Response.ResponseBuilder responseBuilder = Response.status(Status.OK).entity("operation:read");        
        return responseBuilder.build();		
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response create() {
		producer.send("operation:create");		
		Response.ResponseBuilder responseBuilder = Response.status(Status.OK).entity("operation:create");        
        return responseBuilder.build();		
	}

}
