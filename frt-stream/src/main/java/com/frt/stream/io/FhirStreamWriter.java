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
package com.frt.stream.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.frt.stream.application.ApplicationThread;
import com.frt.stream.application.ParticipatingApplication;
import com.frt.stream.application.StreamApplicationException;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

public class FhirStreamWriter implements ParticipatingApplication {

	private static int id = 0; 
	private Producer<String, String> producer;
	private StreamServiceConfig config;
	private String dataFolder;

	public FhirStreamWriter() {
	}
	
	@Override
	public void initialize() 
		throws StreamApplicationException {
		try {
			config = StreamServiceConfig.getInstance();														
			Properties props = config.getProducerConfig(); 
			producer = new KafkaProducer<String, String>(props);
			producer.initTransactions();		
						
			System.out.println("fhir stream writer connecting to fhir stream [" + 
					config.get(StreamServiceConfig.STREAM_TOPIC) + 
					"] on stream broker [" +
					config.getProducerConfig().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG) + "] ...");
			
		} catch (StreamServiceException ssex) {
			throw new StreamApplicationException(ssex);
		}
	}

	public void initialize(String folder) 
		throws StreamApplicationException {
		if (folder == null) {
			dataFolder = "./data";
		} else {
			dataFolder = folder;
		}
		this.initialize();
	}	
	
	@Override
	public void run() {
		try {
			File folder = new File(dataFolder);
			File[] files = folder.listFiles();
			Stream<File> stream = Arrays.stream(files);
			stream.forEach(file->{
				try {
					if (file.getName().endsWith(".json")) {
						String message = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
						send(message);
						rename(file.getAbsolutePath());						
					}
				} catch (IOException ioex) {																																																																																																																						
				}
			});			
		} catch ( Exception ex) {
			throw new StreamApplicationException(ex);
		}
	}
		
	public void send(String message) {
		try {
			producer.beginTransaction();
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>(config.get(StreamServiceConfig.STREAM_TOPIC), messageId, message));
			producer.commitTransaction();
			System.out.println("    sent " + messageId + " " + message);
		} catch (Exception ex) {
		}
	}
	
	public void rename(String name) 
		throws StreamApplicationException { 		
		File oldName = new File(name);
		if (!oldName.exists()) {
			throw new StreamApplicationException("old file '" + name + "' does not exist");			
		}
		File newName = new File(name + "~");
		if (newName.exists()) {
			throw new StreamApplicationException("nex file '" + name + "' exists");			
		}		
		if (!oldName.renameTo(newName)) {
			throw new StreamApplicationException("failed to rename '" + name + "' to '" + name + "~'");
		}
		System.out.println(oldName.getName() + " renamed to " + newName.getName());
	}
	
	@Override
	public void close() {
		try {
			if (producer != null) {
				producer.close();
			}
		} catch (KafkaException ex) {
		}
	}
	
	public static void main(String[] args) {
		try {
			FhirStreamWriter writer = new FhirStreamWriter();
			writer.initialize(args[0]);
			writer.run();
			writer.close();
			System.out.println("fhir stream writer application exit(0)");
			System.exit(0);			
		} catch (StreamApplicationException ex) {
			System.exit(1);
			System.out.println("fhir stream writer application exit(1)");			
		}		
	}
}
