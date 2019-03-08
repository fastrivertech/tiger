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
package com.frt.dl.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * DataLakeServiceConfig class
 * @author chaye
 */
public class DataLakeServiceConfig {

	public static final String DATALAKE_USER="frt.datalake.user";
	public static final String DATALAKE_URL="frt.datalake.url";
	public static final String DATALAKE_PATH="frt.datalake.path";

	private static final String DATALAKESERVICE_CONFIGURATION_PATH = "config/frt_datalake.properties";		
	private final static String FS_HDFS = "fs.";

	private static DataLakeServiceConfig instance;
	private Properties props;

	private DataLakeServiceConfig() throws DataLakeServiceException {
		InputStream is = null;
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			is = classLoader.getResourceAsStream(DATALAKESERVICE_CONFIGURATION_PATH);
			props = new Properties();
			props.load(is);
		} catch (FileNotFoundException fnfex) {
			throw new DataLakeServiceException(fnfex);
		} catch (IOException ioex) {
			throw new DataLakeServiceException(ioex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioex) {
				}
			}
		}
	}
	
	public Properties getHdfsConfig() {
		Properties config = new Properties();
		props.forEach((k, v)-> {
			if (((String)k).startsWith("fs.")) {
				config.put(k, v);
			}
		});
		return config;
	}
	
	public String get(String key) {
		return props.getProperty(key);
	}
	
	public static DataLakeServiceConfig getInstance() 
		throws DataLakeServiceException {
		if (instance == null) {
			instance = new DataLakeServiceConfig();
		}
		return instance;
	}
		
}
