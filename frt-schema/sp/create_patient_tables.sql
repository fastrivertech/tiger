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
CREATE TABLE SYSTEM_RESOURCE (
	system_id	BIGINT NOT NULL,
	version_id	BIGINT NOT NULL,
	status		VARCHAR(16) NOT NULL,
	createDate	TIMESTAMP,
	createUser	VARCHAR(32),
	updateDate	TIMESTAMP,
	updateUser	VARCHAR(32),
	PRIMARY KEY (system_id)
);

-- FHIR base resource table --- 
CREATE TABLE RESOURCE (
	resource_id	BIGINT NOT NULL,
	system_id	BIGINT NOT NULL,	
	id	VARCHAR(32), -- Σ   
	meta CLOB, -- Σ, Meta object serialization and de-serialization
	implicitRules VARCHAR(2048), -- ?!Σ, maximum uri length 
	language VARCHAR(32), -- Σ, maximum code length
	PRIMARY KEY (resource_id), 	
	FOREIGN KEY (system_id) REFERENCES SYSTEM(system_id)
);

-- FHIR domain resource table, I and affected by constraints --- 
CREATE TABLE DOMAIN_RESOURCE (
	domain_resource_id	BIGINT NOT NULL,
	resource_id BIGINT NOT NULL,
	txt CLOB, -- I, Narrative object serialization and de-serialization
	contained CLOB, -- A list of resource_id of resource table object serialization and de-serialization 	
    -- extension -- A list of extension_id of extension table object serialization and de-serialization
	-- modifierExtension -- ?!, A list of extension_id object of extension table serialization and de-serialization
	PRIMARY KEY (domain_resource_id), 	
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)
);

-- FHIR patient resource relevant tables --
CREATE TABLE PATIENT (
	patient_id	BIGINT NOT NULL,
	domain_resource_id	BIGINT,
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
	PRIMARY KEY (patient_id), 	
	FOREIGN KEY (domain_resource_id) REFERENCES DOMAIN_RESOURCE(domain_resource_id)
);

CREATE SEQUENCE PATIENT_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;

