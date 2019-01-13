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
package com.frt.stream.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.time.Duration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;

import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

import org.apache.kafka.common.KafkaException;

/*
 * FhirConsumer class
 * @author chaye
 */
public class FhirConsumer implements ParticipatingApplication {

	private Consumer<String, String> consumer;
	private StreamServiceConfig config;
	
	public FhirConsumer() {	
	}
	
	@Override
	public void initialize() 
		throws StreamDataException {
		try {
			config = StreamServiceConfig.getInstance();
			consumer = new KafkaConsumer<>(config.getConsumerConfig());
			consumer.subscribe(Collections.singletonList(config.get(StreamServiceConfig.STREAM_TOPIC)));
		} catch (StreamServiceException ssex) {
			throw new StreamDataException(ssex);
		}
	}
	
	public List<String> read() 
		throws StreamDataException {
		List<String> messages = new ArrayList<>();
		try {
			ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));	
			
			consumerRecords.forEach(record -> {
				messages.add(record.value());
			});									
			consumer.commitSync();
			return messages;
		} catch (KafkaException ex) {
			throw new StreamDataException(ex);
		}
	}
	
	@Override
	public void close() {
		try {
			if ( consumer != null) {				
				consumer.close();
				consumer.unsubscribe();
			}
		} catch (KafkaException ex) {			
		}
	}
	
}
