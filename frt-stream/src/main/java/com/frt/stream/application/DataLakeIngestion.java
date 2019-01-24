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
package com.frt.stream.application;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;
import com.frt.dl.service.DataLakeService;
import com.frt.dl.service.DataLakeServiceException;

/*
 * DataLakeIngestion class
 * @author chaye
 */
public class DataLakeIngestion implements ParticipatingApplication {

	private Consumer<String, String> consumer;
	private StreamServiceConfig config;
	private DataLakeService dataLakeService;
	private ApplicationThread applicationThread;	
	private CountDownLatch latch;
		
	public DataLakeIngestion(){		
	}
	
	@Override
	public void initialize() 
		throws StreamApplicationException {
		try {
			config = StreamServiceConfig.getInstance();
			consumer = new KafkaConsumer<>(config.getApplicationConfig(StreamServiceConfig.DATALAKE_INGESTION_APPLICATION));
			consumer.subscribe(Collections.singletonList(config.get(StreamServiceConfig.STREAM_TOPIC)));
			
			dataLakeService = new DataLakeService();
			dataLakeService.initialize();
			applicationThread = new ApplicationThread(this);
			
			latch = new CountDownLatch(1);
			Runtime.getRuntime().addShutdownHook(new Thread("datalake-ingestion-shutdown-hook") {
				@Override
				public void run() {
					System.out.println("fhir datalake ingestion stopped ...");
					close();
					latch.countDown();
				}
			});
			
		} catch (StreamServiceException | 
				 DataLakeServiceException ex) {
			throw new StreamApplicationException(ex);
		}		
	}
	
	public void ingest() 
		throws StreamApplicationException {
		try {
			ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
			consumerRecords.forEach(record -> {
				if (record.key().contains("write")) {
					try {
						dataLakeService.write(record.value());
					} catch (DataLakeServiceException ex) {
					}
				}
			});									
			consumer.commitSync();
		} catch (KafkaException ex) {
			throw new StreamApplicationException(ex);
		}		
	}
	
	@Override
	public void run() {
		try {
			ingest();
		} catch (StreamApplicationException ex) {
			throw ex;
		}
	}
	
	public void start() 
		throws StreamApplicationException {
		try {			
			System.out.println("fhir datalake ingestion running ...");
			applicationThread.start();
			latch.await();
			System.out.println("fhir datalake ingestion stopped ...");			
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
			if (dataLakeService != null) {
				dataLakeService.close();
			}
			if (consumer != null) {				
				consumer.close();
				consumer.unsubscribe();
			}
		} catch (KafkaException ex) {			
		}		
	}

	public static void main(String[] args) {
		try {
			DataLakeIngestion ingestion = new DataLakeIngestion();
			ingestion.initialize();
			ingestion.start();
			System.exit(0);
			System.out.println("datalake ingestion application exit(0)");
		} catch (StreamApplicationException ex) {
			System.exit(1);
			System.out.println("datalake ingestion application exit(1)");
		}
	}
	
}
