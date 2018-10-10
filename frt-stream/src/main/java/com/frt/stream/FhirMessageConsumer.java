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

	public static void main(String[] args) {

		String TOPIC = "FhirTopic";
		String BOOTSTRAP_SERVERS = "localhost:9092";
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "FhirMessageConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		final Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(TOPIC));

		int count = 0;
		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

			if (consumerRecords.count() == 0) {
				count++;
				if (count > 10)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {
				System.out.printf("Consumer Record:(%s, %s, %d, %d)\n", record.key(), record.value(),
						record.partition(), record.offset());
			});

			consumer.commitAsync();
		}
		consumer.close();
	}

}
