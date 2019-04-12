CREATE STREAM FHIR_JSON_STREAM (resourceType VARCHAR, 
							    gender VARCHAR, 
								birthDate VARCHAR,
								maritalStatus STRUCT<coding ARRAY<STRUCT<system VARCHAR, code VARCHAR>>>,                                 
								name ARRAY<STRUCT<family VARCHAR, given ARRAY<VARCHAR>>>,
								address ARRAY<STRUCT<city VARCHAR, state VARCHAR>>,
								generalPractitioner ARRAY<STRUCT<id VARCHAR>>,
								managingOrganization STRUCT<id VARCHAR>)								
								WITH (KAFKA_TOPIC='FhirTopic',VALUE_FORMAT='JSON');
CREATE STREAM FHIR_AVRO_STREAM WITH (VALUE_FORMAT='AVRO') AS 
							   SELECT * FROM FHIR_JSON_STREAM;

CREATE TABLE FHIR_GROUPBY_ORG WITH (VALUE_FORMAT='AVRO') AS 
							  SELECT MANAGINGORGANIZATION->ID AS ORG_ID, 
								     GENDER AS PATIENT_GENDER, 
								   	 count(*) AS CNT 
							  FROM FHIR_AVRO_STREAM 
							  GROUP BY MANAGINGORGANIZATION->ID,GENDER;									
CREATE TABLE FHIR_GROUPBY_ORG_SINK WITH (VALUE_FORMAT='AVRO') AS 
								   SELECT TIMESTAMPTOSTRING(ROWTIME, 'yyyy-MM-dd HH:mm:ss.SSS') AS TS, 
										  ORG_ID, 
										  PATIENT_GENDER, 
										  CNT 
								   FROM FHIR_GROUPBY_ORG;

								   
CREATE TABLE FHIR_GROUPBY_GP WITH (VALUE_FORMAT='AVRO') AS 
							 SELECT MAX(ROWTIME) AS TS,
									GENERALPRACTITIONER[0]->ID AS NPI, 
								    GENDER AS PATIENT_GENDER, 
								   	count(*) AS CNT 
							 FROM FHIR_AVRO_STREAM 
							 GROUP BY GENERALPRACTITIONER[0]->ID,GENDER;								   
								   
CREATE TABLE FHIR_GROUPBY_STATE WITH (VALUE_FORMAT='AVRO') AS
								SELECT GENDER as PATIENT_GENDER,
								       ADDRESS[0]->STATE AS ADDR_STATE,
								       COUNT(*) AS CNT
							    FROM FHIR_AVRO_STREAM
                                                                WINDOW TUMBLING (SIZE 180 SECONDS) 
								GROUP BY ADDRESS[0]->STATE,GENDER;
CREATE TABLE FHIR_GROUPBY_STATE_SINK WITH (VALUE_FORMAT='AVRO') AS 
									 SELECT ROWTIME AS TS, 
										    PATIENT_GENDER,
											ADDR_STATE, 
											CNT 
									 FROM FHIR_GROUPBY_STATE;
									 
CREATE STREAM FHIR_ORG_STREAM (id VARCHAR, 
							   name VARCHAR, 
							   telecom VARCHAR,
							   addressline VARCHAR,
							   city VARCHAR,
							   state VARCHAR,
							   postalcode VARCHAR)								
							   WITH (KAFKA_TOPIC='FhirOrgTopic',VALUE_FORMAT='DELIMITED');
CREATE STREAM FHIR_ORG_SINK WITH (VALUE_FORMAT='AVRO') AS 
						    SELECT * FROM FHIR_ORG_STREAM;

CREATE STREAM FHIR_GP_STREAM (id VARCHAR, 
							  name VARCHAR, 
							  specialty VARCHAR, 
							  telecom VARCHAR,
							  addressline VARCHAR,
							  city VARCHAR,
							  state VARCHAR,
							  postalcode VARCHAR)								
							  WITH (KAFKA_TOPIC='FhirGpTopic',VALUE_FORMAT='DELIMITED');
CREATE STREAM FHIR_GP_SINK WITH (VALUE_FORMAT='AVRO') AS 
						   SELECT * FROM FHIR_GP_STREAM;
							