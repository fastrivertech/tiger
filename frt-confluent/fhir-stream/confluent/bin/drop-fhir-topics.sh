./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_STATE_GENDER
./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_STATE

./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_GP_GENDER
./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_GP

./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_ORG_GENDER
./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_ORG
./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_AVRO_STREAM
./kafka-topics --delete --zookeeper localhost:2181 --topic FhirTopic

./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GROUPBY_AGE
./kafka-topics --delete --zookeeper localhost:2181 --topic FhirDlTopic

./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_ORG_SINK
./kafka-topics --delete --zookeeper localhost:2181 --topic FhirOrgTopic

./kafka-topics --delete --zookeeper localhost:2181 --topic FHIR_GP_SINK
./kafka-topics --delete --zookeeper localhost:2181 --topic FhirGpTopic
