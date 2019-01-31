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
-- create user and schema --
CALL SYSCS_UTIL.SYSCS_CREATE_USER('frt','frt');
CALL SYSCS_UTIL.SYSCS_SET_USER_ACCESS('frt','FULLACCESS');
-- CREATE SCHEMA fhir_patient AUTHORIZATION frt;
