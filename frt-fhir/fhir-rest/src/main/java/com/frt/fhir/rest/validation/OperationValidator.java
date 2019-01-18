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
package com.frt.fhir.rest.validation;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import com.frt.util.logging.Localization;

/**
 * InteractionValidator class
 * 
 * @author cqye
 */
public class OperationValidator {
	private final static Localization localizer = Localization.getInstance();

	private final static List<String> formats = new ArrayList<>();
	private final static List<String> summaries = new ArrayList<>();
	private final static Pattern idPattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9._-]+");

	static {
		formats.add("json");
		formats.add("application/json");		
		formats.add("application/fhir+json");
		formats.add("xml");
		formats.add("text/xml");
		formats.add("application/xml");		
		formats.add("application/fhir+xml");

		summaries.add("true");
		summaries.add("false");
		summaries.add("text");
		summaries.add("data");
	}

	/**
	 * Validate mime-type format
	 * 
	 * @param format mime-type format
	 * @throws ValidationException throw if the format is invalid
	 */
	public static void validateFormat(String format) throws ValidationException {
		if (!formats.contains(format)) {
			throw new ValidationException(localizer.x("invalid _format " + format),
										  ValidationException.ErrorCode.INVALID_MIME_TYPE);
		} 
	}

	/**
	 * Validate _summary parameter
	 * 
	 * @param summary
	 * @throws ValidationException throw if the summary value is invalid
	 */
	public static void validateSummary(String summary) throws ValidationException {
		if (summary == null || !summaries.contains(summary)) {
			throw new ValidationException(localizer.x("invalid _summary " + summary),
										  ValidationException.ErrorCode.INVALID_MIME_TYPE);
		}
	}

	public static void validateId(String id) throws ValidationException {
		if (id != null) {
			if (!idPattern.matcher(id).matches()) {
				throw new ValidationException(localizer.x("invalid id " + id),
										      ValidationException.ErrorCode.INVALID_ID);
			}
		}
	}

	public static void validateParameters(String id, MultivaluedMap params) throws ValidationException {
		if (id == null&&(params==null||params.size()==0)) {
				throw new ValidationException(localizer.x("invalid id " + id),
										      ValidationException.ErrorCode.INVALID_QUERY_PARAMS);
		}
	}
}
