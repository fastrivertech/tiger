package com.frt.fhir.rest.validation;

import java.util.List;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Reference;
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
		
		Type targetReference = parameters.getParameter("patient");
		Type targetIdentifier = parameters.getParameter("patient-identifier");
		if (targetReference == null && 
			targetIdentifier == null) {
			throw new OperationValidatorException("one of patient or patient-identifier has to be given for locating a target patient");			
		}

		List<org.hl7.fhir.r4.model.Type> source_patients = parameters.getParameters("source-patient");		
		if (source_patients != null && source_patients.size() > 1) {
			throw new OperationValidatorException("only one source-patient allowed");						
		}
		
		List<org.hl7.fhir.r4.model.Type> patients = parameters.getParameters("patient");;
		if (patients != null && patients.size() > 1) {
			throw new OperationValidatorException("only one patient allowed");						
		}

		int count_of_result_patients = 0;
		List<org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent> result_patients = parameters.getParameter();
		for (int i = 0; i < result_patients.size(); i++) {
			if ("result-patient".equals(result_patients.get(i).getName())) {
				count_of_result_patients++;
			}								
		}			
		if (count_of_result_patients > 1) {
			throw new OperationValidatorException("only one result-patient allowed");									
		}
	}
	
}
