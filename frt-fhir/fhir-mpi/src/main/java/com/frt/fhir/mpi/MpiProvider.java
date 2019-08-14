package com.frt.fhir.mpi;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Identifier;
import com.frt.fhir.mpi.resource.Parameter;

public interface MpiProvider {

	Optional<Patient> merge(Identifier target, Identifier source, List<Parameter> options)
		throws MpiProviderException;
	
	Optional<Patient> link(Identifier target, Identifier source, List<Parameter> options)
			throws MpiProviderException;
	
}
