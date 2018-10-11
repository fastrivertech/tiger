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
 -- FHIR observation resource relevant tables --
CREATE TABLE OBSERVATION (
	observation_id BIGINT NOT NULL,
	resource_id BIGINT NOT NULL,
	status VARCHAR(32) NOT NULL,	
	effectiveDateTime DATETIME,
	effectivePeriod BLOB, -- Period object serialization and de-serialization
	issued DATETIME,
	value BLOB, -- Value object serialization and de-serialization
	dataAbsentReason BLOB, -- CodeableConcept object serialization and de-serialization 	
	interpretation BLOB, -- CodeableConcept object serialization and de-serialization 		
	comment	VARCHAR(2048),
	bodySite BLOB, -- CodeableConcept object serialization and de-serialization 	
	method BLOB, -- CodeableConcept object serialization and de-serialization 	
	specimen BLOB, -- Reference object serialization and de-serialization 	
	device BLOB, -- Reference object serialization and de-serialization
	PRIMARY KEY (observation_id), 	
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)	
);

-- FHIR observation resource element extension table --
CREATE TABLE PATIENT_ELEMENT_EXTENSION (
	extension_id BIGINT NOT NULL,
	element_id	BIGINT NOT NULL,
	observation_id	BIGINT NOT NULL,
	path VARCHAR, -- observation.status, observation.identifier.use, etc.
	extension BLOB, -- Extension object serialization and de-serialization
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (element_id, observation_id) REFERENCES RESOURCE(element_id, observation_id)	
);

-- FHIR Identifier complex type --
CREATE TABLE OBSERVATION_IDENTIFIER (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	use	VARCHAR(32), 
	type	CLOB, -- CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR(2048),
	value VARCHAR(2048), 
	period	BLOB,  -- Period object serialization and de-serialization 	  	 
	assigner BLOB, -- Reference object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR Reference complex type --
CREATE TABLE OBSERVATION_BASEDON (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR(2048),
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

-- FHIR CodeableConcept complex type --
CREATE TABLE OBSERVATION_CATEGORY (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	coding_system	VARCHAR(2048),
	coding_version	VARCHAR(32),
	coding_code	VARCHAR(32),
	coding_display	VARCHAR(2048),
	coding_userSelected	BOOLEAN,
	text VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR CodeableConcept complex type --
CREATE TABLE OBSERVATION_CODE (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	coding_system	VARCHAR(2048),
	coding_version	VARCHAR(32),
	coding_code	VARCHAR(32),
	coding_display	VARCHAR(2048),
	coding_userSelected	BOOLEAN,
	text VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR Reference complex type --
CREATE TABLE OBSERVATION_SUBJECT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR (2048),
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR Reference complex type --
CREATE TABLE OBSERVATION_CONTEXT (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR (2048),
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR Reference complex type --
CREATE TABLE OBSERVATION_PERFORMER (
	element_id BIGINT NOT NULL,
	patient_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR (2048),
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR(2048),
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR BackboneElement complex type --
CREATE TABLE OBSERVATION_REFERENCERANGE (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	low BLOB, -- SimpleQuantity object serialization and de-serialization 
	high BLOB, -- SimpleQuantity object serialization and de-serialization
	type BLOB, -- CodeableConcept object serialization and de-serialization
	appliesTo BLOB, -- A list CodeableConcept object serialization and de-serialization
	age_low BLOB, -- SimpleQuantity object serialization and de-serialization
	age_high BLOB, -- SimpleQuantity object serialization and de-serialization	
	text VARCHAR(2048),	
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR BackboneElement complex type --
CREATE TABLE OBSERVATION_RELATED (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	type VARCHAR(32),
	target_reference VARCHAR(2048),
    target_identifier BLOB, -- Reference object serialization and de-serialization
	target_display VARCHAR(2048)
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR BackboneElement complex type --
CREATE TABLE OBSERVATION_COMPONENT (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	code BLOB, -- CodeableConcept object serialization and de-serialization 
	valueQuantity BLOB, -- Quantity object serialization and de-serialization 
	valueCodeableConcept BLOB, -- CodeableConcept object serialization and de-serialization
	valueString VARCHAR(2048),
	valueRange BLOB, -- Range object serialization and de-serialization
	valueRatio BLOB, -- Ratio object serialization and de-serialization
	valueSampledData BLOB, -- SampledData object serialization and de-serialization
	valueAttachment BLOB, -- Attachment object serialization and de-serialization
	valueTime TIME,
	valueDateTime DATETIME,
	valuePeriod BLOB, -- Extension object serialization and de-serialization	
	dataAbsentReason BLOB, -- CodeableConcept object serialization and de-serialization
	interpretation BLOB, -- CodeableConcept object serialization and de-serialization
	referenceRange BLOB, -- ReferenceRange object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

