package com.frt.fhir.model.base;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;

public class HAPIDataTypeParserTest {
	private static final String MARITAL_STATUS = "\"codeableconcept\": {\r\n" + 
			"    \"coding\": { \r\n" + 
			"      \"system\": \"http://hl7.org/fhir/v3/MaritalStatus\",\r\n" + 
			"      \"code\": \"M\",\r\n" + 
			"      \"display\": \"Married\" \r\n" + 
			"    },\r\n" + 
			"    \"text\": \"Getrouwd\" \r\n" + 
			"  }";
	private static final String MARITAL_STATUS_VAL = "{\r\n" + 
			"    \"coding\": { \r\n" + 
			"      \"system\": \"http://hl7.org/fhir/v3/MaritalStatus\",\r\n" + 
			"      \"code\": \"M\",\r\n" + 
			"      \"display\": \"Married\" \r\n" + 
			"    },\r\n" + 
			"    \"text\": \"Getrouwd\" \r\n" + 
			"  }";
	private static final String BUSINESS_IDENTIFIERS = " \"identifierArray\": [\r\n" + 
			"    {\r\n" + 
			"      \"use\": \"usual\",\r\n" + 
			"      \"type\": {\r\n" + 
			"        \"coding\": [\r\n" + 
			"          {\r\n" + 
			"            \"system\": \"http://hl7.org/fhir/v2/0203\",\r\n" + 
			"            \"code\": \"MR\"\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\r\n" + 
			"      \"value\": \"12345\",\r\n" + 
			"      \"period\": {\r\n" + 
			"        \"start\": \"2001-05-06\"\r\n" + 
			"      },\r\n" + 
			"      \"assigner\": {\r\n" + 
			"        \"display\": \"Acme Healthcare\"\r\n" + 
			"      }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"use\": \"secondary\",\r\n" + 
			"      \"type\": {\r\n" + 
			"        \"coding\": [\r\n" + 
			"          {\r\n" + 
			"            \"system\": \"http://hl7.org/fhir/v2/0203\",\r\n" + 
			"            \"code\": \"DR\"\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"system\": \"urn:oid:1.2.36.146.595.217.0.5\",\r\n" + 
			"      \"value\": \"54321\",\r\n" + 
			"      \"period\": {\r\n" + 
			"        \"start\": \"2001-06-16\", \"end\": \"2010-07-26\"\r\n" + 
			"      },\r\n" + 
			"      \"assigner\": {\r\n" + 
			"        \"display\": \"Acme Healthcare Partner Inc.\"\r\n" + 
			"      }\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			" ";
	private static final String BUSINESS_IDENTIFIERS_VAL = "[\r\n" + 
			"    {\r\n" + 
			"      \"use\": \"usual\",\r\n" + 
			"      \"type\": {\r\n" + 
			"        \"coding\": [\r\n" + 
			"          {\r\n" + 
			"            \"system\": \"http://hl7.org/fhir/v2/0203\",\r\n" + 
			"            \"code\": \"MR\"\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\r\n" + 
			"      \"value\": \"12345\",\r\n" + 
			"      \"period\": {\r\n" + 
			"        \"start\": \"2001-05-06\"\r\n" + 
			"      },\r\n" + 
			"      \"assigner\": {\r\n" + 
			"        \"display\": \"Acme Healthcare\"\r\n" + 
			"      }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"use\": \"secondary\",\r\n" + 
			"      \"type\": {\r\n" + 
			"        \"coding\": [\r\n" + 
			"          {\r\n" + 
			"            \"system\": \"http://hl7.org/fhir/v2/0203\",\r\n" + 
			"            \"code\": \"DR\"\r\n" + 
			"          }\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"system\": \"urn:oid:1.2.36.146.595.217.0.5\",\r\n" + 
			"      \"value\": \"54321\",\r\n" + 
			"      \"period\": {\r\n" + 
			"        \"start\": \"2001-06-16\", \"end\": \"2010-07-26\"\r\n" + 
			"      },\r\n" + 
			"      \"assigner\": {\r\n" + 
			"        \"display\": \"Acme Healthcare Partner Inc.\"\r\n" + 
			"      }\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			" ";
	protected static String CUST_RS_BEGIN = "{\"resourceType\": \"HAPIComplexTypesResource\",";
	protected static String CUST_RS_END = "}";
	protected ca.uhn.fhir.parser.JsonParser parser;
	protected BaseMapper mapper;
	
	@Before
	public void setUp() {
		System.setProperty("frt.persist.store.derby", "TRUE");
		System.out.println("frt.persist.store.derby ==> TRUE");
//		FhirContext context = FhirContext.forDstu3();
//		parser = (ca.uhn.fhir.parser.JsonParser) context.newJsonParser();
		mapper = new BaseMapper() {

			@Override
			public ResourceMapper from(Class source) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ResourceMapper to(Class target) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object map(Object source) throws MapperException {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
	@Test
	public void test() {
		org.hl7.fhir.dstu3.model.CodeableConcept ms = mapper.parseComplexType(org.hl7.fhir.dstu3.model.CodeableConcept.class, MARITAL_STATUS_VAL);
		
		List<org.hl7.fhir.dstu3.model.Identifier> ids = mapper.parseComplexTypeArray(org.hl7.fhir.dstu3.model.Identifier.class, BUSINESS_IDENTIFIERS_VAL);
		// check CodeableConcept object
		assertTrue("Expect 1 coding element in a coding[] in a CodeableConcept object.", ms.getCoding()!=null&&ms.getCoding().size()==1);
		assertTrue("Expect text in CodeableConcept object.", (ms.getText()!=null&&ms.getText().equals("Getrouwd")));
		// check Identifier[]
		assertTrue("Expect an array of 2 Identifier objects.", ids!=null&&ids.size()==2);
		// check Identifier object 1:
		
		assertTrue("Expect 1st Identifier.use as 'usual'.", ids.get(0).getUse().toCode().equals("usual"));
		assertNotNull("Expect 1st Identifier.type present.", ids.get(0).getType());
		assertNotNull("Expect 1st Identifier.system present.", ids.get(0).getSystem());
		assertNotNull("Expect 1st Identifier.assigner present.", ids.get(0).getAssigner());
		assertNotNull("Expect 1st Identifier.period present.", ids.get(0).getPeriod());
		// check Identifier object 2:
		assertTrue("Expect 2nd Identifier.use as 'secondary'", ids.get(1).getUse().toCode().equals("secondary"));
		assertNotNull("Expect 2nd Identifier.type present.", ids.get(1).getType());
		assertNotNull("Expect 2nd Identifier.system present.", ids.get(1).getSystem());
		assertNotNull("Expect 2nd Identifier.assigner present.", ids.get(1).getAssigner());
		assertNotNull("Expect 2nd Identifier.period present.", ids.get(1).getPeriod());
	}

	@After
	public void tearDown() {
		
	}
}
