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

public class PatientAddressMapper extends BaseMapper {

	private Class sourceClz;
	private Class targetClz;
	
	public PatientAddressMapper() {		
	}
	
	@Override
	public PatientAddressMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientAddressMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}
	
	@Override
	public Object map(Object source) 
		throws MapperException {
		// org.hl7.fhir.dstu3.model.Address vs com.frt.dr.model.base.PatientAddress
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Address") &&
		    targetClz.getName().equals("com.frt.dr.model.base.PatientAddress")) {
			com.frt.dr.model.base.PatientAddress frt = new com.frt.dr.model.base.PatientAddress();
			org.hl7.fhir.dstu3.model.Address hapi = (org.hl7.fhir.dstu3.model.Address)source;

			if (hapi.hasId()) {
				frt.setAddressId(Long.valueOf(hapi.getId()));
			}
			frt.setPath("Patient.Address");
			frt.setUse(hapi.getUse().name());
			if (hapi.hasType()) {
				frt.setType(hapi.getType().toCode());
			}
			frt.setTxt(hapi.getText());
			frt.setLine(hapi.getLine()); 
			frt.setCity(hapi.getCity());;
			frt.setDistrict(hapi.getDistrict());;
			frt.setState(hapi.getState());;
			frt.setPostalcode(hapi.getPostalCode());;
			frt.setCountry(hapi.getCountry());;

			// commented out for splice machine clob insert issue
//			frt.setPeriod(SqlHelper.toClob(serializeToJson(hapi.getPeriod(), "period")));

			return (Object)frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAddress") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Address")) {
			org.hl7.fhir.dstu3.model.Address hapi = new org.hl7.fhir.dstu3.model.Address();
			com.frt.dr.model.base.PatientAddress frt = (com.frt.dr.model.base.PatientAddress)source;
			
			hapi.setId(String.valueOf(frt.getAddressId()));
			hapi.setUse(org.hl7.fhir.dstu3.model.Address.AddressUse.valueOf(frt.getUse()));
			// comment out for now
//			hapi.setPeriod(getPeriod(frt.getPeriod()));
			
			return (Object)hapi;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}		
	}

}
