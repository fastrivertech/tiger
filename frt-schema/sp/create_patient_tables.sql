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

-- sequence table -- 
CREATE TABLE SEQUENCE (
	SEQ_NAME VARCHAR(50) NOT NULL, 
	SEQ_COUNT INTEGER, 
	PRIMARY KEY (SEQ_NAME)
);

 -- system tables --
CREATE TABLE SYSTEM_RESOURCE (
	system_id	VARCHAR(64) NOT NULL,
	version_id	VARCHAR(64) NOT NULL,
	status		VARCHAR(16) NOT NULL,
	createDate	TIMESTAMP,
	createUser	VARCHAR(32),
	updateDate	TIMESTAMP,
	updateUser	VARCHAR(32),
	PRIMARY KEY (system_id)
);

-- FHIR base resource table --- 
CREATE TABLE RESOURCE (
	resource_id BIGINT NOT NULL, -- implementation specific primary key
	id	VARCHAR(64) NOT NULL, -- logical ID of the resource per FHIR, visible to end user
	system_id	VARCHAR(64), --NOT NULL, relax now since SYSTEM_RESOURCE is not implemented yet
	meta CLOB, -- Σ, Meta object serialization and de-serialization
	implicitRules VARCHAR(2048), -- ?!Σ, maximum uri length 
	language VARCHAR(32), -- Σ, maximum code length
	resource_type VARCHAR(32), -- discriminator column
	PRIMARY KEY (resource_id)
);

-- FHIR domain resource table, I and affected by constraints --- 
CREATE TABLE DOMAIN_RESOURCE (
	domain_resource_id BIGINT, -- unique key for domain resource leave it here now for future use
	resource_id BIGINT NOT NULL, -- implementation specific primary key
	txt CLOB, -- I, Narrative object serialization and de-serialization
	contained CLOB, -- A list of resource_id of resource table object serialization and de-serialization 	
    -- extension -- A list of extension_id of extension table object serialization and de-serialization
	-- modifierExtension -- ?!, A list of extension_id object of extension table serialization and de-serialization
	domain_resource_type VARCHAR(32),
	PRIMARY KEY (resource_id),
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)	
);

-- FHIR patient resource relevant tables --
CREATE TABLE PATIENT (
    patient_id	BIGINT, -- unique key for patient resource leave it here now for future use
	resource_id BIGINT NOT NULL, -- implementation primary key
	-- identifier -- Σ, refer to PATIENT_IDENTIFIER table	
	active	BOOLEAN NOT NULL, -- ?!Σ
	-- name -- Σ, refer to PATIENT_HUMANNAME table
	-- telecom -- Σ, refer to PATIENT_CONTACT table
	gender VARCHAR(32), -- Σ
	birthDate DATE, -- Σ
	deceasedBoolean BOOLEAN, -- ?!Σ
	deceasedDateTime TIMESTAMP, -- ?!Σ
	-- address -- Σ, refer to PATIENT_ADDRESS table
	-- maritalstatus -- refer to PATIENT_CODEABLECONCEPT table
	multipleBirthBoolean BOOLEAN, 
	multipleBirthInteger INTEGER,
	-- photo -- refer to PATIENT_ATTACHMENT table
	-- contact -- I, refer to PATIENT_CONTACT table
	-- anumal -- ?!Σ, refer to PATIENT_ANIMAL table
	-- communitcation -- refer to PATIENT_COMMUNICATION table
	-- generalPractitioner -- refer to PATIENT_REFERENCE table
	-- managingOrganization -- Σ, refer to PATIENT_REFERENCE table
	-- link -- ?!Σ, refer to PATIENT_LINK table
	PRIMARY KEY (resource_id),
	FOREIGN KEY (resource_id) REFERENCES DOMAIN_RESOURCE(resource_id)	
);

CREATE SEQUENCE PATIENT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_SEQ', 1);
					   
