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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

import com.frt.stream.application.ParticipatingApplication;
import com.frt.stream.application.StreamApplicationException;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;
import java.util.concurrent.*;

public class FhirStreamWriter implements ParticipatingApplication {
	
	private static int DEFAULT_INTERVAL = 1000;
	private static final Map<String, String> STATES = new HashMap<String, String>();
	static { // not used - may use for validation
		STATES.put("AL", "Alabama");
		STATES.put("AK", "Alaska");
		STATES.put("AS", "American Samoa");
		STATES.put("AZ", "Arizona");
		STATES.put("AR", "Arkansas");
		STATES.put("CA", "California");
		STATES.put("CO", "Colorado");
		STATES.put("CT", "Connecticut");
		STATES.put("DE", "Delaware");
		STATES.put("DC", "Dist. of Columbia");
		STATES.put("FL", "Florida");
		STATES.put("GA", "Georgia");
		STATES.put("GU", "Guam");
		STATES.put("HI", "Hawaii");
		STATES.put("ID", "Idaho");
		STATES.put("IL", "Illinois");
		STATES.put("IN", "Indiana");
		STATES.put("IA", "Iowa");
		STATES.put("KS", "Kansas");
		STATES.put("KY", "Kentucky");
		STATES.put("LA", "Louisiana");
		STATES.put("ME", "Maine");
		STATES.put("MD", "Maryland");
		STATES.put("MH", "Marshall Islands");
		STATES.put("MA", "Massachusetts");
		STATES.put("MI", "Michigan");
		STATES.put("FM", "Micronesia");
		STATES.put("MN", "Minnesota");
		STATES.put("MS", "Mississippi");
		STATES.put("MO", "Missouri");
		STATES.put("MT", "Montana");
		STATES.put("NE", "Nebraska");
		STATES.put("NV", "Nevada");
		STATES.put("NH", "New Hampshire");
		STATES.put("NJ", "New Jersey");
		STATES.put("NM", "New Mexico");
		STATES.put("NY", "New York");
		STATES.put("NC", "North Carolina");
		STATES.put("ND", "North Dakota");
		STATES.put("MP", "Northern Marianas");
		STATES.put("OH", "Ohio");
		STATES.put("OK", "Oklahoma");
		STATES.put("OR", "Oregon");
		STATES.put("PW", "Palau");
		STATES.put("PA", "Pennsylvania");
		STATES.put("PR", "Puerto Rico");
		STATES.put("RI", "Rhode Island");
		STATES.put("SC", "South Carolina");
		STATES.put("SD", "South Dakota");
		STATES.put("TN", "Tennessee");
		STATES.put("TX", "Texas");
		STATES.put("UT", "Utah");
		STATES.put("VT", "Vermont");
		STATES.put("VA", "Virginia");
		STATES.put("VI", "Virgin Islands");
		STATES.put("WA", "Washington");
		STATES.put("WV", "West Virginia");
		STATES.put("WI", "Wisconsin");
		STATES.put("WY", "Wyoming");
	};
	private static int id = 0;
	private Producer<String, String> producer;
	private StreamServiceConfig config;
	private String dataFolder;

	private File[] messages;
	private long interval;

	public FhirStreamWriter(String folder, File[] messages, int interval) {
		if (folder == null) {
			this.dataFolder = "./data";
		} else {
			this.dataFolder = folder;
		}
		this.messages = messages;
		this.interval = interval;
	}

