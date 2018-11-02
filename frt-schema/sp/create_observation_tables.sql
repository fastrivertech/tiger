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
	domain_resource_id BIGINT NOT NULL,
	-- identifier -- Σ, refer to OBSERVATION_IDENTIFIER table
	-- basedOn -- Σ, refer to OBSERVATION_REFERENCE table
	status VARCHAR(32) NOT NULL, -- ?!Σ	
	-- category -- refer to OBSERVATION_CODEABLECONCEPT
	-- code -- Σ, refer to OBSERVATION_CODEABLECONCEPT	
	effectiveDateTime TIMESTAMP, -- Σ
	effectivePeriod CLOB, -- Σ, Period object serialization and de-serialization
	issued TIMESTAMP, -- Σ
	-- performer -- Σ, refer to OBSERVATION_REFERENCE table
	value BLOB, -- Value object of any FHIR primitive and complex data types, serialization and de-serialization 
	dataAbsentReason CLOB, -- I, CodeableConcept object serialization and de-serialization 	
	interpretation CLOB, -- CodeableConcept object serialization and de-serialization 		
	comment	VARCHAR(2048),
	bodySite CLOB, -- CodeableConcept object serialization and de-serialization 	
	method CLOB, -- CodeableConcept object serialization and de-serialization 	
	specimen CLOB, -- Reference object serialization and de-serialization 	
	device CLOB, -- Reference object serialization and de-serialization
	-- referenceRange -- I, refer to OBSERVATION_REFERENCERANGE table
	-- related -- Σ, refer to OBSERVATION_RELATED table
	-- component -- Σ, refer to OBSERVATION_COMPONENT table
	PRIMARY KEY (observation_id), 	
	FOREIGN KEY (domain_resource_id) REFERENCES DOMAIN_RESOURCE(domain_resource_id)	
);

-- FHIR observation resource extension table, I and affected by constraints --
CREATE TABLE OBSERVATION_EXTENSION (
	extension_id BIGINT NOT NULL,
	observation_id	BIGINT NOT NULL,
	secondary_id BIGINT NOT NULL,	
	path VARCHAR(128), -- path patterns: observation.domain.resource/observation.[child]/extension
	url	VARCHAR(128) NOT NULL, 	
	value CLOB, -- value of any FHIR primitive and complex data types
	isModifier BOOLEAN DEFAULT false, -- default false
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR observation resource element extension table, I and affected by constraints --
CREATE TABLE OBSERVATION_ELEMENT_EXTENSION (
	extension_id BIGINT NOT NULL,
	observation_id	BIGINT NOT NULL,
	secondary_id BIGINT NOT NULL,	
	path VARCHAR(128), -- path patterns:observation.[attribute]/observation.[child].[attribute]/extesion
	url	VARCHAR(128) NOT NULL, 	
	value BLOB, -- value of any FHIR primitive and complex data types
	isModifier boolean DEFAULT false, -- default false
	PRIMARY KEY (extension_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);


-- FHIR Identifier complex type, Σ --
CREATE TABLE OBSERVATION_IDENTIFIER (
	identifier_id BIGINT NOT NULL,
	observation_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute. The valid value for observation resource: observation.identifier
	use	VARCHAR(32), -- ?!Σ
	type CLOB, -- Σ, CodeableConcept object serialization and de-serialization 	  	 	
	system	VARCHAR(128), -- Σ
	value VARCHAR(128), -- Σ 
	period	CLOB,  -- Σ, Period object serialization and de-serialization 	  	 
	assigner CLOB, -- Σ, Reference object serialization and de-serialization
	PRIMARY KEY (identifier_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR CodeableConcept complex type for category and code --
CREATE TABLE OBSERVATION_CODEABLECONCEPT (
	codeableconcept_id BIGINT NOT NULL, -- as well as is element_id
	observation_id BIGINT NOT NULL,
	path VARCHAR(128) NOT NULL, -- path for the attribute: observation.category, observation.code
	coding_system	VARCHAR(128), -- Σ
	coding_version	VARCHAR(32), -- Σ
	coding_code	VARCHAR(32), -- Σ
	coding_display	VARCHAR(2048), -- Σ
	coding_userSelected	BOOLEAN DEFAULT false, -- Σ
	txt VARCHAR(2048), -- Σ
	PRIMARY KEY (codeableconcept_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)			
);

-- FHIR Reference complex type for basedon, subject, context, performer, ΣI --
CREATE TABLE OBSERVATION_REFERENCE (
	reference_id BIGINT NOT NULL, -- as well as is element_id
	observation_id BIGINT NOT NULL,
	path VARCHAR(128), -- path for the attribute: observation.basedon, observation.subject, observation.context, observation.performer			
    reference VARCHAR (2048), -- ΣI
	identifier CLOB, -- Σ, Identifier object serialization and de-serialization   	
	display VARCHAR(2048), -- Σ
	PRIMARY KEY (reference_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)									
);

-- FHIR BackboneElement complex type, I --
CREATE TABLE OBSERVATION_REFERENCERANGE (
	referencerange_id BIGINT NOT NULL,  -- as well as is element_id
	observation_id BIGINT NOT NULL,
	low CLOB, -- SimpleQuantity object serialization and de-serialization 
	high CLOB, -- SimpleQuantity object serialization and de-serialization
	type CLOB, -- CodeableConcept object serialization and de-serialization
	appliesTo CLOB, -- A list CodeableConcept object serialization and de-serialization
	age_low CLOB, -- SimpleQuantity object serialization and de-serialization
	age_high CLOB, -- SimpleQuantity object serialization and de-serialization	
	txt VARCHAR(2048),	
	PRIMARY KEY (referencerange_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR BackboneElement complex type, Σ --
CREATE TABLE OBSERVATION_RELATED (
	related_id BIGINT NOT NULL, -- as well as is element_id
	observation_id BIGINT NOT NULL,
	type VARCHAR(32),
	target CLOB NOT NULL, -- Reference object serialization and de-serialization
	PRIMARY KEY (related_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

-- FHIR BackboneElement complex type, Σ--
CREATE TABLE OBSERVATION_COMPONENT (
	component_id BIGINT NOT NULL,  -- as well as is element_id
	observation_id BIGINT NOT NULL,
	code CLOB NOT NULL, -- CodeableConcept object serialization and de-serialization 
	value BLOB, -- Quantity/CodeableConcept/string/Range/Ratio/SampleData/Atatchment/time/dateTime/Period object serialization and de-serialization 
	dataAbsentReason CLOB, -- CodeableConcept object serialization and de-serialization
	interpretation CLOB, -- CodeableConcept object serialization and de-serialization
	referenceRange CLOB, -- ReferenceRange object serialization and de-serialization
	PRIMARY KEY (component_id), 	
	FOREIGN KEY (observation_id) REFERENCES OBSERVATION(observation_id)	
);

