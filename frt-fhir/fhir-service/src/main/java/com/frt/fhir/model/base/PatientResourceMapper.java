/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.model.base;

import java.sql.Timestamp;
import java.util.List;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.exceptions.FHIRException;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;

/**
 * PatientResourceMapper class
 * @author chaye
 */
public class PatientResourceMapper implements ResourceMapper {
	private Class sourceClz;
	private Class targetClz;
	
	public PatientResourceMapper() {		
	}
	
	@Override
	public PatientResourceMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientResourceMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}
	
	@Override
	public Object map(Object source) 
		throws MapperException {
		
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Patient") &&
		    targetClz.getName().equals("com.frt.dr.model.base.Patient")) {
			
			// org.hl7.fhir.dstu3.model.Patient => com.frt.dr.model.base.Patient
			// hapi Patient ==> frt Patient
			try {
				com.frt.dr.model.base.Patient frtPatient = new com.frt.dr.model.base.Patient();
				org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient)source;
				// resource
				frtPatient.setPatientId(Long.valueOf(hapiPatient.getId().replace("Patient/", "")));
				
				// domain resource
				
				// patient resource: active 
				frtPatient.setActive(Boolean.valueOf(hapiPatient.getActive()));
				
				// patient resource: name
				// org.hl7.fhir.dstu3.model.HumanName => com.frt.dr.model.base.PatientHumanName			
				List<org.hl7.fhir.dstu3.model.HumanName> names = hapiPatient.getName();
				ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");			
				PatientHumanNameMapper humanNameMapper = new PatientHumanNameMapper();
				names.forEach(name->{												
										Object mappedName = humanNameMapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(name);
										((com.frt.dr.model.base.PatientHumanName)mappedName).setPatient((com.frt.dr.model.base.Patient)frtPatient);
										frtPatient.getNames().add((com.frt.dr.model.base.PatientHumanName)mappedName);
									});			
				
				// patient resource: telecom
				
				// patient resource: gender
				frtPatient.setGender(hapiPatient.getGender().toString());
				
				// patient resource: birthDate
				frtPatient.setBirthDate(hapiPatient.getBirthDate());
				
				// patient resource: deceased, per FHIR 3.0.1 spec - assume patient is alive
				// patient resource: deceasedBoolean
				if (hapiPatient.getDeceased() != null && 
					hapiPatient.getDeceased() instanceof BooleanType) {
					frtPatient.setDeceasedBoolean(hapiPatient.getDeceasedBooleanType().booleanValue());	
				} else if (hapiPatient.getDeceased() != null && 
						   hapiPatient.getDeceased() instanceof DateTimeType) {
				// patient resource: deceasedDateTime
					frtPatient.setDeceasedDateTime(new java.sql.Timestamp(hapiPatient.getDeceasedDateTimeType().getValue().getTime()));
				} else {
					frtPatient.setDeceasedBoolean(false);
					frtPatient.setDeceasedDateTime(null);
				}
				
				// patient resource: address
				
				// patient resource: maritalStatus
				
				// patient resource: multipleBirth, per FHIR 3.0.1 spec, assume not multiplebirth 
				// patient resource: multipleBirthBoolean
				// note, have to do check as below to prevent class casting exception, e.g. try to cast hapi boolean to integer
				// when the MultipleBirth is a boolean
				// FRT MultipleBoolean and MultipleInteger can be:
				// (1) Boolean is NULL and Integer NOT NULL
				// (2) Boolean is NOT NULL and Integer is NULL
				// (3) Boolean is NULL and Integer is NULL ===> HAPI Patient.hasMultipleBirth() must be set to false
				if (hapiPatient.hasMultipleBirth()) {
					if (hapiPatient.getMultipleBirth() instanceof BooleanType) {
						frtPatient.setMultipleBirthBoolean(hapiPatient.getMultipleBirthBooleanType().booleanValue());
						frtPatient.setMultipleBirthInteger(null);
					} else {
						// patient resource: multipleBirthInteger
						frtPatient.setMultipleBirthInteger(hapiPatient.getMultipleBirthIntegerType().getValue());
						frtPatient.setMultipleBirthBoolean(null);
					}
				}
				else {
					// the default
					frtPatient.setMultipleBirthBoolean(null);
					frtPatient.setMultipleBirthInteger(null);
				}
				
				// patient resource: photo
				
				// patient resource: contact
				
				// patient resource: animal
	
				// patient resource: communication
	
				// patient resource: generalPractitioner
				
				// patient resource: managingOrganization
					
				// patient resource: link
				
				return (Object)frtPatient;
			} catch (FHIRException ex) {
				throw new MapperException(ex);
			}
			
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			
			// com.frt.dr.model.base.Patient => org.hl7.fhir.dstu3.model.Patient
			// frt Patient ==> hapi Patient
			org.hl7.fhir.dstu3.model.Patient hapiPatient = new org.hl7.fhir.dstu3.model.Patient();
			com.frt.dr.model.base.Patient frtPatient = (com.frt.dr.model.base.Patient)source;
			// resource
			hapiPatient.setId(frtPatient.getPatientId().toString());	
			
			// domain resource
			
			// patient resource: active
			hapiPatient.setActive(frtPatient.getActive());
			
			// patient resource: name
			// com.frt.dr.model.base.PatientHumanName => org.hl7.fhir.dstu3.model.HumanName	
			List<com.frt.dr.model.base.PatientHumanName> names = frtPatient.getNames();
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");			
			PatientHumanNameMapper humanNameMapper = new PatientHumanNameMapper();
			names.forEach(name->{												
									Object mappedName = humanNameMapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map(name);
									hapiPatient.getName().add((org.hl7.fhir.dstu3.model.HumanName)mappedName);
						 });									
									
			// patient resource: telecom
			
			// patient resource: gender
			hapiPatient.setGender(org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.valueOf(frtPatient.getGender()));
			
			// patient resource: birthDate
			hapiPatient.setBirthDate(frtPatient.getBirthDate());
			
			// patient resource: deceased, per FHIR 3.0.1 spec - assume patient is alive
			// patient resource: deceasedBoolean			
			hapiPatient.setDeceased(new BooleanType(frtPatient.getDeceasedBoolean()));
			// patient resource: deceasedDateTime
			hapiPatient.setDeceased(new DateTimeType(frtPatient.getDeceasedDateTime()));
			

			// patient resource: address
			
			// patient resource: maritalStatus
			
			// patient resource: multipleBirth, per FHIR 3.0.1 spec, assume not multiplebirth 
			// patient resource: multipleBirthBoolean
			if (frtPatient.getMultipleBirthBoolean()!=null) {
				hapiPatient.setMultipleBirth(new BooleanType(frtPatient.getMultipleBirthBoolean()));
			}
			// patient resource: multipleBirthInteger
			if (frtPatient.getMultipleBirthInteger()!=null) {
				hapiPatient.setMultipleBirth(new IntegerType(frtPatient.getMultipleBirthInteger()));
			}
			
			// patient resource: photo
			
			// patient resource: contact
			
			// patient resource: animal

			// patient resource: communication

			// patient resource: generalPractitioner
			
			// patient resource: managingOrganization
				
			// patient resource: link			
		
			return (Object)hapiPatient;
			
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}
	}
		
}
