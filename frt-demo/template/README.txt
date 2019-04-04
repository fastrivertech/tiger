
Prerequisites
-------------
1. cp tiger\frt-demo\template to /home/ec2-user/demo (on EC2)
2. cp /home/ec2-user/demo/etc/kafka/*.* to /home/ec2-user/confluent-5.2.0/etc/kafka
3. cp /home/ec2-user/demo/etc/kafka-connect-jdbc/*.* to /home/ec2-user/confluent-5.2.0/etc/kafka-connect-jdbc
4. cp /home/ec2-user/demo/etc/schema-registry/*.* to /home/ec2-user/confluent-5.2.0/etc/schema-registry
5. cp /home/ec2-user/demo/shared/java/kafka-connect-jdbc/*.jar to /home/ec2-user/confluent-5.2.0/shared/java/kafka-connect-jdbc
6. cp /home/ec2-user/demo/*.sh /home/ec2-user/confluent-5.2.0/bin
7. cp /home/ec2-user/demo/data/*.* /home/ec2-user/confluent-5.2.0/bin

Execute
-------
1. Confluent Center
   http://ec2-54-202-179-213.us-west-2.compute.amazonaws.com:9021
2. create demo topic TestTopic 
   cd /home/ec2-user/confluent-5.2.0/bin
   ./create-demo-topics.sh
3. create demo streams converting from json stream to avro stream
   ./ksql
   ksql>RUN SCRIPT '/home/ec2-user/demo/create-demo-streams.sql';

DataFlow: TestTopic -> demo-mysql-avro-sink -> MySQL
4. start demo-mysql-avro-sink connector
   cd /home/ec2-user/confluent-5.2.0/bin
   ./start-demo-mysql-avro-sink.sh    
5. feed data
   cd /home/ec2-user/confluent-5.2.0/bin
   ./feed-demo-data.sh data.txt

DataFlow: TestTopic -> demo-mysql-json-sink -> MySQL
6. start demo-mysql-json-sink connector
   cd /home/ec2-user/confluent-5.2.0/bin
   ./start-demo-mysql-json-sink.sh    
7. feed data
   cd /home/ec2-user/confluent-5.2.0/bin
   ./feed-demo-data.sh data-w-schema.txt

DataFlow: demo-file-source -> TestTopic -> demo-file-sink
8. start demo-file-source-sink connector
   cd /home/ec2-user/confluent-5.2.0/bin
   ./start-demo-file-source-sink.sh    
9. feed data
   /home/ec2-user/data/data.txt
   /home/ec2-user/data/output.txt
      
Clean up
--------
1. stop any running queries from Confluent Center/KSQL/RUNNING QUERIES
2. drop demo streams
   ./ksql
   ksql>RUN SCRIPT '/home/ec2-user/demo/drop-demo-streams.sql';
3. drop demo topics
   cd /home/ec2-user/confluent-5.2.0/bin
   ./drop-demo-topics.sh
4. stop any connect
   cd /home/ec2-user/confluent-5.2.0/bin
   ./confluent stop connect
5. drop MySQL table TESTTOPIC_AS
   