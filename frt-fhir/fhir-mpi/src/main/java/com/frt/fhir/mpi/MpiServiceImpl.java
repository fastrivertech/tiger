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
package com.frt.fhir.mpi;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.MultivaluedMap;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;
import com.frt.dr.service.query.QueryOption;
import com.frt.dr.transaction.model.Transaction;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.fhir.mpi.parser.ParameterParser;
import com.frt.fhir.mpi.parser.ParameterParserException;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;
import com.frt.fhir.service.FhirService;
import com.frt.mpi.MpiProvider;
import com.frt.mpi.MpiProviderImpl;
import com.frt.mpi.MpiProviderException;

/**
 * MpiServiceImpl class
 * @author cqye
 */
public class MpiServiceImpl implements MpiService<Patient> {

	private MpiProvider mpiProvider;
	private FhirService fhirService;
	
	public MpiServiceImpl(FhirService fhirService) {
		this.fhirService = fhirService;
	}

	public boolean isEnabled() {
		boolean enabled = false;
		return enabled;
	}
	
	/**
	 * @see com.frt.fhir.mpi.MpiService#match(Parameters) 
	 */
	public Bundle match(Parameters parameters) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#search(Parameters)
	 */
	public Bundle search(Parameters parameters)
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	public Optional<Patient> update(Patient patient, List<Parameter> options) 
		throws MpiServiceException {
		Optional<Patient> updated = Optional.empty();
		return updated;
	}
		
	/**
	 * @see com.frt.fhir.mpi.MpiService#merge(org.hl7.fhir.r4.model.Parameters)
	 */
	public Patient merge(org.hl7.fhir.r4.model.Parameters parameters)
		throws MpiServiceException, MpiValidationException {		
		Optional<NamedCache> cache = CacheService.getInstance().getCache();
		MpiMergeValidator validator = new MpiMergeValidator(fhirService);
		try {
			// source-patient
			Patient source = validator.validateSource(parameters, "source-patient");
			// patient
			Patient target = validator.validateSource(parameters, "patient");
			
			// result-patient
			if (parameters.hasParameter("result-patient")) {
				List<org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent> result_patients = parameters.getParameter();
				result_patients.forEach(result_patient->{
					if ("result-patient".equals(result_patient.getName())) {
						Patient patient = (Patient)result_patient.getResource();						
						System.out.println("result-patient = " + patient.getId());
						if (!target.getId().equals(result_patient.getId())) {
							throw new MpiValidationException("invalid result patient: " + result_patient.getId());												
						}						
					}					
				});						
			}
			
			// merge operation			
			Patient result = MpiMerge.execute(source, target);
			PatientLinkComponent sourceLink = new PatientLinkComponent();
			sourceLink.setId(source.getId());
			result.addLink(sourceLink);
			
			// merge source to target 
			Patient resulted = fhirService.update(Patient.class.getName(), 
					                              target.getId(),
					                              result);
			PatientLinkComponent targetLink = new PatientLinkComponent();
			source.addLink(targetLink);
			source.setActive(false);
			fhirService.update(Patient.class.getName(), 
                    		   source.getId(),
                               source);
			// delete source
			//fhirService.delete(org.hl7.fhir.r4.model.Patient.class.getName(), 
			//				   source.get().getId());
			
			 if (cache.isPresent()) {
				 cache.get().put(NamedCache.ACTION_CODE, "Merged");
			 }				
			 
			return resulted;  												     
		} catch (Exception ex) {
			throw new MpiServiceException(ex);
		}
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unmerge(Identifier, List)
	 */
	public Bundle unmerge(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#link(Identifier, Identifier, List)
	 */
	public Bundle link(Identifier sourceId, Identifier targetId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unlink(Identifier, List)
	 */
	public Bundle unlink(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

}
