/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Date:                             
 * $Revision:                         
 * $Author:                                         
 * $Id: 
 */ 
 -- system tables --
CREATE TABLE SYSTEM (
	system_id	BIGINT NOT NULL,
	version_id	BIGINT NOT NULL,
	status		VARCHAR NOT NULL,
	createDate	TIMESTAMP,
	createUser	VARCHAR(20),
	updateDate	TIMESTAMP,
	updateUser	VARCHAR(20),
	PRIMARY KEY (system_id)
);

CREATE TABLE RESOURCE (
	resource_id	BIGINT NOT NULL,
	resource_id_extension BLOB, -- Extension object serialization and de-serialization		
	system_id	BIGINT NOT NULL,	
	resource_meta BLOB, -- Meta object serialization and de-serialization
	resource_implicitRules VARCHAR, 
	resource_language VARCHAR,
	domainResource_text	BLOB, -- Narrative object serialization and de-serialization
	domainResource_contained BLOB, -- A list of resource id object serialization and de-serialization 	
    domainResource_extension	BLOB, -- a list of Extension object serialization and de-serialization
	domainResource_modifierExtension	BLOB, -- a list of Extension object serialization and de-serialization
	-- Element Extension
	resource_implicitRules_extension BLOB, -- Extension object serialization and de-serialization	
	resource_language_extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (resource_id), 	
	FOREIGN KEY (system_id) REFERENCES SYSTEM(system_id)
)

-- FHIR patient resource relevant tables --
CREATE TABLE PATIENT (
	patient_id	BIGINT NOT NULL,
	resource_id	BIGINT NOT NULL,
	active	BOOLEAN NOT NULL,
	gender VARCHAR(10),
	birthDate DATE,
	deceasedBoolean BOOLEAN,
	deceasedDateTime DATATIME,
	multipleBirthBoolean BOOLEAN,
	multipleBirthInteger INTEGER,
	-- Element Extension
	active_extension BLOB, -- Extension object serialization and de-serialization	
	gender_extension BLOB, -- Extension object serialization and de-serialization	
	birthDate_extension BLOB, -- Extension object serialization and de-serialization	
	deceasedBoolean_extension BLOB, -- Extension object serialization and de-serialization	
	deceasedDateTime_extension BLOB, -- Extension object serialization and de-serialization	
	multipleBirthBoolean_extension BLOB, -- Extension object serialization and de-serialization	
	multipleBirthInteger_extension BLOB, -- Extension object serialization and de-serialization		
	PRIMARY KEY (patient_id), 	
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)	
);

