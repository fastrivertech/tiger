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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.frt.stream.application.ApplicationThread;
import com.frt.stream.application.ParticipatingApplication;
import com.frt.stream.application.StreamApplicationException;
import com.frt.stream.service.StreamServiceConfig;
import com.frt.stream.service.StreamServiceException;

public class FhirStreamDataGen implements ParticipatingApplication {
	public static final String[] STATES = { "California", "Arizona", "Washington", "Oregon", "Utah" };
	private List<String> states;
	private List<Integer> populations;
	private List<String> stateDirs;
	private static int id = 0;
	private Producer<String, String> producer;
	private StreamServiceConfig config;
	private String dataFolder;
	private String sourceDir;
	private int limit;
	private int baseInterval;

	public FhirStreamDataGen(String propPath) throws IOException {
		Properties props = new Properties();
		InputStream input = new FileInputStream(propPath);
		props.load(input);
//		states=California,Utah,Arizona,Oregon,Washington
//		populations=1500,200,400,190,600
//		base.directory=../data/generatedData
//		source.directories=CA,UT,AZ,OR,WA
//		base.interval=100
		this.sourceDir=props.getProperty("base.directory");
		if (this.sourceDir==null||this.sourceDir.isEmpty()) {
			throw new IllegalArgumentException("sourceDir required.");
		}
		String statesStr=props.getProperty("states");
		if (statesStr==null||statesStr.isEmpty()) {
			throw new IllegalArgumentException("states required.");
		}
		this.states = Arrays.asList(statesStr.split(","));
		String popsStr=props.getProperty("populations");
		if (popsStr==null||popsStr.isEmpty()) {
			throw new IllegalArgumentException("populations for each state required.");
		}
		List<String> pops = Arrays.asList(popsStr.split(","));
		if (pops.size()!=this.states.size()) {
			throw new IllegalArgumentException("populations required for each state listed: states=" + statesStr + ", populations=" + popsStr);
		}
		this.populations = new ArrayList<Integer>();
		pops.forEach(p -> this.populations.add(Integer.parseInt(p)));
		String intervalStr=props.getProperty("base.interval", "100"); // default 100 milli-sec
		this.baseInterval=Integer.parseInt(intervalStr);
		String stateDirStr=props.getProperty("source.directories");
		if (stateDirStr==null||stateDirStr.isEmpty()) {
			this.stateDirs = Arrays.asList(statesStr.split(","));
		}
		else {
			this.stateDirs = Arrays.asList(stateDirStr.split(","));
			if (this.stateDirs.size()!=this.states.size()) {
				throw new IllegalArgumentException("data source directory required for each state listed: states=" + statesStr + ", populations=" + stateDirStr);
			}
		}

		// further validate data source for each state
		this.stateDirs.forEach(d -> { 
		   File dir = new File(this.sourceDir, d);
		   if (!(dir.exists()&&dir.isDirectory())) {
				throw new IllegalArgumentException("data source directory does not exist or is not a directory:" + dir.getPath());
		   }
		});
	}

	@Override
	public void initialize() throws StreamApplicationException {
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

	public void initialize(String folder) throws StreamApplicationException {
		if (folder == null) {
			dataFolder = "./data";
		} else {
			dataFolder = folder;
		}
		this.initialize();
	}

	@Override
	public void run() {
		File baseDir = new File(this.sourceDir);
        ExecutorService executor = Executors.newFixedThreadPool(this.states.size());
        List<Callable<Integer>> callableTasks = new ArrayList<>();
        Integer[] stateIndex = {0};
        this.states.forEach(s->{
            Callable<Integer> callableTask = () -> {
                return genData(stateIndex[0]);
            };
            stateIndex[0]++;
            callableTasks.add(callableTask);
        });

        try {
            List<Future<Integer>> futures = executor.invokeAll(callableTasks);
        	futures.forEach(f->{
	            Integer count;
				try {
					count = f.get(30*60*1000, TimeUnit.SECONDS);
		            System.out.println("generted patients count : " + count);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	});
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {

            // shut down the executor manually
            executor.shutdown();

        }
	}

	/**
	 * helper - generate messages from patient json files from source dir
	 * @param i - index of state
	 * @return count of file processed
	 */
	private Integer genData(int i) {
		Producer<String, String> producer;
		try {
			producer = new KafkaProducer<String, String>(StreamServiceConfig.getInstance().getProducerConfig());
			producer.initTransactions();

			System.out.println("fhir stream writer connecting to fhir stream ["
					+ config.get(StreamServiceConfig.STREAM_TOPIC) + "] on stream broker ["
					+ config.getProducerConfig().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG) + "] ...");

		} catch (StreamServiceException ssex) {
			throw new StreamApplicationException(ssex);
		}

		File folder = new File(this.sourceDir, this.stateDirs.get(i));
		File[] files = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		Random seed = new Random();
		List<File> fileList = Arrays.asList(files);
		Integer[] total = {0};
		fileList.forEach(file -> {
			try {
				String message = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
				send(producer, message);
				total[0]++;
				rename(file.getAbsolutePath());						
				try {
					Thread.sleep(this.baseInterval + seed.nextInt(100));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		});
		return total[0];
	}

	public void send(Producer<String, String> producer, String message) {
		try {
			producer.beginTransaction();
			String messageId = "message_" + ++id;
			producer.send(new ProducerRecord<String, String>(config.get(StreamServiceConfig.STREAM_TOPIC), messageId,
					message));
			producer.commitTransaction();
			System.out.println("    sent " + messageId + " " + message);
		} catch (Exception ex) {
		}
	}

	public void rename(String name) throws StreamApplicationException {
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

	public static void main(String[] args) throws IOException {
		try {
			if (args.length != 1) {
				System.out.println("Usage: <data-gen.properties-path>");
				System.exit(0);
			}
			FhirStreamDataGen dataGen = new FhirStreamDataGen(args[0]);
			dataGen.generate();
			System.out.println("fhir stream data-gen application exit(0)");
			System.exit(0);
		} catch (StreamApplicationException ex) {
			ex.printStackTrace();
			System.exit(1);
			System.out.println("fhir stream data-gen application exit(1)");
		}
	}

	private void generate() {
		this.initialize(this.sourceDir);
		this.run();
		this.close();
	}
}
