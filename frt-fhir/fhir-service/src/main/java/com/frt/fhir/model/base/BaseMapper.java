package com.frt.fhir.model.base;

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;

import ca.uhn.fhir.context.FhirContext;

public abstract class BaseMapper implements ResourceMapper {
	protected static String PAT_RS_HEAD = "{\"resourceType\": \"Patient\", \"id\": \"PAT1\",";
	protected static String PAT_RS_TAIL = "}";
	protected static String IDENTIFIER_BEGIN = "\"identifier\":[";
	protected static String IDENTIFIER_END = "]";

	protected ca.uhn.fhir.parser.JsonParser parser; // per mapper HAPI parser for json to object of HAPI type convert 

	public BaseMapper() {
		FhirContext context = FhirContext.forDstu3();
		parser = (ca.uhn.fhir.parser.JsonParser)context.newJsonParser();
	}
	
	@Override
	public abstract ResourceMapper from(Class source);

	@Override
	public abstract ResourceMapper to(Class target);

	@Override
	public abstract Object map(Object source) throws MapperException;

}
