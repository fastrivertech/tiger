export CLASSPATH="/home/ec2-user/confluent-5.2.0/share/java/kafka-connect-jdbc/*"
./connect-standalone $HOME/confluent-5.2.0/etc/schema-registry/demo-connect-avro-standalone.properties $HOME/confluent-5.2.0/etc/kafka-connect-jdbc/demo-mysql-avro-sink.properties
