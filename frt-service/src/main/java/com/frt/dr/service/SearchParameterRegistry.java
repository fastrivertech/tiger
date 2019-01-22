package com.frt.dr.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SearchParameterRegistry {
	private static final String FHIR_SEARCH_PARAMETERS_JSON="search-parameters.json";
	private static final JsonParser parser = new JsonParser();
	private static volatile SearchParameterRegistry instance = null;
	private static JsonElement bunndle = null;
	
	private SearchParameterRegistry() {
	}
	
	private void load() throws FileNotFoundException {
		//get json file from resources folder
		FileReader fr = new FileReader(new File(getClass().getClassLoader().getResource(FHIR_SEARCH_PARAMETERS_JSON).getFile()));
		this.bunndle = this.parser.parse(fr);
		// below code traverse bundle hierarchy and build parameter registry
		// to be added
	}

	public static SearchParameterRegistry getInstance() {
		if (instance == null) {
            synchronized (SearchParameterRegistry.class) {
                if (instance == null) {
                    instance = new SearchParameterRegistry();
                    try {
						instance.load();
					} catch (FileNotFoundException e) {
						throw new IllegalStateException("FHIR search parameters definition file: " + FHIR_SEARCH_PARAMETERS_JSON + " not found.");
					}
                }
            }
        }
        return instance;
    }
	
}
