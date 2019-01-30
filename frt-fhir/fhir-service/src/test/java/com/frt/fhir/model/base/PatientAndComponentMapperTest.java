package com.frt.fhir.model.base;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.frt.dr.model.base.PatientIdentifier;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapperInterface;
import com.frt.fhir.model.ResourceMapperFactory;

import ca.uhn.fhir.context.FhirContext;

public class PatientAndComponentMapperTest {
	@Before
	public void setUp() {
	}
	@Test
	public void test() {
		ca.uhn.fhir.context.FhirContext context = FhirContext.forDstu3();
		ca.uhn.fhir.parser.JsonParser parser = (ca.uhn.fhir.parser.JsonParser) context.newJsonParser();

		ResourceMapperInterface mapper = ResourceDictionary.getMapper(ResourceMapperInterface.PATIENT);

		File f = new File("src/test/data/fhir_patient_resource_sample_6k.json");
		
		FileReader fr = null;
		
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("File not found: " + f.getPath());
		}

		org.hl7.fhir.dstu3.model.Patient hapi = parser.doParseResource(org.hl7.fhir.dstu3.model.Patient.class, fr);
		ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get(ResourceMapperInterface.PATIENT);
		Object target = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(hapi);

		assertTrue("Expecting FRT Patient.", (target instanceof com.frt.dr.model.base.Patient));
		com.frt.dr.model.base.Patient frt = (com.frt.dr.model.base.Patient) target;

		assertTrue("Patient.deceasedBoolean should be false",
				frt.getDeceasedBoolean() != null && !frt.getDeceasedBoolean().booleanValue());
		assertTrue("Patient.deceasedDateTime should be NULL", frt.getDeceasedDateTime() == null);

		assertTrue("Patient.multipleBirthBoolean should be NULL", frt.getMultipleBirthBoolean() == null);
		assertTrue("Patient.multipleBirthInteger should be 3",
				frt.getMultipleBirthInteger() != null && (frt.getMultipleBirthInteger().intValue() == 3));

		assertNotNull("Patient.address (FHIR Address[]) expected.", frt.getAddresses());
		assertEquals("Patient.address expected to have 1 elements.", frt.getAddresses().size(), 1);

		assertNotNull("Patient.identifier (FHIR Identifier[] expected.", frt.getIdentifiers());
		assertEquals("Patient.identifier expected to have 2 elements.", frt.getIdentifiers().size(), 2);

		List<PatientIdentifier> ids = frt.getIdentifiers();
		int count = 0;
		for (PatientIdentifier id: ids) {
			assertNotNull("Patient.identifier[" + count + "] expect a 'type' field.", id.getType());
			assertNotNull("Patient.identifier[" + count + "] expect a 'period' field.", id.getPeriod());
			assertNotNull("Patient.identifier[" + count + "] expect a 'assigner' field.", id.getAssigner());
			count++;
		}
		
		assertNotNull("Patient.photo (FHIR Attachment[] expected.", frt.getPhotos());
		assertEquals("Patient.photo expected to have 2 elements.", frt.getPhotos().size(), 2);

		assertNotNull("Patient.telecom (FHIR ContactPoint[] expected.", frt.getTelecoms());
		assertEquals("Patient.telecom expected to have 4 elements.", frt.getTelecoms().size(), 4);

		assertNotNull("Patient.name (FHIR HumanName[] expected.", frt.getNames());
		assertEquals("Patient.name expected to have 3 elements.", frt.getNames().size(), 3);

		assertNotNull("Patient.managingOrganization (FHIR Reference(Organization)) Expected, but it is NULL.",
				frt.getManagingOrganization());
		assertNotNull("Patient.maritalStatus (FHIR CodeableConcept) Expected, but it is NULL.", frt.getMaritalStatus());

		assertNotNull("Patient.animal (FHIR BackboneElement) Expected, but it is NULL.", frt.getAnimal());

		assertNotNull("Patient.communication (FHIR BackboneElement) Expected, but it is NULL.", frt.getCommunications());
		assertEquals("Patient.communication (FHIR BackboneElement[]) Expected, 1 element expected.", 1, frt.getCommunications().size());

		assertNotNull("Patient.link (FHIR BackboneElement) Expected, but it is NULL.", frt.getLinks());
		assertEquals("Patient.link (FHIR BackboneElement[]) Expected, 1 element expected.", 1, frt.getLinks().size());
		
		assertNotNull("Patient.contact (FHIR BackboneElement) Expected, but it is NULL.", frt.getContacts());
		assertEquals("Patient.contact (FHIR BackboneElement[]) Expected, 1 element expected.", 1, frt.getContacts().size());

		// cover Patient.contact FHIR Reference[]
		String address = frt.getContacts().get(0).getAddress();
		assertTrue("Patient.contact(0).address not empty.", !address.isEmpty());
		String relationship = frt.getContacts().get(0).getRelationship();
		assertTrue("Patient.contact(0).relationship not empty.", !relationship.isEmpty());
		String name = frt.getContacts().get(0).getName();
		assertTrue("Patient.contact(0).name not empty.", !name.isEmpty());
		String telecom = frt.getContacts().get(0).getTelecom();
		assertTrue("Patient.contact(0).telecom not empty.", !telecom.isEmpty());

		// cover Patient.generalPractitioner FHIR Reference[]
		assertNotNull("Expect Patient.generalPractitioner here.", frt.getGeneralPractitioners());
		assertEquals("Expect Patient.generalPractitioner 1 element.", 1, frt.getGeneralPractitioners().size());
		String ref = frt.getGeneralPractitioners().get(0).getReference();
		assertTrue("Patient.generalPractitioner(0).reference not empty.", !ref.isEmpty());
		assertNotNull("Patient.generalPractitioner(0).identifier not empty.", frt.getGeneralPractitioners().get(0).getIdentifier());
		String display = frt.getGeneralPractitioners().get(0).getDisplay();
		assertTrue("Patient.generalPractitioner(0).display not empty.", !display.isEmpty());

		assertNotNull("Expect Resource.meta", frt.getMeta());
		assertNotNull("Expect DomainResource.text", frt.getTxt());
		System.out.println("language=" + frt.getLanguage());
		assertTrue("Expect Resource.language = ru", frt.getLanguage()!=null&&frt.getLanguage().equals("ru"));
		System.out.println("contained=" + frt.getContained());
		assertNotNull("Expect DomainResource.contained.", frt.getContained());
		
		String frtStr = BaseMapper.resourceToJson(frt);

//		writeFile("src/test/data/frt_patient_sample_gold.json", frtStr);
		
		try {
			String gold = readFromFile("src/test/data/frt_patient_sample_gold.json");
			System.out.println("frt=" + frtStr);
			System.out.println("gold=" + gold);
			assertEquals("FRT Patient json does not match the cannonical json string.", gold, frtStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Gold file not found: src/test/data/frt_patient_sample_gold.json");
		}
	}

	@After
	public void tearDown() {
		
	}
	
	private String readFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

	private void writeFile(String filePath, String content) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(new File(filePath));
			fw.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (fw!=null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
