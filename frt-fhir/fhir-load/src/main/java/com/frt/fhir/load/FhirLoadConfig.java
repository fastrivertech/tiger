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
package com.frt.fhir.load;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FhirLoadConfig {
	
	public final static String FHIRLOAD_SOURCE_DIR = "frt.fhir.load.source.dir";
	public final static String FHIRLOAD_TARGET_DIR = "frt.fhir.load.target.dir";
	
	private final static String FHIRLOAD_CONFIGURATION_PATH = "config/frt_load.properties";
		
	private static FhirLoadConfig instance;
	private Properties props;

	private FhirLoadConfig() 
		throws FhirLoadException {
		File filePath = new File(FHIRLOAD_CONFIGURATION_PATH);
		try (InputStream is = new FileInputStream(filePath)) {
			props = new Properties();
			props.load(is);			
		} catch(IOException ex) {
			throw new FhirLoadException(ex);
		}
	}

	public String get(String key) {
		return props.getProperty(key);
	}

	public static FhirLoadConfig getInstance() 
		throws FhirLoadException {
		if (instance == null) {
			instance = new FhirLoadConfig();
		}
		return instance;
	}
	
}
