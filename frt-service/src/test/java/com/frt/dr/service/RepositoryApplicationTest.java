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
			patient.setPatientId(Long.valueOf(10000));
			patient.setActive(true);
			patient.setGender("MALE");
			PatientHumanName name = new PatientHumanName();
			name.setHumannameId(Long.valueOf(10000));
			name.setPatientId(Long.valueOf(10000));
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
