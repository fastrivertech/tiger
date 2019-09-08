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
			resourceReferences.forEach(resourceReference-> {
				String reference = ((Reference) resourceReference).getReference();
				String[] tokens = reference.split("/");
				if (tokens.length != 2 && !tokens[0].equalsIgnoreCase("Patient")) {
					throw new MpiValidationException(name + " invalid type: " + reference);
				}
				Optional<Patient> patient = fhirService.read("Patient", 
														     tokens[1],
														     new QueryOption());
				if (!patient.isPresent()) {
					throw new MpiValidationException(name + " invalid reference Id: " + reference);
				}
				;
				source.of(patient);
			});

			List<org.hl7.fhir.r4.model.Type> resourceIdentifiers = parameters.getParameters(name + "-identifier");
			   resourceIdentifiers.forEach(resourceIdentifier-> {
				Reference reference = (Reference) resourceIdentifier;
				
				System.out.println(name + "-identifier = " + 
								   reference.getIdentifier().getSystem() + " : " + 
						           reference.getIdentifier().getValue());
				
				QueryCriteria criterias = new QueryCriteria();
				MultivaluedMap params = new MultivaluedHashMap<>();
				params.add(reference.getIdentifier().getSystem(), reference.getIdentifier().getValue());
				criterias.setParams(params);
				Optional<List<Patient>> patients = fhirService.read("Patient",
																	criterias, 
																	new QueryOption());
				if (!patients.isPresent() || patients.get().size() > 1) {
					throw new MpiValidationException(
							name + " invalid identifier: " + reference.getIdentifier().toString());
				}
				Patient patient = patients.get().get(0);
				if (source.isPresent()) {
					if (!source.get().equals(patient.getId())) {
						throw new MpiValidationException(
								name + " invalid identifier: " + reference.getIdentifier().toString());
					}
				} else {
					source.of(patient);
				}
			});
			   
			return source.get();
		} catch (FhirServiceException ex) {
			throw new MpiValidationException(ex);
		}
	}

}
