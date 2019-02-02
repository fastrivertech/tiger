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
package com.frt.fhir.model.map.base;

import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.map.MapperException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientAddressMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientAddressMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

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
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientAddressMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientAddress frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Address")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientAddress")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_ADDRESS);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setPath("Patient.address");
			frt.setCity(root.get("city")!=null?root.get("city").getAsString():null);
			frt.setDistrict(root.get("district")!=null?root.get("district").getAsString():null);
			frt.setState(root.get("state")!=null?root.get("state").getAsString():null);
			frt.setCountry(root.get("country")!=null?root.get("country").getAsString():null);
			frt.setLine(root.getAsJsonArray("line")!=null?root.getAsJsonArray("line").toString():null);
			frt.setPostalcode(root.get("postalCode")!=null?root.get("postalCode").getAsString():null);
			frt.setType(root.get("type")!=null?root.get("type").getAsString():null);
			frt.setUse(root.get("use")!=null?root.get("use").getAsString():null);
			frt.setTxt(root.get("txt")!=null?root.get("txt").getAsString():null);
			frt.setPeriod(root.getAsJsonObject("period")!=null?root.getAsJsonObject("period").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAddress")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Address")) {
			throw new IllegalStateException("PatientAddressMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientAddressMapper.map() from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}

}
