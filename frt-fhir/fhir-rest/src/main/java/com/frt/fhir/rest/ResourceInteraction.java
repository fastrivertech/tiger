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

import java.net.URI;
import java.util.Locale;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.CacheControl;
import com.frt.util.logging.Logger;
import com.frt.util.logging.Localization;

/**
 * ResourceInteraction base class
 * 
 * @author cqye
 */
public abstract class ResourceInteraction {
	private static Logger logger = Logger.getLog(ResourceInteraction.class.getName());
	private static Localization localizer = Localization.getInstance();

	public ResourceInteraction() {
	}

	public Response responseBuild(Response.ResponseBuilder responseBuilder, URI uri) {
		CacheControl cache = new CacheControl();
		cache.setNoCache(true);
		cache.setNoStore(true);
		cache.setSMaxAge(0);
		cache.setMustRevalidate(true);
		responseBuilder.cacheControl(cache);
		return responseBuilder.language(Locale.ENGLISH).location(uri).header("Pragma", "no-cache")
						   	  .header("Expires", "0").header("Access-Control-Allow-Origin", "*")
						      .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS")
							  .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							  .header("X-Content-Type-Options", "nosniff").build();
	}

	public Response responseBuild(Response.ResponseBuilder responseBuilder, URI uri, StringBuilder warning) {
		CacheControl cache = new CacheControl();
		cache.setNoCache(true);
		cache.setNoStore(true);
		cache.setSMaxAge(0);
		cache.setMustRevalidate(true);
		responseBuilder.cacheControl(cache);
		return responseBuilder.language(Locale.ENGLISH).location(uri).header("Pragma", "no-cache")
				              .header("Expires", "0").header("Access-Control-Allow-Origin", "*")
				              .header("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS")
				              .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
				              .header("Warning-Message", warning.toString()).build();
	}

}
