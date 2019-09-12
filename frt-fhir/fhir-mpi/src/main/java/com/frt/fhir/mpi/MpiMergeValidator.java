package com.frt.fhir.mpi;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import com.frt.fhir.service.FhirServiceException;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.service.query.QueryOption;
import com.frt.fhir.service.FhirService;

public class MpiMergeValidator {
	private FhirService fhirService;

	public MpiMergeValidator(FhirService fhirService) {
		this.fhirService = fhirService;
	}

	public Patient validateSource(Parameters parameters, String name) 
		throws MpiValidationException {
		try {
			Optional<Patient> source = Optional.empty();
			List<org.hl7.fhir.r4.model.Type> resourceReferences = parameters.getParameters(name);
			for (org.hl7.fhir.r4.model.Type resourceReference :  resourceReferences) {	
				String reference = ((Reference) resourceReference).getReference();
				
				System.out.println(name + " reference = " + reference);				
				
				String[] tokens = reference.split("/");
				if (tokens.length != 2 && !tokens[0].equalsIgnoreCase("Patient")) {
					throw new MpiValidationException(name + " invalid type: " + reference);
				}
				Optional<Patient> patient = fhirService.read("Patient", 
														     tokens[1],
														     new QueryOption());
				if (!patient.isPresent()) {
					throw new MpiValidationException(name + " invalid reference Id: " + reference);
				};
				source = Optional.of(patient.get());
			};

			List<org.hl7.fhir.r4.model.Type> resourceIdentifiers = parameters.getParameters(name + "-identifier");
			for (org.hl7.fhir.r4.model.Type resourceIdentifier :  resourceIdentifiers) {	
				Reference reference = (Reference) resourceIdentifier;
				
				System.out.println(name + "-identifier = " +
								   "{ system:" +
								      reference.getIdentifier().getSystem() + " , " + 
								      "value:" +
						              reference.getIdentifier().getValue() + " }");
				
				QueryCriteria criterias = new QueryCriteria();
				MultivaluedMap params = new MultivaluedHashMap<>();
				params.add("system", reference.getIdentifier().getSystem()); 
				params.add("value",	reference.getIdentifier().getValue());
				criterias.setParams(params);
				
				QueryOption queryOption = new QueryOption();
				queryOption.setStatus(QueryOption.StatusType.ACTIVE);
				Optional<List<Patient>> patients = fhirService.read("Patient",
																	criterias, 
																	queryOption);
				
				if (!patients.isPresent() || patients.get().size() != 1) {
					throw new MpiValidationException(
							name + " invalid identifier: " + reference.getIdentifier().toString());
				}
				Patient patient = patients.get().get(0);
				if (source.isPresent()) {
					if (!source.get().getId().equals(patient.getId())) {
						throw new MpiValidationException(name + 
														 " invalid identifier: " + 
														 "{ system:" +
														 reference.getIdentifier().getSystem() + " , " + 
														 "value:" +
												         reference.getIdentifier().getValue() + " }");
					}
				} else {
					source = Optional.of(patient);
				}
			};
			   
			return source.get();
		} catch (FhirServiceException ex) {
			throw new MpiValidationException(ex);
		}
	}

}