-- FHIR patient resource extension table, I and affected by constraints --
CREATE TABLE PATIENT_EXTENSION (
	patient_extension_id BIGINT NOT NULL, -- implementation specific primary key
	resource_id	BIGINT NOT NULL, -- FK to PATIENT table
	path VARCHAR(128), -- path patterns: patient.domain.resource/paitent.[child]/extension, for exampe, patient, patient.identifier, etc.
	url	VARCHAR(128) NOT NULL, 	
	value CLOB, -- value of any FHIR primitive and complex data types
	isModifier BOOLEAN DEFAULT false, -- default false
	PRIMARY KEY (patient_extension_id), 
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)	
);

CREATE SEQUENCE PATIENT_EXTENSION_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_EXTENSION_SEQ', 1);

-- FHIR Identifier complex type, Σ --
CREATE TABLE PATIENT_IDENTIFIER (
	identifier_id BIGINT NOT NULL, -- as well as is element_id
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.identifier	
	use	VARCHAR(32), -- ?!Σ
	type CLOB, -- Σ, CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR(128), -- Σ
	value VARCHAR(128), -- Σ
	period	CLOB,  -- Σ, Period object serialization and de-serialization 	  	 
	assigner CLOB, -- Σ, Reference object serialization and de-serialization
	PRIMARY KEY (identifier_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)	
);

CREATE SEQUENCE PATIENT_IDENTIFIER_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_IDENTIFIER_SEQ', 1);

-- FHIR HumanName complex type, Σ --
CREATE TABLE PATIENT_HUMANNAME (
	humanname_id BIGINT NOT NULL, -- implemntation specific primary key, as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.name	
	use VARCHAR(32), -- ?!Σ
	txt VARCHAR(2048), -- Σ
	family VARCHAR(32), -- Σ
	given	VARCHAAR(1024), -- Σ, A list of string object serialization and de-serialization 
	prefix	VARCHAAR(1024), -- Σ, A list of string object serialization and de-serialization
	suffix	VARCHAAR(1024), -- Σ, A list of string object serialization and de-serialization
	period	CLOB, -- Σ, Extension object serialization and de-serialization
	PRIMARY KEY (humanname_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)	
);

CREATE SEQUENCE PATIENT_HUMANNAME_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_HUMANNAME_SEQ', 1);

-- FHIR ContactPoint complex type, Σ --
CREATE TABLE PATIENT_CONTACTPOINT (
	contactpoint_id	BIGINT NOT NULL, -- implemntation specific primary key, as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.telecom
	system VARCHAR(128), -- Σ
	value VARCHAR(2048), -- Σ
	use VARCHAR(32), -- ?!Σ
	rank INTEGER, -- Σ
	period CLOB, -- Σ, Period object serialization and de-serialization 
	PRIMARY KEY (contactpoint_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)	
);

CREATE SEQUENCE PATIENT_CONTACTPOINT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_CONTACTPOINT_SEQ', 1);

-- FHIR Address complex type, Σ --
CREATE TABLE PATIENT_ADDRESS (
	address_id BIGINT NOT NULL, -- implementation primary key, as well as is element_id
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.address
	use VARCHAR(32), -- ?!Σ
	type VARCHAR(32), -- Σ
	txt VARCHAR(2048),  -- Σ
	line VARCHAR(2048), -- Keep a new line character for suporting multiple lines
	city VARCHAR(32),  -- Σ
	district VARCHAR(32),  -- Σ
	state VARCHAR(32),  -- Σ
	postalCode VARCHAR(32),  -- Σ
	country VARCHAR(32),  -- Σ		
	period CLOB, -- Σ, Period object serialization and de-serialization
	PRIMARY KEY (address_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)		
);

CREATE SEQUENCE PATIENT_ADDRESS_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_ADDRESS_SEQ', 1);

-- FHIR CodeableConcept complex type --
CREATE TABLE PATIENT_CODEABLECONCEPT (
	codeableconcept_id BIGINT NOT NULL,  -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.maritalstatus	
	coding CLOB, -- Σ, collection of Coding object serialization and de-serialization
	txt VARCHAR(2048), -- Σ
	PRIMARY KEY (codeableconcept_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)			
);

CREATE SEQUENCE PATIENT_CODEABLECONCEPT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_CODEABLECONCEPT_SEQ', 1);

