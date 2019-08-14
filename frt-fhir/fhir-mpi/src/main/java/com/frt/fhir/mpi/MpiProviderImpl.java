package com.frt.fhir.mpi;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import com.frt.fhir.mpi.resource.Parameter;
import com.sun.mdm.index.webservice.PatientEJB;
import com.sun.mdm.index.webservice.MergePatientResult;
import com.sun.mdm.index.webservice.MatchColResult;
import com.sun.mdm.index.webservice.SystemPatient;
import com.sun.mdm.index.master.UserException;
import com.sun.mdm.index.master.ProcessingException;


public class MpiProviderImpl implements MpiProvider {
	private PatientEJB patientMpi;
	
	public MpiProviderImpl() {
		patientMpi = new PatientEJB();
	}
	
	public Optional<Patient> merge(Identifier target, Identifier source, List<Parameter> options)
		throws MpiProviderException {
		try {
			Optional<Patient> merged = Optional.empty();				
			MergePatientResult result = patientMpi.mergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);		
			return merged;		
		} catch (ProcessingException | UserException ex) {
			throw new MpiProviderException(ex);
		}
	}

	public Optional<Patient> unmerge(Identifier target, Identifier source, List<Parameter> options)
		throws MpiProviderException {
		try {
			Optional<Patient> unmerged = Optional.empty();				
			MergePatientResult result = patientMpi.unmergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);		
			return unmerged;	
		} catch (ProcessingException | UserException ex) {
			throw new MpiProviderException(ex);
		}		
	}
	
	public Optional<Patient> link(Identifier target, Identifier source, List<Parameter> options)
		throws MpiProviderException {
		try {
			Optional<Patient> linked = Optional.empty();
			String targetEuid = patientMpi.getEUID(target.getSystem(), target.getValue());
			String sourceEuid = patientMpi.getEUID(source.getSystem(), source.getValue());
			MergePatientResult result = patientMpi.mergeEnterpriseRecord(sourceEuid, targetEuid, false);		
			return linked;
		} catch (ProcessingException | UserException ex) {
			throw new MpiProviderException(ex);
		}		
	}
	
	public Optional<Patient> unlink(Identifier identifier, List<Parameter> options)
		throws MpiProviderException {
		try {
			Optional<Patient> unlinked = Optional.empty();
			String euid = patientMpi.getEUID(identifier.getSystem(), identifier.getValue());
			MergePatientResult result = patientMpi.unmergeEnterpriseRecord(euid, false);		
			return unlinked;
		} catch (ProcessingException | UserException ex) {
			throw new MpiProviderException(ex);
		}
	}
	
	public Optional<Patient> update(Patient patient, List<Parameter> options)
		throws MpiProviderException {
		try {
			Optional<Patient> created = Optional.empty();
			SystemPatient systemPatient = new SystemPatient();
			MatchColResult result = patientMpi.executeMatch(systemPatient);		
			return created;
		} catch (ProcessingException | UserException ex) {
			throw new MpiProviderException(ex);
		}
	}
	
	
}
