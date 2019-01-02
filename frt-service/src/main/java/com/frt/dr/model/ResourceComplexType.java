package com.frt.dr.model;

import com.frt.dr.model.base.Patient;

/**
 * marker interface for all
 * FRT object for FHIR composite types e.g.
 * FHIR Address, CodeableConcept, Identifier, etc.
 * @author Jim.Fu
 *
 */
public interface ResourceComplexType {
	void setPath(String path);
	void setPatient(Patient p);
}
