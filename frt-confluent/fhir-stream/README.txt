# Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
-------------------------------------------------------------------------
DataFlow: 
	patient fhir messages -> FhirTopic 
	  			 -> FHIR_JSON_STREAM stream with patient schema
				   -> FHIR_AVRO_STREAM stream
					 -> FHIR_GROUPBY_ORG table with queries 
					 -> FHIR_GROUPBY_ORG_SINK table with dashboard schema
					 -> FHIR MySQL Sink Connector
					 -> MySQL FHIR_GROUPBY_ORG_SINK table
					 -> Grafana dashboard
-------------------------------------------------------------------------
DataFlow: 
	messages -> FhirDlTopic 
	  			 -> FHIR_DL_STREAM stream
				   -> FHIR_GROUPBY_AGE stream
					 -> FHIR_GROUPBY_AGE table with dashboard schema
					 -> FHIR MySQL Sink Connector
					 -> MySQL FHIR_GROUPBY_AGE table
					 -> Grafana dashboard					 
--------------------------------------------------------------------------
DataFlow: managingOrganization.csv
				     -> FHIR ORG File Source Connector
					 -> FhirOrgTopic 					  
	  			     -> FHIR_ORG_STREAM stream with organization schema
					 -> FHIR_ORG_SINK table
					 -> FHIR MySQL Sink Connector
					 -> MySQL FHIR_ORG_SINK table
					 -> Grafana dashboard
--------------------------------------------------------------------------
DataFlow: generalPractitioner.csv
				     -> FHIR PG File Source Connector
					 -> FhirPgTopic 					  
	  			     -> FHIR_PG_STREAM stream with Practitioner schema
					 -> FHIR_PG_SINK table
					 -> FHIR MySQL Sink Connector
					 -> MySQL FHIR_PG_SINK table
					 -> Grafana dashboard
---------------------------------------------------------------------------


Prerequisites
-------------
1)Create Amazon EC2 RHEL7.6(64-bit x86)
2)Install JDK 11.0.2 on EC2
3)Install Confluent and Grafana on EC2 
  - https://github.com/fastrivertech/tiger/wiki/FHIR-Confluent-and-Grafana-Setup
  - Confluent 5.2.x / Grafana 6.1.1 
  - no user/password required for Confluent 5.2.x
  - default user/password for Grafana 6.1.1: admin/admin   
4)Install MySQL on EC2  
  - https://github.com/fastrivertech/tiger/wiki/Install-MySQL-on-EC2-(RHEL7)
  - MySQL 8.0
  - Use tiger\fhir-stream\mysql\create_db_user.sql
    > Create FHIR_DB database
	  > Create a frt user with mysql-native-password policy  
  - Use tiger\fhir-stream\mysql\create_grafana_ds_resources.sql
    > Create FHIR_GROUPBY_STATE_GENDER table with ET timestamp for historical data simulation
	  > Create insert trigger DS_TRIGGER_STATE_GENDER to map influx state + gender aggregate records to any time range by tagging the record with ET timestamp
	  > ET will be used by Grafana time series queries to render history data points on a time graph.
5)Build the Tiger project using JDK 11.0.2 and get frt-service-package ready  
  
Setup
-----
1) Copy demo scripts and confluent patches, e.g. as below:
   cp tiger\fhir-stream to /home/ec2-user/fhir-stream (on EC2)
2) Install scripts and patching confluent as below:
   cd /home/ec2-user/fhir-stream and execute 
   ./install.sh ~/confluent-5.2.0 (assume confluent is installed as shown)
	 
   or manually do following steps:
   2.1)cp /home/ec2-user/fhir-stream/confluent/etc/kafka-connect-jdbc/*.* /home/ec2-user/confluent-5.2.0/etc/kafka-connect-jdbc
   2.2)cp /home/ec2-user/fhir-stream/confluent/etc/schema-registry/*.* /home/ec2-user/confluent-5.2.0/etc/schema-registry
   2.3)cp /home/ec2-user/fhir-stream/confluent/share/java/kafka-connect-jdbc/*.jar /home/ec2-user/confluent-5.2.0/share/java/kafka-connect-jdbc
   2.4)cp /home/ec2-user/fhir-stream/confluent/bin/*.* /home/ec2-user/confluent-5.2.0/bin
	 
3) Deploy FRT service tools by doing below:
   unzip frt-service-package-1.0.0-SNAPSHOT.zip to /home/ec2-user/frt-service

Generate Patient Data
---------------------
1)run /home/ec2-user/frt-service/synthea/run_synthea
  - generate syntha patient bundles 
2)run /home/ec2-user/frt-service/load/extract.sh 
  - convert patient from the bundles  

Start up Services	
-----------------
1)start confluent
  - /home/ec2-user/confluent-5.2.0/bin/confluent start  
2) stop connect
  - /home/ec2-user/confluent-5.2.0/bin/confluent stop connect
3)launch Confluent Center
  - http://ec2-18-236-222-44.us-west-2.compute.amazonaws.com:9021     
4)create FhirTopic
  - /home/ec2-user/confluent-5.2.0/bin/create-fhir-topics.sh  
5)create fhir streams
  - ksql>RUN SCRIPT './create-fhir-streams.sql';
6)modify connection.url of mysql-sink connector
  - /home/ec2-user/confluent-5.2.0/etc/kafka-connect-jdbc/fhir-groupby-org-mysql-sink.properties 
7)load organization and practitioner records
  - /home/ec2-user/confluent-5.2.0/bin/start-fhir-file-mysql-sink.sh
  - you only need to load once
8)start connect
  - /home/ec2-user/confluent-5.2.0/bin/start-fhir-mysql-sink.sh
9)feed patient records
  - /home/ec2-user/frt-service/bin/fhir-stream-writer.sh ..\data  
  OR
  - /home/ec2-user/frt-service/bin/fhir-stream-feeder.sh ..\data_feeder 1000 California 3000 Arizona 1900 Washington 900 Oregon 500
  note, fhir-stream-feeder.sh takes command line parameters and call open source / frt tools to:
  a) generate fhir patient bundle json files per states and sample population sizes (using synthea) - results are stored in per state folders under base folder 
     (e.g. ../data_feeder/California/fhir, ../data_feeder/Arizona/fhir, ...)
  b) extract fhir patient json files from bundle json files (using frt java tool extract.sh) - the results are stored in per state folders under base folder 
     (e.g. ../data_feeder_EXT_MESSAGES/California, ../data_feeder_EXT_MESSAGES/Arizona, ...)
  c) call fhir-stream-writer given base folder (e.g. ../data_feeder_EXT_MESSAGES), each states patient jsons are read and send to kafka topic: FhirTopic
  d) in step c), each state's patient json files are read and sent by a java task (thread), the tasks are running in paralelle, generating an infux of patients across the states into the kafka streaming system.
  
10)launch Grafana
  - http://ec2-18-237-217-142.us-west-2.compute.amazonaws.com:3000 
11)create the FHIR dashboard

Clean up
--------
1)stop confluent
  - /home/ec2-user/confluent-5.2.0/bin/confluent stop
2)clean up confluent data
  - rm -rf /tmp/confluent.*
  