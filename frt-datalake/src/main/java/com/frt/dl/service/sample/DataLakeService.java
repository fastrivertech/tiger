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
package com.frt.dl.service.sample;

import java.net.URI;
import java.net.URISyntaxException;
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

import com.frt.dl.service.DataLakeServiceException;

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
			System.setProperty("HADOOP_USER_NAME", "cloudera");
			String hdfsUrl = "hdfs://10.0.0.21:8020";
			Configuration config = new Configuration();
			config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
			config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());		
			config.set("fs.hdfs.impl.disable.cache", "true");
			fs = FileSystem.get(new URI(hdfsUrl), config);
		} catch (IOException | URISyntaxException ex) {
			throw new DataLakeServiceException(ex);
		}
	}

	public void write() 
		throws DataLakeServiceException{
		try { 
			String username = "ec2-user"; //name of the user having write permission on HDFS 
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
			/*
			ugi.doAs(new PrivilegedExceptionAction<Void>() {
			    public Void run() throws Exception { 			
					String text = "Hello, this is sample text to be written to a HDFS file"; 
			        //We will write text to the file /home/hadoop/example2.txt on HDFS
			        OutputStream outputStream = fs.create(new Path("/user/frt/datalake/example2.txt"));
			        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(outputStream));
			        br.write(text);
			        br.close();		
			        return null;
			    }
			});
			*/			
			System.out.println("started to write ...");	
			String text = "Hello, this is sample text to be written to a HDFS file"; 
	        //We will write text to the file /home/hadoop/example2.txt on HDFS
			Path path = new Path("/user/cloudera/example1.txt");
	        OutputStream outputStream = fs.create(path);
			fs.setPermission(path, new FsPermission("777"));        
	        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(outputStream));
	        br.write(text);
	        br.close();		
			fs.close();
			System.out.println("finished writting ...");				
		} catch (Exception ex) {
			throw new DataLakeServiceException(ex);
		}
	}

	public void read() 
		throws DataLakeServiceException{
		try { 
			String text = "Hello, this is sample text to be written to a HDFS file"; 
	        //We will write text to the file /home/hadoop/example2.txt on HDFS
			Path path = new Path("/user/cloudera/example1.txt");
			fs.setPermission(path, new FsPermission("777"));        
	        InputStream inputStream = fs.open(path);
			System.out.println("started to read ...");				
	        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        String line = br.readLine();
	        while (line != null) {
	        	System.out.println(line);
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
		  //dl.write();
		    dl.read();
			System.exit(0);
		} catch (DataLakeServiceException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
