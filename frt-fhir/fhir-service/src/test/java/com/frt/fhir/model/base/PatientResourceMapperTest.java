package com.frt.fhir.model.base;

import static org.junit.Assert.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.junit.Test;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.model.ResourceMapperFactory;

public class PatientResourceMapperTest {

	@Test
	public void test() {
		Patient patient1 = new Patient();
		patient1.setId("10000");
		patient1.setActive(true);
		patient1.setGender(AdministrativeGender.MALE);
		HumanName name = new HumanName();
		name.setId("10000");
		name.setUse(NameUse.OFFICIAL);
		name.setFamily("Charles");
		patient1.getName().add(name);
		ResourceMapper mapper = ResourceMapperFactory.getInstance().create("Patient");		
		ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("Patient");
		// from fhir to frt
		Object target1 = mapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map((Object)patient1);		
		// from frt to fhir
		Object target2 = mapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map((Object)target1);		
		Patient patient2 = (Patient)target2;
		System.out.println("id = " + patient2.getId());
		System.out.println("active = " + Boolean.toString(patient2.getActive()));
		System.out.println("gender = " + patient2.getGender().toString());
		System.out.println("name.id = " + patient2.getName().get(0).getId());			
		System.out.println("name.use = " + patient2.getName().get(0).getUse().toString());
		System.out.println("name.family = " + patient2.getName().get(0).getFamily());		
	}

	
}
