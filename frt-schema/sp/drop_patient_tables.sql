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
DROP TABLE PATIENT_IDENTIFIER IF EXISTS;
DROP TABLE PATIENT_HUMANNAME IF EXISTS;
DROP TABLE PATIENT_CONTACTPOINT IF EXISTS;
DROP TABLE PATIENT_ADDRESS IF EXISTS;
DROP TABLE PATIENT_CODEABLECONCEPT IF EXISTS;
DROP TABLE PATIENT_ATTACHMENT IF EXISTS;
DROP TABLE PATIENT_CONTACT IF EXISTS;
DROP TABLE PATIENT_ANIMAL IF EXISTS;
DROP TABLE PATIENT_COMMUNICATION IF EXISTS;
DROP TABLE PATIENT_REFERENCE IF EXISTS;
DROP TABLE PATIENT_LINK IF EXISTS;
DROP TABLE PATIENT_EXTENSION IF EXISTS;
DROP TABLE PATIENT IF EXISTS;

-- drop patient transaction table --
DROP TABLE PATIENT_TRANSACTION IF EXISTS;

 -- drop system tables --
DROP TABLE DOMAIN_RESOURCE IF EXISTS;
DROP TABLE RESOURCE IF EXISTS;

-- drop sequences --
DROP SEQUENCE PATIENT_IDENTIFIER_SEQ RESTRICT;
DROP SEQUENCE PATIENT_HUMANNAME_SEQ RESTRICT;
DROP SEQUENCE PATIENT_CONTACTPOINT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_ADDRESS_SEQ RESTRICT;
DROP SEQUENCE PATIENT_CODEABLECONCEPT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_ATTACHMENT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_CONTACT_SEQ RESTRICT;
DROP SEQUENCE PATIENT_LINK_SEQ RESTRICT;
DROP SEQUENCE PATIENT_ANIMAL_SEQ RESTRICT;
DROP SEQUENCE PATIENT_COMMUNICATION_SEQ RESTRICT;
DROP SEQUENCE PATIENT_REFERENCE_SEQ RESTRICT;
DROP SEQUENCE PATIENT_EXTENSION_SEQ RESTRICT;
DROP SEQUENCE PATIENT_SEQ RESTRICT;

-- drop system table sequences
DROP SEQUENCE RESOURCE_SEQ RESTRICT;
DROP SEQUENCE DOMAIN_RESOURCE_SEQ RESTRICT;

-- drop patient transaction table sequences
DROP SEQUENCE PATIENT_TRANSACTION_SEQ RESTRICT;

DROP TABLE SEQUENCE;
