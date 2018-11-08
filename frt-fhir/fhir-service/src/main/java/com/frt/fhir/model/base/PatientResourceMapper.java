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

import com.frt.fhir.model.MapperException;
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
		// org.hl7.fhir.dstu3.model.Patient vs com.frt.dr.model.base.Patient
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Patient") &&
		    targetClz.getName().equals("com.frt.dr.model.base.Patient")) {
			com.frt.dr.model.base.Patient target = new com.frt.dr.model.base.Patient();
			org.hl7.fhir.dstu3.model.Patient patient = (org.hl7.fhir.dstu3.model.Patient)source;
			target.setActive(Boolean.valueOf(patient.getActive()));
			target.setGender(patient.getGender().toString());			
			return (Object)target;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			org.hl7.fhir.dstu3.model.Patient target = new org.hl7.fhir.dstu3.model.Patient();
			com.frt.dr.model.base.Patient patient = (com.frt.dr.model.base.Patient)source;
			target.setActive(patient.getActive());
			target.setGender(org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.valueOf(patient.getGender()));
			target.setId(patient.getPatientId().toString());			
			return (Object)target;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}
	}
	
	
}
