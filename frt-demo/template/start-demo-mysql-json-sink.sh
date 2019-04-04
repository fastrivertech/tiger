export CLASSPATH="/home/ec2-user/confluent-5.2.0/share/java/kafka-connect-jdbc/*"
./connect-standalone $HOME/confluent-5.2.0/etc/schema-registry/demo-connect-json-standalone.properties $HOME/confluent-5.2.0/etc/kafka-connect-jdbc/demo-mysql-json-sink.properties
