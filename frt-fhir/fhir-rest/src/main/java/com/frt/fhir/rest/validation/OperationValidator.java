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
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriInfo;

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
	private final static List<String> modes = new ArrayList<>();	
	private final static Pattern idPattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._-]*");

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
		
		modes.add("full");
		modes.add("normative");
		modes.add("terminology");		
	}

	/**
	 * Validate mime-type format
	 * 
	 * @param format mime-type format
	 * @throws OperationValidatorException throw if the format is invalid
	 */
	public static void validateFormat(String format) 
		throws OperationValidatorException {
		if (!formats.contains(format)) {
			throw new OperationValidatorException(localizer.x("invalid _format " + format),
					  OperationValidatorException.ErrorCode.INVALID_MIME_TYPE);
		} 
	}

	/**
	 * Validate _summary parameter
	 * 
	 * @param summary
	 * @throws OperationValidatorException throw if the summary value is invalid
	 */
	public static void validateSummary(String summary) 
		throws OperationValidatorException {
		if (!summaries.contains(summary)) {
			throw new OperationValidatorException(localizer.x("invalid _summary " + summary),
					  OperationValidatorException.ErrorCode.INVALID_SUMMARY_TYPE);
		} 
	}
	
	/**
	 * Validate id parameter
	 * 
	 * @param id
	 * @throws OperationValidatorException throw if the id value is invalid
	 */
	public static void validateId(Optional<String> id) 
		throws OperationValidatorException {
		if (id.isPresent()) {
			if (!idPattern.matcher(id.get()).matches()) {
				throw new OperationValidatorException(localizer.x("invalid id " + id.get()),
										      OperationValidatorException.ErrorCode.INVALID_ID);
			}
		}
	}

}
