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

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import com.frt.stream.application.ApplicationThread;
import com.frt.stream.application.ParticipatingApplication;
import com.frt.stream.application.StreamApplicationException;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

public class FhirStreamReader implements ParticipatingApplication {

	private Consumer<Long, String> consumer;
	private StreamServiceConfig config;
	private ApplicationThread applicationThread;		
	private CountDownLatch latch;

	public FhirStreamReader() {
	}

	@Override
	public void initialize() 
		throws StreamApplicationException {
		try {
			config = StreamServiceConfig.getInstance();											
			consumer = new KafkaConsumer<>(config.getConsumerConfig());
			consumer.subscribe(Collections.singletonList(config.get(StreamServiceConfig.STREAM_TOPIC)));
			
			System.out.println("fhir message reader connecting to fhir stream [" + 
					config.get(StreamServiceConfig.STREAM_TOPIC) + 
					"] on stream broker [" +
					config.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG) + "] ...");
			
			applicationThread = new ApplicationThread(this);	
			
			latch = new CountDownLatch(1);
			Runtime.getRuntime().addShutdownHook(new Thread("fhir-message-reader-shutdown-hook") {
				@Override
				public void run() {
					latch.countDown();
				}
			});
			
		} catch (StreamServiceException ssex) {
			throw new StreamApplicationException(ssex);
		}
	}	
	
	@Override
	public void run() {
		try {
			receives();
		} catch (StreamApplicationException ex) {
			throw ex;
		}
	}
	
	public void receives() {
		int count = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if (consumerRecords.count() == 0) {
				count++;
				if (count > 300)
					break;
				else
					continue;
			}
			consumerRecords.forEach(record -> {
				System.out.printf("    received: (%s, %s, %d, %d)\n", 
						           record.key(), record.value(), record.partition(), record.offset());
			});
			consumer.commitAsync();
		}
	}
		
	public void start() 
		throws StreamApplicationException {
		try {			
			System.out.println("fhir message reader running ...");
			applicationThread.start();
			latch.await();
			close();
			System.out.println("fhir message reader stopped ...");			
		} catch (KafkaException | IllegalStateException | InterruptedException ex) {
			throw new StreamApplicationException(ex);
		}
	}
	
	@Override
	public void close() {
		try {
			if (applicationThread != null) {
				applicationThread.close();
			}			
			if (consumer != null) {
				consumer.close();
			}
		} catch (KafkaException ex) {
		}
	}

	public static void main(String[] args) {
		try {
			FhirStreamReader reader = new FhirStreamReader();
			reader.initialize();
			
			System.out.println("fhir message reader application exit(0)");
			System.exit(0);			
		} catch (StreamApplicationException ex) {
			System.exit(1);
			System.out.println("fhir message reader application exit(1)");			
		}
	}
	
}