	@Override
	public void initialize() 
		throws StreamApplicationException {
		try {
			config = StreamServiceConfig.getInstance();
			Properties props = config.getProducerConfig();
			producer = new KafkaProducer<String, String>(props);
			producer.initTransactions();

			System.out.println("fhir stream writer connecting to fhir stream ["
					+ config.get(StreamServiceConfig.STREAM_TOPIC) + "] on stream broker ["
					+ config.getProducerConfig().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG) + "] ...");

		} catch (StreamServiceException ssex) {
			throw new StreamApplicationException(ssex);
		}
	}

	@Override
	public void run() {
		try {
			File folder = new File(dataFolder);
			File[] files = folder.listFiles();
			Stream<File> stream = Arrays.stream(files);
			stream.forEach(file -> {
				
			 // random sleep pattern
				Random seed = new Random();
				int factor = seed.nextInt(5);
				try {
					Thread.sleep(this.interval + factor*100);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
					
				try {
					if (file.getName().endsWith(".json")) {
						String message = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
						send(message);
						rename(file.getAbsolutePath());
					}
				} catch (IOException ioex) {
				}
		 
			});
			
		} catch (Exception ex) {
			throw new StreamApplicationException(ex);
		}
	}

	public void send(String message) {
		try {
			producer.beginTransaction();
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>(config.get(StreamServiceConfig.STREAM_TOPIC), messageId, message));			
			producer.commitTransaction();
			System.out.println("    sent " + messageId + " " + message);
		} catch (Exception ex) {
		}
	}

	public void rename(String name) 
		throws StreamApplicationException {
		
		File oldName = new File(name);
		if (!oldName.exists()) {
			throw new StreamApplicationException("old file '" + name + "' does not exist");
		}
		File newName = new File(name + "~");
		if (newName.exists()) {
			newName.delete();
		}
		if (!oldName.renameTo(newName)) {
			throw new StreamApplicationException("failed to rename '" + name + "' to '" + name + "~'");
		}
		System.out.println(oldName.getName() + " renamed to " + newName.getName());
		
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

	public static void main(String[] args) {
		try {
			
			// Input parameters:
			// <BaseDir> - required directory where patient JSON files located
			//             for each state is located in sub folder <baseDir>/<state>
			// 		       the writer will spin off a thread processing JSON files at base folder level and
			// 			   each sub folder level simultaneously
			// <Interval> - interval (milli-seconds) between publish to topic
			
			if (args.length > 0 && args.length <= 2) {
				
				String baseDir = args[0];
				int interval = DEFAULT_INTERVAL;
				if (args.length == 2) {
					interval = Integer.parseInt(args[1]);
					if (interval < DEFAULT_INTERVAL)
						interval = DEFAULT_INTERVAL;
				}
				
				File base = new File(baseDir);
				int maxCnt = 0;
				if (base.exists() && base.isDirectory()) {
					
					List<FhirStreamWriter> writers = new ArrayList<FhirStreamWriter>();
					List<FutureTask<String>> tasks = new ArrayList<FutureTask<String>>();

					int msgCnt = addWriter(writers, base, interval);
					maxCnt = Math.max(maxCnt, msgCnt);

					File[] subDirs = base.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return new File(dir, name).isDirectory();
						}
					});

					for (File subDir : subDirs) {
						msgCnt = addWriter(writers, subDir, interval);
						maxCnt = Math.max(maxCnt, msgCnt);
					}

					for (FhirStreamWriter w : writers) {
						w.setProportionInterval(maxCnt);
						w.initialize();
						tasks.add(new FutureTask<>(w, "Stream task is complete: " + w.getDataFolder()));
					}

					ExecutorService executor = Executors.newFixedThreadPool(writers.size());

					try {
						for (FutureTask<String> t : tasks) {
							Future future = executor.submit(t);
						}

						for (FutureTask<String> t : tasks) {
							try {
								System.out.println(t.get());
							} catch (InterruptedException | ExecutionException e) {
							}
						}
					} finally {
						executor.shutdownNow();
					}
				}
			} else {
				printUsage();
			}
		} catch (StreamApplicationException ex) {
			ex.printStackTrace();
			System.out.println("fhir stream writer application exit(1)");
			System.exit(1);
		}
	}

	private static int addWriter(List<FhirStreamWriter> writers, File folder, int interval) {
		
		File[] files = folder.listFiles(new FilenameFilter() {
			public boolean accept(File d, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		
		if (files.length > 0) {
			writers.add(new FhirStreamWriter(folder.getPath(), files, interval));
		}
		return files.length;
	}

	private static void printUsage() {
		System.out.println("Usage: <baseDir> [<publish-interval>]");
		System.out.println("<baseDir> patient json files to be published (required)");
		System.out.println("<publish-interval> interval between publish to topic, default 1000 milli-seconds");
	}

	public long getInterval() {
		return interval;
	}

	public void setProportionInterval(long maxSize) {
		// pattern:
		// maxSize (total number of messages) / messages.length
		// more messages, wait less time
		setInterval((long) Math.ceil((getInterval() * maxSize) / messages.length));
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(String dataFolder) {
		this.dataFolder = dataFolder;
	}
}
