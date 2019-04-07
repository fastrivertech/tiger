CREATE STREAM FHIR_ORG_STREAM (id VARCHAR, 
							   name VARCHAR, 
							   telecom VARCHAR,
							   addressline VARCHAR,
							   city VARCHAR,
							   state VARCHAR,
							   postalcode VARCHAR)								
							   WITH (KAFKA_TOPIC='FhirOrgTopic',VALUE_FORMAT='DELIMITED');
CREATE STREAM FHIR_ORG_SINK WITH (VALUE_FORMAT='AVRO') AS SELECT * FROM FHIR_ORG_STREAM;
