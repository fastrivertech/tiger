package com.frt.fhir.mpi;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;

public class MpiMerge {

	public static Patient execute(Patient source, Patient  target) {
		Patient result = target.copy();
		
		PatientLinkComponent sourceLink = new PatientLinkComponent();
		sourceLink.setId(source.getId());
		result.addLink(sourceLink);

		PatientLinkComponent targetLink = new PatientLinkComponent();
		source.addLink(targetLink);
		source.setActive(false);

		return result;
	}
	
}
