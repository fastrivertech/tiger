package com.frt.fhir.rest.validation;

import java.util.List;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Resource;

public class MpiOperationValidator extends OperationValidator {
	
	public static void validateIdentifiers(List<String> identifiers) 
		throws OperationValidatorException  {
		if (identifiers != null && identifiers.size() != 2) {
			throw new OperationValidatorException(localizer.x("invalid number of patient identifiers " + identifiers.size()),
					  OperationValidatorException.ErrorCode.INVALID_QUERY_PARAMS);			
		}
	}
	
	public static void validateParameters(Parameters parameters) 
		throws OperationValidatorException  {
		Type sourceReference = parameters.getParameter("source-patient");
		Type sourceIdentifier = parameters.getParameter("source-patient-identifier");
		//sourceReference or sourceIdentifier has to be specified 
		if (sourceReference == null && 
			sourceIdentifier == null) {
			throw new OperationValidatorException("one of source-patient or source-patient-identifier has to be given for locating a source patient");
		}
		//sourceReference patient reference
		
		//sourceIdentifier patient identifier
		
		Type targetReference = parameters.getParameter("patient");
		Type targetIdentifier = parameters.getParameter("patient-identifier");
		if (targetReference == null && 
			targetIdentifier == null) {
			throw new OperationValidatorException("one of patient or patient-identifier has to be given for locating a target patient");			
		}
		//targetReference patient reference
		
		//targetIdentifier patient identifier
						
	}

}
