/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.Assert.fail;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * JsonParser class
 * 
 * @author cqye
 */
public class JsonParserTest {

	@Test
	public <R extends DomainResource> void test() {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get("./src/test/data/general-patient-example.json"));
			String jsonPatient = new String(bytes);
			Patient patient = new JsonParser().deserialize("Patient", jsonPatient);			
			System.out.println("id = " + patient.getId());
			System.out.println("active = " + Boolean.toString(patient.getActive()));
			System.out.println("gender = " + patient.getGender().toString());
			System.out.println("name.id = " + patient.getName().get(0).getId());			
			System.out.println("name.use = " + patient.getName().get(0).getUse().toString());
			System.out.println("name.family = " + patient.getName().get(0).getFamily());			
			assertTrue(true);
		} catch (IOException ioex) {
			fail(ioex.getMessage());
		}
	}

}
