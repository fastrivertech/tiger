Apache Kafka 

Download Apache Kafka 2.12-2.1.0 from https://kafka.apache.org/downloads
Install(unzip) to C:\apache\kafka\kafka_2.12-2.1.0

1. Run tiger\env.bat to setup the environment variables

2. Start a single node Kafka server
   tiger\frt-stream\scripts\start
   
3. Create FhirTopic
   tiger\frt-stream\scripts\create-topic.bat
   
4. List topics
   tiger\frt-stream\scripts\list-topics.bat

5. View message count of FhirTopi
   tiger\frt-stream\scripts\view-topic.bat

6. Stop a single node Kafka server   
   tiger\frt-stream\scripts\stop
   
7. Delete FhirTopic
   Kafka 2.12-.2.1.0 has an issue of phyically deleting a topic. The workaround is 
   7.1) stop the kafka server
   7.2) manually delete C:\tmp\kafka-logs and C:\tmp\zookeeper
   7.3) restart the kafka server
   