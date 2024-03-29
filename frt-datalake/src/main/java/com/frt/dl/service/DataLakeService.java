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
package com.frt.dl.service;

import java.util.Properties;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import com.frt.dl.DataLakeUtils;

/**
 * DataLakeService class
 * 
 * @author cqye
 */
public class DataLakeService {

	private DataLakeServiceConfig config;
	private Configuration hdfsConfig;
	
	public DataLakeService() {
	}

	public void initialize() 
		throws DataLakeServiceException {

		config = DataLakeServiceConfig.getInstance();
		System.setProperty("HADOOP_USER_NAME", config.get(DataLakeServiceConfig.DATALAKE_USER));

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		hdfsConfig.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

		Properties props = config.getHdfsConfig();
		props.forEach((k, v) -> {
			hdfsConfig.set((String) k, (String) v);
		});

	}

	public void close() {
	}
	
	public void write(String message) 
		throws DataLakeServiceException {
		try { 
			System.out.println("Data Lake Service is writting a message ...");
			FileSystem fs = FileSystem.get(new URI(config.get(DataLakeServiceConfig.DATALAKE_URL)), hdfsConfig);			
			String path = config.get(DataLakeServiceConfig.DATALAKE_PATH) + DataLakeUtils.partitioned();			
			Path fileName = new Path(path + System.currentTimeMillis() + ".json");
			
	        OutputStream outputStream = fs.create(fileName);
			fs.setPermission(fileName, new FsPermission("777"));        	        
	        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(outputStream));
	        
	        System.out.println("started to ingest " + path + fileName.getName() + " to fhir datalake ...");	
	        br.write(message);
	        br.close();		
			fs.close();
			System.out.println("finished ingesting " + path + fileName.getName() + " to fhir datalake ...");
			
		} catch (Exception ex) {
			throw new DataLakeServiceException(ex);
		}
	}
	
	public void read(String message) 
		throws DataLakeServiceException {
		try {
			FileSystem fs = FileSystem.get(new URI(config.get(DataLakeServiceConfig.DATALAKE_URL)), hdfsConfig);			
			String fileName = config.get(DataLakeServiceConfig.DATALAKE_PATH) + message + ".json";
			Path path = new Path(fileName);
	        InputStream inputStream = fs.open(path);
	        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        
			System.out.println("started to read " + path + " ...");				
	        String line = br.readLine();
	        while (line != null) {
	        	System.out.println(" => " + line);
	        	line = br.readLine();
	        }	        
	        br.close();		
	        fs.close();	
	        System.out.println("finished reading ...");
	        
		} catch (Exception ex) {
			throw new DataLakeServiceException(ex);			
		}
	}

	public static void main(String[] args) {
		
		try {
			DataLakeService dl = new DataLakeService();
			dl.initialize();
			if (args == null || 
				args.length == 0 || 
				args[0].equalsIgnoreCase("write")) {
				dl.write("test message 1");
		    } else if (args[0].equalsIgnoreCase("read")) {
		    	dl.read(args[1]);
		    }
			System.exit(0);
		} catch (DataLakeServiceException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		
	}

}
