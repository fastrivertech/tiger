package com.frt.fhir.rest.validation;

import java.util.List;

public class MpiOperationValidator extends OperationValidator {
	
	public static void validateIdentifiers(List<String> identifiers) 
		throws OperationValidatorException  {
		if (identifiers != null && identifiers.size() != 2) {
			throw new OperationValidatorException(localizer.x("invalid number of patient identifiers " + identifiers.size()),
					  OperationValidatorException.ErrorCode.INVALID_QUERY_PARAMS);			
		}
	}

}
