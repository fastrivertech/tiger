# Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
-------------------------------------------------------------------------
DataFlow: managingOrganization.csv
				     -> FHIR ORG File Source Connector
					 -> FhirOrgTopic 					  
	  			     -> FHIR_ORG_STREAM stream with organization schema
					 -> FHIR_ORG_SINK table
					 -> FHIR MySQL Sink Connector
					 -> MySQL FHIR_ORG_SINK table
					 -> Grafana dashboard
--------------------------------------------------------------------------

Start up Services	
-----------------
1)start confluent
  - /home/ec2-user/confluent-5.2.0/bin/confluent start  
2) stop connect
  - /home/ec2-user/confluent-5.2.0/bin/confluent stop connect
3)launch Confluent Center
  - http://ec2-54-202-179-213.us-west-2.compute.amazonaws.com:9021     
4)create FhirOrgTopic
  - /home/ec2-user/confluent-5.2.0/bin/create-fhir-org-topics.sh
5)create fhir org streams
  - ksql>RUN SCRIPT './create-fhir-org-streams.sql';
6)modify connection.url of mysql-sink connector
  - /home/ec2-user/confluent-5.2.0/etc/kafka-connect-jdbc/fhir-org-mysql-sink.properties 
7)start connect
  - /home/ec2-user/confluent-5.2.0/bin/start-fhir-org-mysql-sink.sh 
8)feed organization records
  - /home/ec2-user/fhir-stream/data/managingOrganization.csv 
9)launch Grafana
  - http://ec2-54-202-179-213.us-west-2.compute.amazonaws.com:3000 
10)create the FHIR dashboard

Clean up
--------  