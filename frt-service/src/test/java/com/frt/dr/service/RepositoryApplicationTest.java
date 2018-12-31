package com.frt.dr.service;

import static org.junit.Assert.*;
import org.junit.Test;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientHumanName;

public class RepositoryApplicationTest {

	@Test
	public void test() {
		try {
			RepositoryContext context = new RepositoryContext(RepositoryApplication.class);			
			RepositoryApplication repository = (RepositoryApplication)context.getBean(RepositoryApplication.class);	
			
			Patient patient = new Patient();
			patient.setPatientId("03FAC9BB-5C5E-4F26-93EA-750A333B88E5");
			patient.setActive(true);
			patient.setGender("MALE");
			PatientHumanName name = new PatientHumanName();
			name.setHumannameId("A8838F72-A8BD-4398-B85C-DCC0BB5FB5CE");
			//name.setPatientId(Long.valueOf(10000));
			name.setUse("OFFICIAL");
			name.setFamily("Charles");
			patient.getNames().add(name);			
			repository.create(Patient.class, patient);
			assertTrue(true);
		} catch (RepositoryContextException | RepositoryServiceException ex) {
			fail(ex.getMessage());
		}					
	}

}
