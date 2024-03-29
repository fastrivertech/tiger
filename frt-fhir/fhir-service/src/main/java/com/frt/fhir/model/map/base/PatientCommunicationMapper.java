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

public class PatientCommunicationMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientCommunicationMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientCommunicationMapper() {
	}

	@Override
	public PatientCommunicationMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientCommunicationMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientCommunicationMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientCommunication frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.r4.model.BackboneElement")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientCommunication")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_COMMUNICATION);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setPreferred(root.get("preferred")!=null?root.get("preferred").getAsBoolean():null);
			frt.setLanguage(root.getAsJsonObject("language")!=null?root.getAsJsonObject("language").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientCommunication")
				&& targetClz.getName().equals("org.hl7.fhir.r4.model.BackboneElement")) {
			throw new IllegalStateException("PatientCommunicationMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientCommunicationMapper.map() from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}

}
