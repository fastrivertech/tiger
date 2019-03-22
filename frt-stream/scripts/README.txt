Apache Kafka 

Download Apache Kafka 2.12-2.1.0 from https://kafka.apache.org/downloads
Install(unzip) to C:\apache\kafka\kafka_2.12-2.1.0

1. Run tiger\env.bat to setup the environment variables

2. Start a single node Kafka server
   tiger\frt-stream\scripts\start
   
3. Create FhirTopic/FhirDiscoveryTopic
   tiger\frt-stream\scripts\create-topic.bat FhirTopic/FhirDiscoveryTopic
   
4. List topics
   tiger\frt-stream\scripts\list-topics.bat

5. View message count of FhirTopic/FhirDiscoveryTopic
   tiger\frt-stream\scripts\read-topic.bat

6. Stop a single node Kafka server   
   tiger\frt-stream\scripts\stop
   
7. Delete FhirTopic
   Kafka 2.12-.2.1.0 has an issue of phyically deleting a topic. The workaround is 
   7.1) stop the kafka server
   7.2) manually delete C:\tmp\kafka-logs and C:\tmp\zookeeper
   7.3) restart the kafka server
   
On linux:
unzip frt-service-package-1.0.0-SNAPSHOT.zip into a folder e.g. ~/frt-services
the folder structure is as below:
/home/ec2-user/frt-services/
/home/ec2-user/
env.sh
-----------------------------+ app
                             |
                             + bin --- datalake-ingestion.bat
                                       datalake-ingestion.sh
                                       datalake-read-write.bat
                                       datalake-read-write.sh
                                       fhir-stream-reader.bat
                                       fhir-stream-reader.sh
                                       fhir-stream-writer.bat
                                       fhir-stream-writer.sh
                                       ....
-----------------------------+ stream --- create-topic.bat
                                          create-topic.sh
                                          read-topic.bat
                                          read-topic.sh
                                          list-topic.bat
                                          list-topic.sh
                                          start.bat
                                          start.sh
                                          stop.bat
										  stop.sh
                                          ... ... ...

chmod 777 to all the *.sh if necessary.
                                                                         
start single node kafka server:

* go to frt-services/stream/
* execute ./start-zookeeper.sh, then ./start.sh (kafka start will fail if zookeeper is down)

create fhir topics:
* execute ./create-topic FhirTopic or FhirDiscoveryTopic
* then read metrics of the topics : ./read-metrics.sh FhirTopic etc.

then you can go to ../bin, to write and read messages to the topics created in previous steps.

stop the kafka:

* ./stop.sh