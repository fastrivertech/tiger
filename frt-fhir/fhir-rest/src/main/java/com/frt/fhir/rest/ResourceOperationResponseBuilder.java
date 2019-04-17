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
import java.util.Date;
import javax.ws.rs.core.Response;
import com.frt.util.logging.Localization;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;

/**
 * ResourceOperationResponseBuilder class
 * 
 * @author cqye
 */
public class ResourceOperationResponseBuilder {
	private static Localization localizer = Localization.getInstance("com.frt.fhir");

	public static Response build(Object body, Response.Status status, String tag, String type) {
		Response.ResponseBuilder responseBuilder = Response.status(status).entity(body);
		responseBuilder.lastModified(new Date()).tag("W/" + tag).type(type + ";charset=UTF-8");
		return responseBuilder.build();
	}
	
	public static Response build(Object body, Response.Status status, String tag, String location, String type) {
		Response.ResponseBuilder responseBuilder = Response.status(status).entity(body);
		responseBuilder.lastModified(new Date()).tag("W/" + tag).type(type + ";charset=UTF-8").header("Location", location);
		return responseBuilder.build();
	}

	public static OperationOutcome buildOperationOutcome(String message, OperationOutcome.IssueSeverity code, OperationOutcome.IssueType type) {
		CodeableConcept codeableConcept = new CodeableConcept().setText(message);
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setSeverity(code).setCode(type).setDetails(codeableConcept);
		return outcome;
	}

}
