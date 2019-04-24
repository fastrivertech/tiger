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

CREATE TABLE FHIR_GROUPBY_ORG_GENDER WITH (VALUE_FORMAT='AVRO') AS 
							  SELECT MAX(ROWTIME) AS TS,
							         MANAGINGORGANIZATION->ID AS ORG_ID, 
								     GENDER AS PATIENT_GENDER, 
								   	 count(*) AS CNT 
							  FROM FHIR_AVRO_STREAM 
							  GROUP BY MANAGINGORGANIZATION->ID,GENDER;									

CREATE TABLE FHIR_GROUPBY_GP_GENDER WITH (VALUE_FORMAT='AVRO') AS 
							 SELECT MAX(ROWTIME) AS TS,
									GENERALPRACTITIONER[0]->ID AS NPI, 
								    GENDER AS PATIENT_GENDER, 
								   	count(*) AS CNT 
							 FROM FHIR_AVRO_STREAM 
							 GROUP BY GENERALPRACTITIONER[0]->ID,GENDER;								   
								   
CREATE TABLE FHIR_GROUPBY_STATE_GENDER WITH (VALUE_FORMAT='AVRO') AS
								SELECT MAX(ROWTIME) AS TS,
								       GENDER AS PATIENT_GENDER,
								       ADDRESS[0]->STATE AS ADDR_STATE,
								       COUNT(*) AS CNT
							    FROM FHIR_AVRO_STREAM
								GROUP BY ADDRESS[0]->STATE,GENDER;

CREATE STREAM FHIR_DL_STREAM (patient_gender VARCHAR,
							  age_group VARCHAR,
							  patient_count VARCHAR)								
							  WITH (KAFKA_TOPIC='FhirDlTopic',VALUE_FORMAT='JSON');								   								  
CREATE STREAM FHIR_GROUPBY_AGE WITH (VALUE_FORMAT='AVRO') AS 
			 				   SELECT ROWTIME AS TS,
									  PATIENT_GENDER,
									  AGE_GROUP,
									  PATIENT_COUNT
							   FROM FHIR_DL_STREAM;
																
CREATE STREAM FHIR_ORG_STREAM (id VARCHAR, 
							   name VARCHAR, 
							   telecom VARCHAR,
							   addressline VARCHAR,
							   city VARCHAR,
							   state VARCHAR,
							   postalcode VARCHAR)								
							   WITH (KAFKA_TOPIC='FhirOrgTopic',VALUE_FORMAT='DELIMITED');

CREATE STREAM FHIR_ORG WITH (VALUE_FORMAT='AVRO') AS 
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

CREATE STREAM FHIR_GP WITH (VALUE_FORMAT='AVRO') AS 
						   SELECT * FROM FHIR_GP_STREAM;
							