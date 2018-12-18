package com.frt.fhir.model.base;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.ResourceMapperFactory;

import ca.uhn.fhir.context.FhirContext;

public class PatientAndComponentMapperTest {
	protected void setUp() {
		// set env var DERBY_DB to YES so that logic processing Clobs will be executed
		Map<String, String> tmpEnv = new HashMap<String, String>();
		tmpEnv.put("DERBY_DB", "YES");
		try {
			this.setEnv(tmpEnv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void test() {
		ca.uhn.fhir.context.FhirContext context = FhirContext.forDstu3();
		ca.uhn.fhir.parser.JsonParser parser = (ca.uhn.fhir.parser.JsonParser) context.newJsonParser();

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

		assertNotNull("Patient.photo (FHIR Attachment[] expected.", frt.getPhotos());
		assertEquals("Patient.photo expected to have 2 elements.", frt.getPhotos().size(), 2);

		assertNotNull("Patient.telecom (FHIR ContactPoint[] expected.", frt.getTelecoms());
		assertEquals("Patient.telecom expected to have 4 elements.", frt.getTelecoms().size(), 4);

		assertNotNull("Patient.name (FHIR HumanName[] expected.", frt.getNames());
		assertEquals("Patient.name expected to have 3 elements.", frt.getNames().size(), 3);

		assertNotNull("Patient.managingOrganization (FHIR Reference(Organization)) Expected, but it is NULL.",
				frt.getManagingOrganization());
		assertNotNull("Patient.maritalStatus (FHIR CodeableConcept) Expected, but it is NULL.", frt.getMaritalStatus());
		String frtStr = BaseMapper.resourceToJson(frt);

		try {
			String gold = readFromFile("src/test/data/frt_patient_sample_gold.json");
//			System.out.println("frt=" + frtStr);
//			System.out.println("gold=" + gold);
			assertEquals("FRT Patient json does not match the cannonical json string.", gold, frtStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Gold file not found: " + f.getPath());
		}
	}

	protected void tearDown() {
		
	}
	
	private String readFromFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

	protected void setEnv(Map<String, String> newenv) throws Exception {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String, String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}
	}
}