-- FHIR patient resource extension table, I and affected by constraints --
CREATE TABLE PATIENT_EXTENSION (
	extension_id BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	secondary_id BIGINT NOT NULL,	
	path VARCHAR(128), -- path patterns: patient.domain.resource/paitent.[child]/extension, for exampe, patient, patient.identifier, etc.
	url	VARCHAR(128) NOT NULL, 	
	value CLOB, -- value of any FHIR primitive and complex data types
	isModifier BOOLEAN DEFAULT false, -- default false
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR patient resource element extension table, I and affected by constraints --
CREATE TABLE PATIENT_ELEMENT_EXTENSION (
	extension_id BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	secondary_id BIGINT NOT NULL,	
	path VARCHAR(128), -- path patterns:paitent.[attribute]/patient.[child].[attribute]/extesion, for exampe, patient.active, patient.identifier.id, etc.
	url	VARCHAR(128) NOT NULL, 	
	value BLOB, -- value of any FHIR primitive and complex data types
	isModifier boolean DEFAULT false, -- default false
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR Identifier complex type, Σ --
CREATE TABLE PATIENT_IDENTIFIER (
	identifier_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.identifier	
	use	VARCHAR(32), -- ?!Σ
	type CLOB, -- Σ, CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR(128), -- Σ
	value VARCHAR(128), -- Σ
	period	CLOB,  -- Σ, Period object serialization and de-serialization 	  	 
	assigner CLOB, -- Σ, Reference object serialization and de-serialization
	PRIMARY KEY (identifier_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR HumanName complex type, Σ --
CREATE TABLE PATIENT_HUMANNAME (
	humanname_id BIGINT NOT NULL, -- as well as is element_id
	patient_id	BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.name	
	use VARCHAR(32), -- ?!Σ
	txt VARCHAR(2048), -- Σ
	family VARCHAR(32), -- Σ
	given	CLOB, -- Σ, A list of string object serialization and de-serialization 
	prefix	CLOB, -- Σ, A list of string object serialization and de-serialization
	suffix	CLOB, -- Σ, A list of string object serialization and de-serialization
	period	CLOB, -- Σ, Extension object serialization and de-serialization
	PRIMARY KEY (humanname_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

CREATE SEQUENCE PATIENT_HUMANNAME_SEQ AS BIGINT START WITH 1 INCREMENT by 1 NO CYCLE;

-- FHIR ContactPoint complex type, Σ --
CREATE TABLE PATIENT_CONTACTPOINT (
	contactpoint_id	BIGINT NOT NULL, -- as well as is element_id
	patient_id	BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.telecom
	system VARCHAR(128), -- Σ
	value VARCHAR(2048), -- Σ
	use VARCHAR(32), -- ?!Σ
	rank INTEGER, -- Σ
	period CLOB, -- Σ, Period object serialization and de-serialization 
	PRIMARY KEY (contactpoint_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR Address complex type, Σ --
CREATE TABLE PATIENT_ADDRESS (
	address_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
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
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)		
);

-- FHIR CodeableConcept complex type --
CREATE TABLE PATIENT_CODEABLECONCEPT (
	codeableconcept_id BIGINT NOT NULL,  -- as well as is element_id
	patient_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.maritalstatus	
	coding_system VARCHAR(128), -- Σ
	coding_version	VARCHAR(32), -- Σ
	coding_code	VARCHAR(32), -- Σ
	coding_display	VARCHAR(2048), -- Σ
	coding_userSelected	BOOLEAN DEFAULT false, -- Σ
	txt VARCHAR(2048), -- Σ
	PRIMARY KEY (codeableconcept_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)			
);

-- FHIR Attachment complex type ,Σ --
CREATE TABLE PATIENT_ATTACHMENT (
	attachment_id BIGINT NOT NULL,  -- as well as is element_id
	patient_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.photo		
	contentType VARCHAR(32), -- Σ
	language VARCHAR(32), -- Σ
	data BLOB, -- Σ, base64Binary object serialization and de-serialization
	url VARCHAR(1024), -- Σ
	size INTEGER, -- Σ
	hash BLOB, -- Σ, base64Binary object serialization and de-serialization
	title VARCHAR(32), -- Σ
	creation TIMESTAMP, -- Σ
	PRIMARY KEY (attachment_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)				
);

-- FHIR BackboneElement complex type, I and affected by constraints --
CREATE TABLE PATIENT_CONTACT ( 
	contact_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
	relationship CLOB, -- A list of CodeableConcept object serialization and de-serialization 	
	name CLOB, -- HumanName object serialization and de-serialization, can keep in PATIENT_HUMANNAME with a path of patient.contact.name
	telecom CLOB,-- A list of ContactPoint object serialization and de-serialization, can keep in PATIENT_CONTACTPOINT with a path of patient.contact.telecom 	 
	address CLOB, -- Address object serialization and de-serialization, can keep in PATIENT_ADDRESS with a path of patient.contact.address
	gender VARCHAR(32), 
	-- organization -- I, refer to PATIENT_REFERENCE table
	period CLOB, -- Period object serialization and de-serialization 
	PRIMARY KEY (contact_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)					
);

-- FHIR BackboneElement complex type, ?!Σ --
CREATE TABLE PATIENT_ANIMAL (
	animal_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
	species CLOB, -- CodeableConcept object serialization and de-serialization
	breed CLOB, -- CodeableConcept object serialization and de-serialization
	genderStatus CLOB, -- CodeableConcept object serialization and de-serialization
	PRIMARY KEY (animal_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)							
);

-- FHIR BackboneElement complex type --
CREATE TABLE PATIENT_COMMUNICATION (
	communication_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
	language CLOB NOT NULL, -- CodeableConcept object serialization and de-serialization  
	preferred BOOLEAN, 
	PRIMARY KEY (communication_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)								
);

-- FHIR Reference complex type, ΣI --
CREATE TABLE PATIENT_REFERENCE (
	reference_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for patient resource: patient.contact.organization, patient.generalpractitioner, patient.managingorganization			
    reference VARCHAR (2048), -- ΣI
	identifier CLOB, -- Σ, Identifier object serialization and de-serialization   	
	display VARCHAR(2048), -- Σ
	PRIMARY KEY (reference_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)									
);

-- FHIR BackboneElement complex type ?!Σ --
CREATE TABLE PATIENT_LINK (
	link_id BIGINT NOT NULL, -- as well as is element_id
	patient_id BIGINT NOT NULL, 
	other CLOB NOT NULL, -- Σ 
	type VARCHAR(32) NOT NULL, -- Σ
	PRIMARY KEY (link_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)										
);
