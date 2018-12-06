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

import java.sql.Clob;
import java.sql.SQLException;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;

public class PatientReferenceMapper extends BaseMapper {

	private Class sourceClz;
	private Class targetClz;
	
	public PatientReferenceMapper() {		
	}
	
	@Override
	public PatientReferenceMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientReferenceMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}
	
	@Override
	public Object map(Object source) 
		throws MapperException {
		// org.hl7.fhir.dstu3.model.HumanName vs com.frt.dr.model.base.PatientHumanName
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier") &&
		    targetClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")) {
			com.frt.dr.model.base.PatientIdentifier frt = new com.frt.dr.model.base.PatientIdentifier();
			org.hl7.fhir.dstu3.model.Identifier hapi = (org.hl7.fhir.dstu3.model.Identifier)source;

			if (hapi.hasId()) {
				frt.setIdentifierId(Long.valueOf(hapi.getId()));
			}
			frt.setPath("Patient.Identifier");
			frt.setUse(hapi.getUse().name());
			frt.setSystem(hapi.getSystem());
			frt.setValue(hapi.getValue());
			// commented out for splice machine clob insert issue
//			frt.setType(SqlHelper.toClob(serializeToJson(hapi.getType(), "type")));
//			frt.setPeriod(SqlHelper.toClob(serializeToJson(hapi.getPeriod(), "period")));
//			frt.setAssigner(SqlHelper.toClob(serializeToJson(hapi.getAssigner(), "assigner")));

			return (Object)frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientIdentifier") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")) {
			org.hl7.fhir.dstu3.model.Identifier hapi = new org.hl7.fhir.dstu3.model.Identifier();
			com.frt.dr.model.base.PatientIdentifier frt = (com.frt.dr.model.base.PatientIdentifier)source;
			
			hapi.setId(String.valueOf(frt.getIdentifierId()));
			hapi.setSystem(frt.getSystem());
			hapi.setValue(frt.getValue());
			hapi.setUse(getIdentifierUse(frt.getUse()));
			// comment out for now
//			hapi.setType(getType(frt.getType()));
//			hapi.setPeriod(getPeriod(frt.getPeriod()));
//			hapi.setAssigner(getAssigner(frt.getAssigner()));
			
			return (Object)hapi;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}		
	}
}
