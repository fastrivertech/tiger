package com.frt.fhir.mpi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

public class MpiProviderLocator {

	private Properties props;
	
	public void config() 
		throws MpiProviderException {
		try (FileInputStream is = new FileInputStream(new File("./config/patient.mpi.properties"))) {
			props = new Properties();
			props.load(is);
		} catch (IOException ex) {
			throw new MpiProviderException(ex);
		}
	}
	
}
