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
package com.frt.stream;

import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class FhirMessageProducer {

	private static int id = 0; 
	private Producer<String, String> producer;
	
	public FhirMessageProducer() {
		try {
			Properties props = new Properties();
//			props.put("acks", "all");
//			props.put("retries", 0);
//			props.put("batch.size", 16384);
//			props.put("linger.ms", 1);
//			props.put("buffer.memory", 33554432);						
			String BOOTSTRAP_SERVERS = "localhost:9092";
		    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
	        props.put(ProducerConfig.CLIENT_ID_CONFIG, "FhirMessageProducer");
	        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");  
	        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "0000");  
	 	                
			producer = new KafkaProducer<String, String>(props);
			producer.initTransactions();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void send(String message) {
		try {
			producer.beginTransaction();
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>("FhirTopic", messageId + " " + message));
			producer.commitTransaction();
			System.out.println("    " + messageId + " sent successfully");
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
