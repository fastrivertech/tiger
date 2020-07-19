./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic FhirTopic
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --config message.timestamp.type=LogAppendTime --topic FhirDlTopic
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic FhirOrgTopic
./kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic FhirGpTopic