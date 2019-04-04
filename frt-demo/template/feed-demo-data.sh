./kafka-console-producer --broker-list localhost:9092 --topic TestTopic --property "parse.key=true" --property "key.separator=:" < %1
