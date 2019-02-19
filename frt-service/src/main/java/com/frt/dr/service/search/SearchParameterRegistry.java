/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.service.search;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.XmlParser;
import org.hl7.fhir.dstu3.model.SearchParameter;
import org.hl7.fhir.dstu3.model.Bundle;

public class SearchParameterRegistry {

	private final static String FHIR_SEARCH_PARAMETERS_PATH = "./search-parameters.xml";
	
	private XmlParser parser;
	private List<SearchParameter> searchParameters;
	
	public SearchParameterRegistry() {
		FhirContext context = FhirContext.forR4();
		parser = (XmlParser)context.newXmlParser();		
		searchParameters = new ArrayList<>(); 
	}
	
	public void load() 
		throws SearchParameterException{
	    ClassLoader classLoader = this.getClass().getClassLoader();
	    try (InputStream is = classLoader.getResourceAsStream(FHIR_SEARCH_PARAMETERS_PATH)) {
			 Bundle bundle = parser.doParseResource(Bundle.class, new InputStreamReader(is));						
			 List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
			 entries.forEach(entry ->searchParameters.add((SearchParameter)entry.getResource()));	
			/* 
			   org.hl7.fhir.r4.model.SearchParameter
			   name/code/type/base/expression/xpath/multipleOr/multipleAnd/modifier/comparator
			*/
		} catch (FileNotFoundException fnfex) {
			throw new SearchParameterException(fnfex);
		} catch (IOException ioex) {
			throw new SearchParameterException(ioex);
		} 	
	}
	
}
