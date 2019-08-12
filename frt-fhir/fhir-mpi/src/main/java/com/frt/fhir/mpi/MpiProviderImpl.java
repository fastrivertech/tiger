package com.frt.fhir.mpi;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Identifier;
import com.frt.fhir.mpi.resource.Parameter;

public class MpiProviderImpl implements MpiProvider {

	public Optional<DomainResource> merge(Identifier target, Identifier source, List<Parameter> options)
		throws MpiProviderException {
		Optional<DomainResource> merged = Optional.empty();		
		return merged;		
	}
	
}
