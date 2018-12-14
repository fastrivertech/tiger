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
-- drop FHIR patient resource relevant tables --
DROP TABLE PATIENT_IDENTIFIER;
DROP TABLE PATIENT_HUMANNAME;
DROP TABLE PATIENT_CONTACTPOINT;
DROP TABLE PATIENT_ADDRESS;
DROP TABLE PATIENT_CODEABLECONCEPT;
DROP TABLE PATIENT_ATTACHMENT;
DROP TABLE PATIENT_CONTACT;
DROP TABLE PATIENT_ANIMAL;
DROP TABLE PATIENT_COMMUNICATION;
DROP TABLE PATIENT_REFERENCE;
DROP TABLE PATIENT_LINK;
DROP TABLE PATIENT_EXTENSION;
DROP TABLE PATIENT_ELEMENT_EXTENSION;
DROP TABLE PATIENT;

 -- drop system tables --
DROP TABLE DOMAIN_RESOURCE;
DROP TABLE RESOURCE;
DROP TABLE SYSTEM_RESOURCE;

-- drop sequences --
DROP SEQUENCE PATIENT_IDENTIFIER_SEQ RESTRICT;
DROP SEQUENCE PATIENT_HUMANNAME_SEQ RESTRICT;
DROP SEQUENCE PATIENT_ANIMAL_SEQ RESTRICT;
DROP SEQUENCE PATIENT_ATTACHMENT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_CONTACTPOINT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_COMMUNICATION_SEQ RESTRICT;
DROP SEQUENCE PATIENT_SEQ RESTRICT;
