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

-- export all FHIR patient resource tables
-- '#' is used as the quote
export('/home/ec2-user/frt-exported/resource', false, null, null, null, '#') select * from resource;
export('/home/ec2-user/frt-exported/domain_resource', false, null, null, null, '#') select * from domain_resource;
export('/home/ec2-user/frt-exported/patient', false, null, null, null, '#') select * from patient;
export('/home/ec2-user/frt-exported/patient_address', false, null, null, null, '#') select * from patient_address;
export('/home/ec2-user/frt-exported/patient_animal', false, null, null, null, '#') select * from patient_animal;
export('/home/ec2-user/frt-exported/patient_attachment', false, null, null, null, '#') select * from patient_attachment;
export('/home/ec2-user/frt-exported/patient_codeableconcept', false, null, null, null, '#') select * from patient_codeableconcept;
export('/home/ec2-user/frt-exported/patient_communication', false, null, null, null, '#') select * from patient_communication;
export('/home/ec2-user/frt-exported/patient_contact', false, null, null, null, '#') select * from patient_contact;
export('/home/ec2-user/frt-exported/patient_contactpoint', false, null, null, null, '#') select * from patient_contactpoint;
export('/home/ec2-user/frt-exported/patient_extension', false, null, null, null, '#') select * from patient_extension;
export('/home/ec2-user/frt-exported/patient_humanname', false, null, null, null, '#') select * from patient_humanname;
export('/home/ec2-user/frt-exported/patient_identifier', false, null, null, null, '#') select * from patient_identifier;
export('/home/ec2-user/frt-exported/patient_link', false, null, null, null, '#') select * from patient_link;
export('/home/ec2-user/frt-exported/patient_reference', false, null, null, null, '#') select * from patient_reference;
export('/home/ec2-user/frt-exported/patient_transaction', false, null, null, null, '#') select * from patient_transaction;
export('/home/ec2-user/frt-exported/sequence', false, null, null, null, '#') select * from sequence;


