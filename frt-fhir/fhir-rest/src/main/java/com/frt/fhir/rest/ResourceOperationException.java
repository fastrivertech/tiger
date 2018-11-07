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
import java.net.URISyntaxException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 * ResourceInteractionException class
 * 
 * @author cqye
 */
public class ResourceOperationException extends WebApplicationException {

    public ResourceOperationException(Throwable t, Response.Status s, String errorCode, String errorMessage, String relatedLink, String additionalInfoLink) {
        super(t, Response.status(s).
                 entity(buildErrorMessage(s, errorCode, errorMessage, relatedLink, additionalInfoLink)).
                 type(MediaType.APPLICATION_JSON).build());
    }

    public ResourceOperationException(Throwable t, Response.Status s, String errorCode, String errorMessage) {
        super(t, Response.status(s).
                 entity(buildErrorMessage(s, errorCode, errorMessage, null, null)).
                 type(MediaType.APPLICATION_JSON).build());
    }

	private static ResourceOperationErrorMessage buildErrorMessage(Response.Status s, String errorCode, String errorMessage, String relatedLink, String additionalInfoLink) {
		ResourceOperationErrorMessage error = new ResourceOperationErrorMessage();
        try {
            URI errorCodeURI = new URI(errorCode);
            error.setErrorCode(errorCodeURI.toASCIIString());
        } catch (URISyntaxException uex) {
             error.setErrorCode(errorCode);
        }
        error.setErrorMessage(errorMessage);
        error.setHttpStatusCode(s.getStatusCode());
        error.setHttpMessage(s.getReasonPhrase());
        error.setRelatedLink(relatedLink);
        error.setAdditionalInfoLink(additionalInfoLink);
        return error;
    }
}
