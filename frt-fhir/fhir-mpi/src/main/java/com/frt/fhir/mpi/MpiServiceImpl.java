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
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
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

	private MpiProvider mpiProvider;
	
	public MpiServiceImpl() {
		mpiProvider = new MpiProviderImpl();
	}

	public boolean isEnabled() {
		boolean enabled = false;
		return enabled;
	}
	
	/**
	 * @see com.frt.fhir.mpi.MpiService#match(Parameters) 
	 */
	@Override
	public Bundle match(Parameters parameters) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#search(Parameters)
	 */
	@Override
	public Bundle search(Parameters parameters)
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Patient> update(Patient patient, List<Parameter> options) 
		throws MpiServiceException {
		Optional<Patient> updated = Optional.empty();
		return updated;
	}
		
	/**
	 * @see com.frt.fhir.mpi.MpiService#merge(org.hl7.fhir.r4.model.Parameters)
	 */
	@Override	
	public Optional<Patient> merge(org.hl7.fhir.r4.model.Parameters parameters)
		throws MpiServiceException {
		try {
			//Identifier source = ParameterParser.decodeIdentifier(identifiers.get(0));
			//Identifier target = ParameterParser.decodeIdentifier(identifiers.get(0));
		    //return mpiProvider.merge(target, source);
			return Optional.empty();  
		} catch (MpiProviderException | ParameterParserException ex) {
			throw new MpiServiceException(ex);
		}		
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unmerge(Identifier, List)
	 */
	@Override		
	public Bundle unmerge(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#link(Identifier, Identifier, List)
	 */
	@Override		
	public Bundle link(Identifier sourceId, Identifier targetId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.frt.fhir.mpi.MpiService#unlink(Identifier, List)
	 */
	@Override		
	public Bundle unlink(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException {
		throw new UnsupportedOperationException();
	}

}
