package com.frt.mpi;

import java.util.List;
import java.util.Optional;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientIdentifier;

public interface MpiProvider {

	Optional<Patient> merge(PatientIdentifier target, PatientIdentifier source)
		throws MpiProviderException;
	
	Optional<Patient> link(PatientIdentifier target, PatientIdentifier source)
		throws MpiProviderException;
	
	Optional<Patient> create(Patient patient)
		throws MpiProviderException;
}
