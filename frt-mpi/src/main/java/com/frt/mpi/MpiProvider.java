package com.frt.mpi;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Identifier;

public interface MpiProvider {

	Optional<Patient> merge(Identifier target, Identifier source)
		throws MpiProviderException;
	
	Optional<Patient> link(Identifier target, Identifier source)
			throws MpiProviderException;
	
}
