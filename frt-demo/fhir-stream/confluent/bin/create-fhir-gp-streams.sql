CREATE STREAM FHIR_GP_STREAM (id VARCHAR, 
							  name VARCHAR, 
							  specialty VARCHAR, 
							  telecom VARCHAR,
							  addressline VARCHAR,
							  city VARCHAR,
							  state VARCHAR,
							  postalcode VARCHAR)								
	WITH (KAFKA_TOPIC='FhirGpTopic',VALUE_FORMAT='DELIMITED');
CREATE STREAM FHIR_GP_SINK 
    WITH (VALUE_FORMAT='AVRO') AS SELECT * FROM FHIR_GP_STREAM;