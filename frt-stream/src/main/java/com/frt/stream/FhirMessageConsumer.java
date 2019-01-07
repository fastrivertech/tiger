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

import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class FhirMessageConsumer {

	private Consumer<Long, String> consumer;
	
	public FhirMessageConsumer() {

		String TOPIC = "FhirTopic";
		String BOOTSTRAP_SERVERS = "localhost:9092";
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "FhirMessageConsumer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(TOPIC));
	}

	public void receives() {
		int count = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if (consumerRecords.count() == 0) {
				count++;
				if (count > 10)
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
		consumer.close();		
	}
	
	public void receive() {
		ConsumerRecords<Long, String> consumerRecords = consumer.poll(5000);
		consumerRecords.forEach(record -> {
			System.out.printf("    received: (%s, %s, %d, %d)\n", 
					           record.key(), record.value(), record.partition(), record.offset());
		});
		consumer.commitSync();
		consumer.close();
	}
	
	
	public static void main(String[] args) {
		new FhirMessageConsumer().receives();
	}
}
