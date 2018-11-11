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

public class PatientHumanNameMapper implements ResourceMapper {

	private Class sourceClz;
	private Class targetClz;
	
	public PatientHumanNameMapper() {		
	}
	
	@Override
	public PatientHumanNameMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientHumanNameMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}
	
	@Override
	public Object map(Object source) 
		throws MapperException {
		// org.hl7.fhir.dstu3.model.HumanName vs com.frt.dr.model.base.PatientHumanName
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName") &&
		    targetClz.getName().equals("com.frt.dr.model.base.PatientHumanName")) {
			com.frt.dr.model.base.PatientHumanName target = new com.frt.dr.model.base.PatientHumanName();
			org.hl7.fhir.dstu3.model.HumanName name = (org.hl7.fhir.dstu3.model.HumanName)source;
			
			target.setHumannameId(Long.valueOf(name.getId()));			
			target.setUse(name.getUse().name());			
			target.setFamily(name.getFamily());			
			
			return (Object)target;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientHumanName") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName")) {
			org.hl7.fhir.dstu3.model.HumanName target = new org.hl7.fhir.dstu3.model.HumanName();
			com.frt.dr.model.base.PatientHumanName name = (com.frt.dr.model.base.PatientHumanName)source;
			
			target.setId(name.getHumannameId().toString());
			target.setUse(org.hl7.fhir.dstu3.model.HumanName.NameUse.valueOf(name.getUse()));
			target.setFamily(name.getFamily());
			
			return (Object)target;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}		
	}
}
