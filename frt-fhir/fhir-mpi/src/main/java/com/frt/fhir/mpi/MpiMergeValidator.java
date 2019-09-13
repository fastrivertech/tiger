package com.frt.fhir.mpi;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import com.frt.fhir.service.FhirServiceException;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.service.query.QueryOption;
import com.frt.dr.transaction.model.Transaction;
import com.frt.fhir.service.FhirService;

public class MpiMergeValidator {
	private FhirService fhirService;

	public MpiMergeValidator(FhirService fhirService) {
		this.fhirService = fhirService;
	}

	public Patient validateSource(Parameters parameters, String name) 
		throws MpiValidationException, MpiHasMergedException {
		Optional<NamedCache<String, String>> cache = CacheService.getInstance().getCache();		
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
				if (checkStatus(patient.get())) {
					// has been merged
					 if (cache.isPresent()) {
				    	 cache.get().put(NamedCache.ACTION_CODE, "HasMerged");
				     }							
					return patient.get();
				}
			};

			List<org.hl7.fhir.r4.model.Type> resourceIdentifiers = parameters.getParameters(name + "-identifier");
			for (org.hl7.fhir.r4.model.Type resourceIdentifier :  resourceIdentifiers) {	
				
				Reference reference = (Reference) resourceIdentifier;
				String message = "{ system:" + reference.getIdentifier().getSystem() + " , " + 
								    "value:" + reference.getIdentifier().getValue() + " }";				
				System.out.println(name + "-identifier = " + message);				
				
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
				if (!patients.isPresent() ||
					patients.get().isEmpty()) {
					queryOption.setStatus(QueryOption.StatusType.INACTIVE);
					Optional<List<Patient>> more = fhirService.read("Patient",
																		criterias, 
																		queryOption);
					if (!more.isPresent() ||
						more.get().isEmpty()) {																				
						throw new MpiValidationException(name + " invalid identifier: " + message);
					} else if (checkStatus(more.get().get(0))) {
						// has been merged
						if (cache.isPresent()) {
							cache.get().put(NamedCache.ACTION_CODE, "HasMerged");
						}							
						return more.get().get(0);
					} else {
						throw new MpiValidationException(name + " invalid identifier: " + message);						
					}
				}
			
				Patient patient = patients.get().get(0);
				if (source.isPresent()) {
					if (!source.get().getId().equals(patient.getId())) {
						throw new MpiValidationException(name +  " invalid identifier: " + message);
					}
				} else {
					source = Optional.of(patient);
				}
			};
			   
			return source.get();
		} catch (MpiValidationException | MpiHasMergedException ex) {
			throw ex;
		} catch (FhirServiceException ex) {
			throw new MpiValidationException(ex);
		}
	}

	public static boolean checkStatus(Patient patient) {
		boolean checked = false;
		List<Extension> extensions = patient.getExtension();
		for (Extension extension : extensions) {
			if ("http://hl7.org/fhir/StructureDefinition/patient-status".equals(extension.getUrl())) {
				if ("M".equals(extension.getValue().toString())) {
					checked = true;
					break;
				}
			}
		}
		return checked;
	}
	
}
