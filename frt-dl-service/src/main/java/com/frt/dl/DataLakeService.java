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
package com.frt.dl;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * DataLakeService class
 * 
 * @author cqye
 */
public class DataLakeService {
	private FileSystem fs;

	public DataLakeService() {
	}

	public void initialize() throws DataLakeServiceException {
		try {
			String hdfsUrl = "hdfs://192.168.1.1:54310";
			Configuration config = new Configuration();
			config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
			config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());			
			FileSystem fs = FileSystem.get(new URI(hdfsUrl), config);
		} catch (IOException | URISyntaxException ex) {
			throw new DataLakeServiceException(ex);
		}
	}

	public void write() 
		throws DataLakeServiceException{
		try { 
			String username = "hadoopUser"; //name of the user having write permission on HDFS 
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
			
			ugi.doAs(new PrivilegedExceptionAction<Void>() {
			    public Void run() throws Exception { 			
					String text = "Hello, this is sample text to be written to a HDFS file"; 
			        //We will write text to the file /home/hadoop/example2.txt on HDFS
			        OutputStream outputStream = fs.create(new Path("/home/hadoop/example2.txt"));
			        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(outputStream));
			        br.write(text);
			        br.close();		
			        return null;
			    }
			});
			
		} catch (Exception ex) {
			throw new DataLakeServiceException(ex);
		}
	}

	public static void main(String[] args) {
		try {
			DataLakeService dl = new DataLakeService();
			dl.initialize();
			dl.write();
		} catch (DataLakeServiceException ex) {
			ex.printStackTrace();
		}
	}

}
