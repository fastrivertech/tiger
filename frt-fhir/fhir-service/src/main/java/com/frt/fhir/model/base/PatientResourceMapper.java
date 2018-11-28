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
		// org.hl7.fhir.dstu3.model.Patient => com.frt.dr.model.base.Patient
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Patient") &&
		    targetClz.getName().equals("com.frt.dr.model.base.Patient")) {
			com.frt.dr.model.base.Patient target = new com.frt.dr.model.base.Patient();
			org.hl7.fhir.dstu3.model.Patient patient = (org.hl7.fhir.dstu3.model.Patient)source;
			
			target.setPatientId(Long.valueOf(patient.getId().replace("Patient/", "")));
			target.setActive(Boolean.valueOf(patient.getActive()));
			target.setGender(patient.getGender().toString());
			target.setBirthDate(patient.getBirthDate());

			if (patient.getDeceased()!=null) {
				if (patient.getDeceased() instanceof BooleanType) {
					BooleanType bt = null;
					try {
						bt = patient.getDeceasedBooleanType();
					}
					catch (FHIRException e) {
						throw new MapperException("FHIRException caught when mapping FHIR Patient to FRT Patient attribute: Deceased", e);
					}
					if (bt!=null) {
						target.setDeceasedBoolean(bt.getValue());
					}
					else {
						// assume alive
						target.setDeceasedBoolean(false);
					}
				}
				else {
					DateTimeType dtt = null;
					try {
						dtt = patient.getDeceasedDateTimeType();
					} catch (FHIRException e) {
						e.printStackTrace();
						throw new MapperException("FHIRException caught when mapping FHIR Patient to FRT Patient attribute: Deceased.", e);
					}
					target.setDeceasedDateTime(new Timestamp(dtt.getValue().getTime()));
				}
			}
			else {
				// per FHIR spec - assume patient is alive
				target.setDeceasedBoolean(false);
				target.setDeceasedDateTime(null);
			}
			
			if (patient.getMultipleBirth()!=null) {
				if (patient.getMultipleBirth() instanceof BooleanType) {
					try {
						BooleanType mbt = patient.getMultipleBirthBooleanType();
						if (mbt!=null) {
							target.setMultipleBirthBoolean(mbt.getValue());
						}
						else {
							// assume default - not multiple birth
							target.setMultipleBirthBoolean(false);
						}
					} catch (FHIRException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new MapperException("FHIRException caught when mapping FHIR Patient to FRT Patient attribute: MultipleBirth.", e);
					}
				}
				else {
					// it's a birth order
					try {
						IntegerType it = patient.getMultipleBirthIntegerType();
						if (it!=null) {
							target.setMultipleBirthInteger(it.getValue());
						}
						else {
							target.setMultipleBirthInteger(null);
						}
					} catch (FHIRException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new MapperException("FHIRException caught when mapping FHIR Patient to FRT Patient attribute: MultipleBirth.", e);
					}
				}
			}
			else {
				// assume not multiplebirth
				target.setMultipleBirthBoolean(false);
				target.setMultipleBirthInteger(null);
			}
			
			// org.hl7.fhir.dstu3.model.HumanName => com.frt.dr.model.base.PatientHumanName			
			List<org.hl7.fhir.dstu3.model.HumanName> names = patient.getName();
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");			
			PatientHumanNameMapper humanMapper = new PatientHumanNameMapper();
			names.forEach(name->{												
									Object mapped = humanMapper.from(resourcePair.getFhir()).to(resourcePair.getFrt()).map(name);
									target.getNames().add((com.frt.dr.model.base.PatientHumanName)mapped);
								});			
			return (Object)target;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			// com.frt.dr.model.base.Patient => org.hl7.fhir.dstu3.model.Patient			
			org.hl7.fhir.dstu3.model.Patient target = new org.hl7.fhir.dstu3.model.Patient();
			com.frt.dr.model.base.Patient patient = (com.frt.dr.model.base.Patient)source;
			
			target.setId(patient.getPatientId().toString());			
			target.setActive(patient.getActive());
			target.setGender(org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.valueOf(patient.getGender()));
			target.setBirthDate(patient.getBirthDate());
			target.setDeceased(new BooleanType(patient.getDeceasedBoolean()));
			target.setDeceased(new DateTimeType(patient.getDeceasedDateTime()));
			target.setMultipleBirth(new BooleanType(patient.getMultipleBirthBoolean()));
			target.setMultipleBirth(new IntegerType(patient.getMultipleBirthInteger()));
			
			// com.frt.dr.model.base.PatientHumanName => org.hl7.fhir.dstu3.model.HumanName			
			List<com.frt.dr.model.base.PatientHumanName> names = patient.getNames();
			ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");			
			PatientHumanNameMapper humanMapper = new PatientHumanNameMapper();
			names.forEach(name->{												
									Object mapped = humanMapper.from(resourcePair.getFrt()).to(resourcePair.getFhir()).map(name);
									target.getName().add((org.hl7.fhir.dstu3.model.HumanName)mapped);
								});									
			return (Object)target;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}
	}
		
}
