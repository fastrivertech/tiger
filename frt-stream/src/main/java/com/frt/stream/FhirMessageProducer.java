/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Date:                             
 * $Revision:                         
 * $Author:                                         
 * $Id: 
 */
package com.frt.stream;

import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class FhirMessageProducer {

	private static int id = 0; 
	private Producer<String, String> producer;
	
	public FhirMessageProducer() {
		try {
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("acks", "all");
			props.put("retries", 0);
			props.put("batch.size", 16384);
			props.put("linger.ms", 1);
			props.put("buffer.memory", 33554432);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			producer = new KafkaProducer<String, String>(props);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void send(String message) {
		try {
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>("FhirTopic", messageId, message));
			System.out.println("Message sent successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void finalize()
		throws Throwable {
		try {
			if (producer != null) {
				producer.close();
			}
		} finally {
			super.finalize();
		}
	}
	
	public static void main(String[] args) {
		new FhirMessageProducer().send("test");
	}
}
