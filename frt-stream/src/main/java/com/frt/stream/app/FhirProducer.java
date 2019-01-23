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
package com.frt.stream.app;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

/*
 * FhirProducer class
 * @author chaye
 */
public class FhirProducer implements ParticipatingApplication {

	private Producer<String, String> producer;
	private StreamServiceConfig config;
	
	public FhirProducer() {
	}

	@Override
	public void initialize() 
		throws StreamDataException {
		try {
			config = StreamServiceConfig.getInstance();
			producer = new KafkaProducer<String, String>(config.getProducerConfig());
			producer.initTransactions();			
		} catch (StreamServiceException ssex) {
			throw new StreamDataException(ssex);
		}
	}

	public void write(String key, String message) 
		throws StreamDataException {
		try {
			producer.beginTransaction();
			producer.send(new ProducerRecord<String, String>(config.get(StreamServiceConfig.STREAM_TOPIC), key, message));
			producer.commitTransaction();
		} catch (KafkaException ex) {
			try {
				producer.abortTransaction();
			} catch (KafkaException ignore) {				
			}
		}		
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
	
}