-- FHIR Attachment complex type ,Σ --
CREATE TABLE PATIENT_ATTACHMENT (
	attachment_id BIGINT NOT NULL,  -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.photo		
	contentType VARCHAR(32), -- Σ
	language VARCHAR(32), -- Σ
	data CLOB, -- Σ, base64Binary object serialization and de-serialization
	url VARCHAR(1024), -- Σ
	size INTEGER, -- Σ
	hash CLOB, -- Σ, base64Binary object serialization and de-serialization
	title VARCHAR(32), -- Σ
	creation TIMESTAMP, -- Σ
	PRIMARY KEY (attachment_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)				
);

CREATE SEQUENCE PATIENT_ATTACHMENT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_ATTACHMENT_SEQ', 1);

-- FHIR BackboneElement complex type, I and affected by constraints --
CREATE TABLE PATIENT_CONTACT ( 
	contact_id BIGINT NOT NULL, -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	relationship CLOB, -- A list of CodeableConcept object serialization and de-serialization 	
	name CLOB, -- HumanName object serialization and de-serialization, can keep in PATIENT_HUMANNAME with a path of patient.contact.name
	telecom CLOB,-- A list of ContactPoint object serialization and de-serialization, can keep in PATIENT_CONTACTPOINT with a path of patient.contact.telecom 	 
	address CLOB, -- Address object serialization and de-serialization, can keep in PATIENT_ADDRESS with a path of patient.contact.address
	gender VARCHAR(32), 
	-- organization -- I, refer to PATIENT_REFERENCE table
	organization CLOB,
	period CLOB, -- Period object serialization and de-serialization 
	PRIMARY KEY (contact_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)					
);

CREATE SEQUENCE PATIENT_CONTACT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_CONTACT_SEQ', 1);

-- FHIR BackboneElement complex type, ?!Σ --
CREATE TABLE PATIENT_ANIMAL (
	animal_id BIGINT NOT NULL, -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	species CLOB, -- CodeableConcept object serialization and de-serialization
	breed CLOB, -- CodeableConcept object serialization and de-serialization
	genderStatus CLOB, -- CodeableConcept object serialization and de-serialization
	PRIMARY KEY (animal_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)							
);

CREATE SEQUENCE PATIENT_ANIMAL_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_ANIMAL_SEQ', 1);

-- FHIR BackboneElement complex type --
CREATE TABLE PATIENT_COMMUNICATION (
	communication_id BIGINT NOT NULL, -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	language CLOB NOT NULL, -- CodeableConcept object serialization and de-serialization  
	preferred BOOLEAN, 
	PRIMARY KEY (communication_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)								
);

CREATE SEQUENCE PATIENT_COMMUNICATION_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_COMMUNICATION_SEQ', 1);

-- FHIR Reference complex type, ΣI --
CREATE TABLE PATIENT_REFERENCE (
	reference_id BIGINT NOT NULL, -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.contact.organization, patient.generalpractitioner, patient.managingorganization			
    reference VARCHAR (2048), -- ΣI
	identifier CLOB, -- Σ, Identifier object serialization and de-serialization   	
	display VARCHAR(2048), -- Σ
	PRIMARY KEY (reference_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)									
);

CREATE SEQUENCE PATIENT_REFERENCE_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_REFERENCE_SEQ', 1);

-- FHIR BackboneElement complex type ?!Σ --
CREATE TABLE PATIENT_LINK (
	link_id BIGINT NOT NULL, -- implementation primary key (not part of FHIR spec schema), as well as is element_id?
	resource_id BIGINT NOT NULL, -- FK to PATIENT TABLE
	other CLOB NOT NULL, -- Σ 
	type VARCHAR(32) NOT NULL, -- Σ
	PRIMARY KEY (link_id), 	
	FOREIGN KEY (resource_id) REFERENCES PATIENT(resource_id)										
);

CREATE SEQUENCE PATIENT_LINK_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;
INSERT INTO SEQUENCE (SEQ_NAME, SEQ_COUNT) VALUES ('PATIENT_LINK_SEQ', 1);
