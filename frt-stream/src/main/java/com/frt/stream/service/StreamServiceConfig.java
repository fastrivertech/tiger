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
package com.frt.stream.service;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;

/*
 * StreamServiceConfig class
 * @author chaye
 */
public class StreamServiceConfig {

	public static final String STREAM_TOPIC="frt.stream.topic";
	public static final String STREAM_ENABLE="frt.stream.enable";	
	
	private final static String STREAMSERVICE_CONFIGURATION_PATH = "./config/frt_stream.properties";
	private final static String PRODUCER_HEAD = "p.";
	private final static String CONSUMER_HEAD = "c.";
		
	private static StreamServiceConfig instance;
	private Properties props;
	
	private StreamServiceConfig() 
		throws StreamServiceException {
		InputStream is = null;
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			is = classLoader.getResourceAsStream(STREAMSERVICE_CONFIGURATION_PATH);
			props = new Properties();
			props.load(is);			
		} catch (FileNotFoundException fnfex) {
			throw new StreamServiceException(fnfex);
		} catch (IOException ioex) {
			throw new StreamServiceException(ioex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioex) {
				}
			}
		}
	}

	public Properties getProducerConfig() {
		Properties config = new Properties();
		props.forEach((k, v)-> {
			if (((String)k).startsWith("p.")) {
				config.put(((String)k).substring(2), v);
			}
		});
		return config;
	}

	public Properties getConsumerConfig() {
		Properties config = new Properties();
		props.forEach((k, v)-> {
			if (((String)k).startsWith("c.")) {
				config.put(((String)k).substring(2), v);
			}
		});
		return config;		
	}
	
	public String get(String key) {
		return props.getProperty(key);
	}
	
	public static StreamServiceConfig getInstance() 
		throws StreamServiceException {
		if (instance == null) {
			instance = new StreamServiceConfig();
		}
		return instance;
	}
	
}
