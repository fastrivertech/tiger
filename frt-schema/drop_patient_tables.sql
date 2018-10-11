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
 -- drop system tables --
DROP TABLE SYSTEM CASCADE CONSTRAINTS;
DROP TABLE RESOURCE CASCADE CONSTRAINTS;

-- drop FHIR patient resource relevant tables --
DROP TABLE PATIENT CASCADE CONSTRAINTS;
DROP TABLE PATIENT_IDENTIFIER CASCADE CONSTRAINTS;
DROP TABLE PATIENT_NAME CASCADE CONSTRAINTS;
DROP TABLE PATIENT_TELECOM CASCADE CONSTRAINTS;
DROP TABLE PATIENT_ADDRESS CASCADE CONSTRAINTS;
DROP TABLE PATIENT_MARITALSTATUS CASCADE CONSTRAINTS;
DROP TABLE PATIENT_PHOTO CASCADE CONSTRAINTS;
DROP TABLE PATIENT_CONTACT CASCADE CONSTRAINTS;
DROP TABLE PATIENT_ANIMAL CASCADE CONSTRAINTS;
DROP TABLE PATIENT_COMMUNICATION CASCADE CONSTRAINTS;
DROP TABLE PATIENT_GENERALPRACTITIONER CASCADE CONSTRAINTS;
DROP TABLE PATIENT_MANAGINGORGANIZATION CASCADE CONSTRAINTS;
DROP TABLE PATIENT_LINK CASCADE CONSTRAINTS;
