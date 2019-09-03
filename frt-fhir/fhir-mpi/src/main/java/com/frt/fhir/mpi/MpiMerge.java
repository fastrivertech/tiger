package com.frt.fhir.mpi;

import org.hl7.fhir.r4.model.Patient;

public class MpiMerge {

	public static Patient execute(Patient source, Patient  target) {
		Patient resulted = target.copy();
		
		return resulted;
	}
	
}