CREATE TABLE PATIENT_IDENTIFIER (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	use	VARCHAR, 
	type	CLOB, -- CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR,
	value VARCHAR, 
	period	BLOB,  -- Period object serialization and de-serialization 	  	 
	assigner BLOB, -- Reference object serialization and de-serialization
	-- Element Extension
	use_extension BLOB, -- Extension object serialization and de-serialization	
	system_extension BLOB, -- Extension object serialization and de-serialization	
	value_extension BLOB, -- Extension object serialization and de-serialization			
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

CREATE TABLE PATIENT_NAME (
	element_id	BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	use VARCHAR,
	text VARCHAR,
	family VARCHAR,
	given	CLOB, -- A list of string object serialization and de-serialization 
	prefix	CLOB, -- A list of string object serialization and de-serialization
	suffix	CLOB, -- A list of string object serialization and de-serialization
	period	BLOB, -- Extension object serialization and de-serialization
	-- Element Extension
	use_extension BLOB, -- Extension object serialization and de-serialization	
	text_extension BLOB, -- Extension object serialization and de-serialization	
	family_extension BLOB, -- Extension object serialization and de-serialization		
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

CREATE TABLE PATIENT_TELECOM (
	element_id	BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	system VARCHAR,
	value VARCHAR,
	use VARCHAR,
	rank INTEGER,
	period BLOB, -- Period object serialization and de-serialization 
	-- Element Extension	
	element_extension BLOB, -- Extension object serialization and de-serialization
	system_extension BLOB, -- Extension object serialization and de-serialization
	value_extension BLOB, -- Extension object serialization and de-serialization
	use_extension BLOB, -- Extension object serialization and de-serialization
	rank_extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

CREATE TABLE PATIENT_ADDRESS (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	use VARCHAR(20),
	type VARCHAR(20),
	text VARCHAR(20),
	line VARCHAR(200),
	city VARCHAR(20),
	district VARCHAR(20),
	state VARCHAR(20),
	postalCode VARCHAR(20),
	country VARCHAR(20),		
	period BLOB, -- Period object serialization and de-serialization
	-- Element Extension 
	use_extension BLOB, -- Extension object serialization and de-serialization
	type_extension BLOB, -- Extension object serialization and de-serialization
	text_extension BLOB, -- Extension object serialization and de-serialization
	line_extension BLOB, -- Extension object serialization and de-serialization
	city_extension BLOB, -- Extension object serialization and de-serialization
	district_extension BLOB, -- Extension object serialization and de-serialization
	state_extension BLOB, -- Extension object serialization and de-serialization
	postalCode_extension BLOB, -- Extension object serialization and de-serialization
	country_extension BLOB, -- Extension object serialization and de-serialization	
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)		
);

CREATE TABLE PATIENT_MARITALSTATUS (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	coding_system	VARCHAR,
	coding_version	VARCHAR,
	coding_code	VARCHAR,
	coding_display	VARCHAR,
	coding_userSelected	BOOLEAN,
	text VARCHAR,
	-- Element Extension
	coding_system_extension BLOB, -- Extension object serialization and de-serialization
	coding_version_extension BLOB, -- Extension object serialization and de-serialization
	coding_code_extension BLOB, -- Extension object serialization and de-serialization
	coding_display_extension BLOB, -- Extension object serialization and de-serialization
	coding_userSelected_extension BLOB, -- Extension object serialization and de-serialization
	text_extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)			
);

CREATE TABLE PATIENT_PHOTO (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	contentType VARCHAR,
	language VARCHAR,
	data BLOB,
	url VARCHAR,
	size INTEGER,
	hash BLOB,
	title VARCHAR,
	creation DATATIME,
	-- Element Extension
	contentType_extension BLOB, -- Extension object serialization and de-serialization
	language_extension BLOB, -- Extension object serialization and de-serialization
	data_extension BLOB, -- Extension object serialization and de-serialization
	url_extension BLOB, -- Extension object serialization and de-serialization
	size_extension BLOB, -- Extension object serialization and de-serialization
	hash_extension BLOB, -- Extension object serialization and de-serialization
	title_extension BLOB, -- Extension object serialization and de-serialization
	creation_extension BLOB, -- Extension object serialization and de-serialization	
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)				
);

CREATE TABLE PATIENT_CONTACT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	relationship BLOB, -- A list of CodeableConcept object serialization and de-serialization 	
	name BLOB, -- HumanName object serialization and de-serialization
	telecom,-- A list of Contact object serialization and de-serialization 	 
	address BLOB, -- Address object serialization and de-serialization
	gender VARCHAR 
	organization BLOB, -- Reference object serialization and de-serialization 
	period BLOB, -- Period object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)					
);

CREATE TABLE PATIENT_ANIMAL (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	species BLOB, -- CodeableConcept object serialization and de-serialization
	breed BLOB, -- CodeableConcept object serialization and de-serialization
	genderStatus BLOB, -- CodeableConcept object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)							
);

CREATE TABLE PATIENT_COMMUNICATION (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization 
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	language BLOB -- CodeableConcept object serialization and de-serialization  
	preferred BOOLEAN, 
	-- Element Extension
	preferred_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)								
);

CREATE TABLE PATIENT_GENERALPRACTITIONER (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)									
);

CREATE TABLE PATIENT_MANAGINGORGANIZATION (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)									
);

CREATE TABLE PATIENT_LINK (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension  BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization
	other_reference VARCHAR,
	other_identifier -- Identifer object serialization and de-serialization 
	other_display VARCHAR,
	type VARCHAR,
	-- Element Extension
	other_extension BLOB, -- Extension object serialization and de-serialization
	other_extension BLOB, -- Extension object serialization and de-serialization
	type_extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)										
);
