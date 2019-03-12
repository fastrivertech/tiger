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

/******************************************************************
** SYSCS_UTIL.IMPORT_DATA (
** SYSCS_UTIL.UPSERT_DATA_FROM_FILE (
** SYSCS_UTIL.MERGE_DATA_FROM_FILE (
**    schemaName,
**    tableName,
**    insertColumnList | null,
**    fileOrDirectoryName,
**    columnDelimiter | null,
**    characterDelimiter | null,
**    timestampFormat | null,
**    dateFormat | null,
**    timeFormat | null,
**    badRecordsAllowed,
**    badRecordDirectory | null,
**    oneLineRecords | null,
**    charset | null
**  );
****************************************************************/
-- import from csv files into their corresponding FHIR patient resource tables
-- note: '#' is used as the charator quote char, use other char if the quoted column has value that contains '#'
-- oneLineRecord parameter is 'false' since json string has newline and carriage return in it
-- schema name 'frt2' is the target splice machine DB schema, change it to your target schema accordingly
-- badRecordAllowed, here is set to 100, set it to value that meet your needs, e.g. 0

call SYSCS_UTIL.IMPORT_DATA('frt2', 'resource', null, '../../frt-exported/resource/', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'domain_resource', null, '/home/ec2-user/frt-exported/domain_resource', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient', null, '/home/ec2-user/frt-exported/patient', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_address', null, '/home/ec2-user/frt-exported/patient_address', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_animal', null, '/home/ec2-user/frt-exported/patient_animal', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_attachment', null, '/home/ec2-user/frt-exported/patient_attachment', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_codeableconcept', null, '/home/ec2-user/frt-exported/patient_codeableconcept', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_communication', null, '/home/ec2-user/frt-exported/patient_communication', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_contact', null, '/home/ec2-user/frt-exported/patient_contact', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_contactpoint', null, '/home/ec2-user/frt-exported/patient_contactpoint', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_extension', null, '/home/ec2-user/frt-exported/patient_extension', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_humanname', null, '/home/ec2-user/frt-exported/patient_humanname', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_identifier', null, '/home/ec2-user/frt-exported/patient_identifier', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_link', null, '/home/ec2-user/frt-exported/patient_link', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_reference', null, '/home/ec2-user/frt-exported/patient_reference', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'patient_transaction', null, '/home/ec2-user/frt-exported/patient_transaction', null, '#', null, null, null, 100, null, false, null);
call SYSCS_UTIL.IMPORT_DATA('frt2', 'sequence', null, '/home/ec2-user/frt-exported/sequence', null, '#', null, null, null, 100, null, false, null);


