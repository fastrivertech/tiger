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
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import com.frt.dr.service.RepositoryApplication;
import com.frt.dr.service.RepositoryContext;
import com.frt.dr.service.RepositoryContextException;
import com.frt.dr.service.RepositoryServiceException;
import com.frt.dr.service.query.QueryOption;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.fhir.mpi.parser.ParameterParser;
import com.frt.fhir.mpi.parser.ParameterParserException;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;
import com.frt.mpi.MpiProvider;
import com.frt.mpi.MpiProviderImpl;
import com.frt.mpi.MpiProviderException;

/**
 * MpiServiceImpl class
 * @author cqye
 */
public class MpiServiceImpl implements MpiService<Patient> {

	@Autowired
	private RepositoryApplication repository;	
	private MpiProvider mpiProvider;
	
	public MpiServiceImpl() 
		throws MpiServiceException {
		try {
			RepositoryContext context = new RepositoryContext(RepositoryApplication.class); 			
			repository = (RepositoryApplication)context.getBean(RepositoryApplication.class);			
		} catch (RepositoryContextException rcex) {
			throw new MpiServiceException(rcex);
		}	
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
		throws MpiServiceException {		
		try {
			List<org.hl7.fhir.r4.model.Type> source_patients = parameters.getParameters("source-patient");		
			source_patients.forEach(source_patient->{
				Reference ref = (Reference)source_patient;
				System.out.println("source-patient reference = " + ref.getReference());
				
			});
			
			List<org.hl7.fhir.r4.model.Type> source_patient_identifiers = parameters.getParameters("source-patient-identifier");
			source_patient_identifiers.forEach(source_patient_identifier->{
				Reference ref = (Reference)source_patient_identifier;
				System.out.println("source-patient-identifier = " + ref.getIdentifier().getSystem() + " : " + ref.getIdentifier().getValue());
			});
			
			List<org.hl7.fhir.r4.model.Type> patients = parameters.getParameters("patient");;
			patients.forEach(patient->{
				Reference ref = (Reference)patient;
				System.out.println("patient reference = " + ref.getReference());				
			});
			
			List<org.hl7.fhir.r4.model.Type> patient_identifiers = parameters.getParameters("patient-identifier");
			patient_identifiers.forEach(patient_identifier->{
				Reference ref = (Reference)patient_identifier;
				System.out.println("patient-identifier = " + ref.getIdentifier().getSystem() + " : " + ref.getIdentifier().getValue());
			});
			
			if (parameters.hasParameter("result-patient")) {
				//List<org.hl7.fhir.r4.model.Type> result_patients = parameters.getParameters("result-patient");
				List<org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent> values = parameters.getParameter();
				values.forEach(value->{
					if ("result-patient".equals(value.getName())) {
						org.hl7.fhir.r4.model.Patient patient = (org.hl7.fhir.r4.model.Patient)value.getResource();						
						System.out.println("result-patient = " + patient.getId());				
					}					
				});				
			}
						
			return Optional.empty();  												     
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
