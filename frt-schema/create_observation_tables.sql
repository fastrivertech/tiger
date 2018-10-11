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
	status VARCHAR,	
	effectiveDateTime DATATIME,
	effectivePeriod BLOB, -- Period object serialization and de-serialization
	issued DATATIME,
	value BLOB, -- Value object serialization and de-serialization
	dataAbsentReason BLOB, -- CodeableConcept object serialization and de-serialization 	
	interpretation BLOB, -- CodeableConcept object serialization and de-serialization 		
	comment	VARCHAR,
	bodySite BLOB, -- CodeableConcept object serialization and de-serialization 	
	method BLOB, -- CodeableConcept object serialization and de-serialization 	
	specimen BLOB, -- Reference object serialization and de-serialization 	
	device BLOB, -- Reference object serialization and de-serialization
	PRIMARY KEY (observation_id), 	
	FOREIGN KEY (resource_id) REFERENCES RESOURCE(resource_id)	
);

CREATE TABLE OBSERVATION_IDENTIFIER (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
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
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

CREATE TABLE OBSERVATION_BASEDON (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_CATEGORY (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_CODE (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_SUBJECT (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_CONTEXT (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_PERFORMER (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization  
    reference VARCHAR,
	identifier BLOB, -- Identifier object serialization and de-serialization   	
	display VARCHAR,
	-- Element Extension
	reference_extension BLOB, -- Extension object serialization and de-serialization
	display_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

CREATE TABLE OBSERVATION_REFERENCERANGE (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	low BLOB, -- SimpleQuantity object serialization and de-serialization 
	high BLOB, -- SimpleQuantity object serialization and de-serialization
	type BLOB, -- CodeableConcept object serialization and de-serialization
	appliesTo BLOB, -- A list CodeableConcept object serialization and de-serialization
	age BLOB, -- A list CodeableConcept object serialization and de-serialization
	text VARCHAR,	
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

CREATE TABLE OBSERVATION_RELATED (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	type VARCHAR,
	target BLOB, -- Reference object serialization and de-serialization
	-- Element Extension
	type_extension BLOB, -- Extension object serialization and de-serialization 
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

CREATE TABLE OBSERVATION_COMPONENT (
	element_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	element_extension BLOB, -- Extension object serialization and de-serialization
	backbone_modifierExtension BLOB, -- Extension object serialization and de-serialization 
	code BLOB, -- CodeableConcept object serialization and de-serialization 
	value BLOB, -- Value object serialization and de-serialization 
	dataAbsentReason BLOB, -- CodeableConcept object serialization and de-serialization
	interpretation BLOB, -- CodeableConcept object serialization and de-serialization
	referenceRange BLOB, -- ReferenceRange object serialization and de-serialization
	PRIMARY KEY (element_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

