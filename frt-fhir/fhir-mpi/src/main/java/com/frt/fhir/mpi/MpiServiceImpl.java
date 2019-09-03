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
	public Optional<Patient> merge(org.hl7.fhir.r4.model.Parameters parameters)
		throws MpiServiceException, MpiInvalidException{		
		try {
			// source-patient
			Optional<Patient> source = Optional.empty(); 
			List<org.hl7.fhir.r4.model.Type> source_patients = parameters.getParameters("source-patient");					
			source_patients.forEach(source_patient->{
				String reference = ((Reference)source_patient).getReference();
				String[] tokens = reference.split("/");
				if (tokens.length != 2 &&
					!tokens[0].equalsIgnoreCase("Patient")) {
					throw new MpiInvalidException("invalid source patient type: " + reference);
				}
				Optional<Patient> patient = fhirService.read(org.hl7.fhir.r4.model.Patient.class.getName(), 
						                                     tokens[1], 
						                                     new QueryOption());
				if (!patient.isPresent()) {
					throw new MpiInvalidException("invalid source patient reference Id: " + reference);					
				};
				source.of(patient);
			}); 
			// source-patient-identifier
			List<org.hl7.fhir.r4.model.Type> source_patient_identifiers = parameters.getParameters("source-patient-identifier");
			source_patient_identifiers.forEach(source_patient_identifier->{
				Reference reference = (Reference)source_patient_identifier;
				System.out.println("source-patient-identifier = " + 
				                    reference.getIdentifier().getSystem() + " : " + 
				                    reference.getIdentifier().getValue());
				QueryCriteria criterias = new QueryCriteria();
				MultivaluedMap params = null; 
				criterias.setParams(params);
				Optional<List<Patient>> patients = fhirService.read(org.hl7.fhir.r4.model.Patient.class.getName(), 
						                                           criterias, 
														     	   new QueryOption());
				if (!patients.isPresent() || 
					 patients.get().size() > 1) {
					throw new MpiInvalidException("invalid source patient identifier: " + reference.getIdentifier().toString());					
				}
				Patient patient = patients.get().get(0);
				if (source.isPresent()) {
					if (!source.get().equals(patient.getId())) {
						throw new MpiInvalidException("invalid source patient identifier: " + reference.getIdentifier().toString());											
					}
				} else {
					source.of(patient);
				}
				
			});
			// patient
			Optional<Patient> target = Optional.empty();
			List<org.hl7.fhir.r4.model.Type> patients = parameters.getParameters("patient");;
			patients.forEach(patient->{
				Reference reference = (Reference)patient;
				System.out.println("patient reference = " + reference.getReference());	
				// ToDo 
				
			});
			// patient-identifier
			List<org.hl7.fhir.r4.model.Type> patient_identifiers = parameters.getParameters("patient-identifier");
			patient_identifiers.forEach(patient_identifier->{
				Reference reference = (Reference)patient_identifier;
				System.out.println("patient-identifier = " + 
				                   reference.getIdentifier().getSystem() + " : " + 
						           reference.getIdentifier().getValue());
				// ToDo
			});
			// result-patient
			if (parameters.hasParameter("result-patient")) {
				List<org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent> result_patients = parameters.getParameter();
				result_patients.forEach(result_patient->{
					if ("result-patient".equals(result_patient.getName())) {
						org.hl7.fhir.r4.model.Patient patient = (org.hl7.fhir.r4.model.Patient)result_patient.getResource();						
						System.out.println("result-patient = " + patient.getId());
						if (!target.get().equals(result_patient.getId())) {
							throw new MpiInvalidException("invalid result patient: " + result_patient.getId());												
						}						
					}					
				});						
			}
			// merge operation			
			Patient result = target.get().copy();
			PatientLinkComponent sourceLink = new PatientLinkComponent();
			sourceLink.setId(source.get().getId());
			result.addLink(sourceLink);
			
			// merge source to target 
			Patient resulted = fhirService.update(org.hl7.fhir.r4.model.Patient.class.getName(), 
					                              target.get().getId(),
					                              result);
			PatientLinkComponent targetLink = new PatientLinkComponent();
			source.get().addLink(targetLink);
			source.get().setActive(false);
			fhirService.update(org.hl7.fhir.r4.model.Patient.class.getName(), 
                    		   source.get().getId(),
                               source.get());
			// delete source
			//fhirService.delete(org.hl7.fhir.r4.model.Patient.class.getName(), 
			//				   source.get().getId());
			
			return Optional.of(resulted);  												     
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
