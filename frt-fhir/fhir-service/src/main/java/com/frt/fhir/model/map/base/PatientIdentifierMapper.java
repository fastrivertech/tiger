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

public class PatientIdentifierMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientIdentifierMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientIdentifierMapper() {
	}

	@Override
	public PatientIdentifierMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientIdentifierMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientIdentifierMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}
		com.frt.dr.model.base.PatientIdentifier frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_IDENTIFIER);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setUse(root.get("use")!=null?root.get("use").getAsString():null);
			frt.setValue(root.get("value")!=null?root.get("value").getAsString():null);
			frt.setSystem(root.get("system")!=null?root.get("system").getAsString():null);
			frt.setPeriod(root.getAsJsonObject("period")!=null?root.getAsJsonObject("period").toString():null);
			frt.setType(root.getAsJsonObject("type")!=null?root.getAsJsonObject("type").toString():null);
			frt.setAssigner(root.getAsJsonObject("assigner")!=null?root.getAsJsonObject("assigner").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")) {
			throw new IllegalStateException("PatientIdentifierMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientIdentifierMapper.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
