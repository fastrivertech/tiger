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
	resource_implicitRules VARCHAR(2048), -- maximum uri length 
	resource_language VARCHAR(32), -- maximu code length
	domainResource_text	BLOB, -- Narrative object serialization and de-serialization
	domainResource_contained BLOB, -- A list of resource_id object serialization and de-serialization 	
    domainResource_extension	BLOB, -- A list of Extension object serialization and de-serialization
	domainResource_modifierExtension	BLOB, -- A list of Extension object serialization and de-serialization
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
	gender VARCHAR(32),
	birthDate DATE,
	deceasedBoolean BOOLEAN,
	deceasedDateTime DATETIME,
	multipleBirthBoolean BOOLEAN,
	multipleBirthInteger INTEGER,
	PRIMARY KEY (patient_id), 	
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)
);

-- FHIR patient resource element extension table --
CREATE TABLE PATIENT_ELEMENT_EXTENSION (
	extension_id BIGINT NOT NULL,
	element_id	BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	path VARCHAR(128), -- patient.active, patient.identifier.use, etc.
	extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (element_id, patient_id) REFERENCES RESOURCE(element_id, patient_id)	
);

-- FHIR Identifier complex type --
CREATE TABLE PATIENT_IDENTIFIER (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.identifier
	use	VARCHAR(32), 
	type	CLOB, -- CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR(128),
	value VARCHAR(128), 
	period	BLOB,  -- Period object serialization and de-serialization 	  	 
	assigner BLOB, -- Reference object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR HumanName complex type --
CREATE TABLE PATIENT_HUMANNAME (
	element_id	BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.name	
	use VARCHAR(32),
	text VARCHAR(2048),
	family VARCHAR(32),
	given	CLOB, -- A list of string object serialization and de-serialization 
	prefix	CLOB, -- A list of string object serialization and de-serialization
	suffix	CLOB, -- A list of string object serialization and de-serialization
	period	BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR ContactPoint complex type --
CREATE TABLE PATIENT_CONTACTPOINT (
	element_id	BIGINT NOT NULL,
	patient_id	BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.telecom
	system VARCHAR(128),
	value VARCHAR(2048),
	use VARCHAR(32),
	rank INTEGER,
	period BLOB, -- Period object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)	
);

-- FHIR Address complex type --
CREATE TABLE PATIENT_ADDRESS (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.address
	use VARCHAR(32),
	type VARCHAR(32),
	text VARCHAR(2048),
	line VARCHAR(2024), -- Keep a new line character for suporting multiple lines
	city VARCHAR(32),
	district VARCHAR(32),
	state VARCHAR(32),
	postalCode VARCHAR(32),
	country VARCHAR(32),		
	period BLOB, -- Period object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)		
);

-- FHIR CodeableConcept complex type --
CREATE TABLE PATIENT_CODEABLECONCEPT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.maritalstatus	
	coding_system	VARCHAR(2048),
	coding_version	VARCHAR(32),
	coding_code	VARCHAR(32),
	coding_display	VARCHAR(2048),
	coding_userSelected	BOOLEAN,
	text VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)			
);

-- FHIR Attachment complex type --
CREATE TABLE PATIENT_ATTACHMENT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.photo		
	contentType VARCHAR(32),
	language VARCHAR(32),
	data BLOB, -- base64Binary object serialization and de-serialization
	url VARCHAR(2048),
	size INTEGER,
	hash BLOB, -- base64Binary object serialization and de-serialization
	title VARCHAR(32),
	creation DATETIME,
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)				
);

-- FHIR Reference complex type --
CREATE TABLE PATIENT_REFERENCE (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
	path VARCHAR(1024), -- path for the attribute. The valid value for patient resource: patient.generalpractitioner, patient.managingorganization			
    reference VARCHAR (2048),
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)									
);

-- FHIR BackboneElement complex type --
CREATE TABLE PATIENT_CONTACT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	relationship BLOB, -- A list of CodeableConcept object serialization and de-serialization 	
	name BLOB, -- HumanName object serialization and de-serialization
	telecom BLOB,-- A list of ContactPoint object serialization and de-serialization 	 
	address BLOB, -- Address object serialization and de-serialization
	gender VARCHAR(32), 
	organization BLOB, -- Reference object serialization and de-serialization 
	period BLOB, -- Period object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)					
);

-- FHIR BackboneElement complex type --
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

-- FHIR BackboneElement complex type --
CREATE TABLE PATIENT_COMMUNICATION (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization 
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	language BLOB -- CodeableConcept object serialization and de-serialization  
	preferred BOOLEAN, 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)								
);

-- FHIR BackboneElement complex type --
CREATE TABLE PATIENT_LINK (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension  BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization
	other_reference VARCHAR(2048),
	other_identifier -- Identifer object serialization and de-serialization 
	other_display VARCHAR(2048),
	type VARCHAR(32),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (patient_id) REFERENCES PATIENT(patient_id)										
);
