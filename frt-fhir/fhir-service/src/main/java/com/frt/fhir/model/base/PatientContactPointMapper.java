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
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientContactPointMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientContactPointMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientContactPointMapper() {
	}

	@Override
	public PatientContactPointMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientContactPointMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientContactPointMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientContactPoint frt = null;
		
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.ContactPoint")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientContactPoint")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_CONTACTPOINT);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setPath("Patient.telecom");
			frt.setUse(root.get("use")!=null?root.get("use").getAsString():null);
			frt.setValue(root.get("value")!=null?root.get("value").getAsString():null);
			frt.setSystem(root.get("system")!=null?root.get("system").getAsString():null);
			frt.setRank(root.get("rank")!=null?root.get("rank").getAsInt():null);
			frt.setPeriod(root.getAsJsonObject("period")!=null?root.getAsJsonObject("period").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientContactPoint")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.ContactPoint")) {
			throw new IllegalStateException("PatientContactPointMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientContactPointMapper.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
