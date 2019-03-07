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

public class FhirMessageWriter implements ParticipatingApplication {

	private static int id = 0; 
	private Producer<String, String> producer;
	private StreamServiceConfig config;
	private ApplicationThread applicationThread;		
	
	public FhirMessageWriter() {
	}
	
	@Override
	public void initialize() 
		throws StreamApplicationException {
		try {
			config = StreamServiceConfig.getInstance();														
			Properties props = config.getProducerConfig(); 
			producer = new KafkaProducer<String, String>(props);
			producer.initTransactions();			
		} catch (StreamServiceException ssex) {
			throw new StreamApplicationException(ssex);
		}
	}
	
	@Override
	public void run() {	
	}
	
	public void send(String message) {
		try {
			producer.beginTransaction();
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>("FhirTopic", messageId + " " + message));
			producer.commitTransaction();
			System.out.println("    sent " + messageId + " " + message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		try {
			if (applicationThread != null) {
				applicationThread.close();
			}			
			if (producer != null) {
				producer.close();
			}
		} catch (KafkaException ex) {
		}
	}
	
	public static void main(String[] args) {
	}
}
