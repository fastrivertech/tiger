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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.hl7.fhir.dstu3.model.DomainResource;

import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

/**
 * HistoryResourceOperation class
 * @author cqye
 */
@Path(ResourcePath.BASE_PATH)
@PermitAll
public class HistoryResourceOperation extends ResourceOperation {
	
	private static Logger logger = Logger.getLog(HistoryResourceOperation.class.getName());
	private static Localization localizer = Localization.getInstance("com.frt.fhir");
		
	@Context
	private UriInfo uriInfo;

	public HistoryResourceOperation(){
	}
	
	@GET
	@Path(ResourcePath.TYPE_PATH + ResourcePath.ID_PATH + ResourcePath.HISTORY_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("type") final String type, 
												    @PathParam("id") String id,
												    @QueryParam("_format") @DefaultValue("json") final String _format) {
		
		logger.info(localizer.x("FHR_I005: HistoryResourceOperation retrieves the hsitory of resource {0} by its id {1}", type, id));										
		String resourceInJson = "not implemented yet";
		return ResourceOperationResponseBuilder.build(resourceInJson, Status.OK, "1.0", MediaType.APPLICATION_JSON);
	
	}
	
}
