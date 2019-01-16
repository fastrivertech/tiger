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

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.common.KafkaException;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

/*
 * RealTimeDiscovery Implementation
 * @author chaye
 */
public class RealTimeDiscovery implements ParticipatingApplication {

	private StreamServiceConfig config;
	private KafkaStreams streams;

	private CountDownLatch latch;

	public void RealTimeDiscovery() {
	}

	public void initialize() throws StreamDataException {
		try {
			Serde<String> stringSerde = Serdes.String();
			Serde<Long> longSerde = Serdes.Long();
			
			config = StreamServiceConfig.getInstance();
			StreamsBuilder builder = new StreamsBuilder();
			KStream<String, String> source = builder.stream(config.get(StreamServiceConfig.STREAM_TOPIC),
														    Consumed.with(stringSerde, stringSerde));
			
			KTable<String, Long> counts = source.groupBy((key, value) -> value).count();
			
			counts.toStream().to(StreamServiceConfig.STREAM_DISCOVERY_TOPIC,
								 Produced.with(Serdes.String(), Serdes.Long()));
			
			Properties props = config.getApplicationConfig();
			
			props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
			props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
			
			
			streams = new KafkaStreams(builder.build(), props);
			latch = new CountDownLatch(1);

			Runtime.getRuntime().addShutdownHook(new Thread("realtime-discovery-shutdown-hook") {
				@Override
				public void run() {
					System.out.println("fhir stream discovery stop ...");
					streams.close();
					latch.countDown();
				}
			});

		} catch (StreamServiceException ssex) {
			throw new StreamDataException(ssex);
		}
	}

	public void start() throws StreamDataException {
		try {
			streams.start();
			System.out.println("fhir stream discovery ...");
			latch.await();
		} catch (KafkaException | IllegalStateException | InterruptedException ex) {
			throw new StreamDataException(ex);
		}
	}

	public void close() {
		if (streams != null) {
			streams.close();
		}
		if (latch != null) {
			latch.countDown();
		}
	}

	public static void main(String[] args) {
		try {
			RealTimeDiscovery discovery = new RealTimeDiscovery();
			discovery.initialize();
			discovery.start();
			System.exit(0);
			System.out.println("discovery application exit(0)");
		} catch (StreamDataException ex) {
			System.exit(1);
			System.out.println("discovery application exit(1)");
		}
	}
}
