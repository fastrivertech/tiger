package com.frt.mpi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import com.sun.mdm.index.webservice.client.PatientEJB;
import com.sun.mdm.index.webservice.client.PatientEJBService;


public class MpiProviderLocator {

	private Properties props;
	private static MpiProviderLocator instance;
	
	public static MpiProviderLocator getInstance() {
		if (instance == null) {
			instance = new MpiProviderLocator();
		}
		return instance;
	}
	
	public void config() 
		throws MpiProviderException {
		try (FileInputStream is = new FileInputStream(new File("./config/patient.mpi.properties"))) {
			props = new Properties();
			props.load(is);
		} catch (IOException ex) {
			throw new MpiProviderException(ex);
		}
	}
	
	public PatientEJB mpi() {
		 PatientEJBService service = new PatientEJBService();
		 return service.getPatientEJBPort();
	}
}
