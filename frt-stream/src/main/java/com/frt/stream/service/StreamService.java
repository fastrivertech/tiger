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

import java.util.List;
import com.frt.stream.data.ParticipatingApplication;
import com.frt.stream.data.FhirConsumer;
import com.frt.stream.data.FhirProducer;
import com.frt.stream.data.StreamDataException;

/*
 * StreamService class
 * @author chaye
 */
public class StreamService {
	
	private StreamServiceConfig config;
	private ParticipatingApplication producer;
	private ParticipatingApplication consumer;	
	
	public StreamService() {			
	}
	
	public void initialize() 
		throws StreamServiceException {
		try {			
			config = StreamServiceConfig.getInstance();
			if (enabled()) {
				producer = new FhirProducer();
				producer.initialize();
				consumer = new FhirConsumer();
				consumer.initialize();
			}
		} catch(StreamDataException ex) {
			throw new StreamServiceException(ex);
		}
	}
	
	public boolean enabled() {
		String enabled = config.get(StreamServiceConfig.STREAM_ENABLE);
		return Boolean.parseBoolean(enabled);
	}

	public void write(String message) 
		throws StreamServiceException {
		try {
			((FhirProducer)producer).write(message);
		} catch (StreamDataException ex) {
			throw new StreamServiceException(ex);
		}
	}
	
	public List<String> read() 
		throws StreamServiceException {
		try {
			return ((FhirConsumer)consumer).read();
		} catch (StreamDataException ex) {
			throw new StreamServiceException(ex);
		}
	}
	
	public void close() {
		if (producer != null) {
			producer.close();
		}
		if (consumer != null) {
			consumer.close();
		}		
	}
	
}
