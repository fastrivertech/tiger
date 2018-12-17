package com.frt.fhir.model.base;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.ResourceMapperFactory;

import ca.uhn.fhir.context.FhirContext;

public class PatientAndComponentMapperTest {

	@Test
	public void test() {
		ca.uhn.fhir.context.FhirContext context = FhirContext.forDstu3();
		ca.uhn.fhir.parser.JsonParser parser = (ca.uhn.fhir.parser.JsonParser)context.newJsonParser();

		ResourceMapperFactory factory = ResourceMapperFactory.getInstance();
		ResourceMapper mapper = factory.create("Patient");
		File f = new File("src/test/data/fhir_patient_resource_sample.json");
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("File not found: " + f.getPath());
		}

		org.hl7.fhir.dstu3.model.Patient hapi = parser.doParseResource(org.hl7.fhir.dstu3.model.Patient.class, fr);
		ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT");
		Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(hapi);

		assertTrue("Expecting FRT Patient.", (target instanceof com.frt.dr.model.base.Patient));
		com.frt.dr.model.base.Patient frt = (com.frt.dr.model.base.Patient)target;

		assertTrue("Patient.deceasedBoolean should be false", frt.getDeceasedBoolean()!=null&&!frt.getDeceasedBoolean().booleanValue());
		assertTrue("Patient.deceasedDateTime should be NULL", frt.getDeceasedDateTime()==null);

		assertTrue("Patient.multipleBirthBoolean should be NULL", frt.getMultipleBirthBoolean()==null);
		assertTrue("Patient.multipleBirthInteger should be 3", frt.getMultipleBirthInteger()!=null&&(frt.getMultipleBirthInteger().intValue()==3));

		assertNotNull("Patient.address (FHIR Address[]) expected.", frt.getAddresses());
		assertEquals("Patient.address expected to have 1 elements.", frt.getAddresses().size(), 1);

		assertNotNull("Patient.identifier (FHIR Identifier[] expected.", frt.getIdentifiers());
		assertEquals("Patient.identifier expected to have 2 elements.", frt.getIdentifiers().size(), 2);

		assertNotNull("Patient.photo (FHIR Attachment[] expected.", frt.getPhotos());
		assertEquals("Patient.photo expected to have 2 elements.", frt.getPhotos().size(), 2);

		assertNotNull("Patient.telecom (FHIR ContactPoint[] expected.", frt.getTelecoms());
		assertEquals("Patient.telecom expected to have 4 elements.", frt.getTelecoms().size(), 4);

		assertNotNull("Patient.name (FHIR HumanName[] expected.", frt.getNames());
		assertEquals("Patient.name expected to have 3 elements.", frt.getNames().size(), 3);

		assertNotNull("Patient.managingOrganization (FHIR Reference(Organization)) Expected, but it is NULL.", frt.getManagingOrganization());
		assertNotNull("Patient.maritalStatus (FHIR CodeableConcept) Expected, but it is NULL.", frt.getMaritalStatus());
	
	}

}
