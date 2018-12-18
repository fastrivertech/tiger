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

import java.util.Iterator;
import java.util.Set;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceMapper;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientHumanNameMapper implements ResourceMapper {
	private static Logger logger = Logger.getLog(PatientHumanNameMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

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
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientHumanName.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientHumanName frt = null;

		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientHumanName")) {

			frt = ResourceDictionary.getComplexInstance(PATIENT_HUMANNAME);
			frt.setPath("Patient.name");
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setUse(root.get("use")!=null?root.get("use").getAsString():null);
			frt.setFamily(root.get("family")!=null?root.get("family").getAsString():null);
			frt.setTxt(root.get("text")!=null?root.get("text").getAsString():null);

			if (System.getenv("DERBY_DB") != null && System.getenv("DERBY_DB").equalsIgnoreCase("YES")) {
				frt.setGiven(root.get("given")!=null?root.get("given").toString():null);
				frt.setPrefix(root.get("prefix")!=null?root.get("prefix").toString():null);
				frt.setSuffix(root.get("suffix")!=null?root.get("suffix").toString():null);
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientHumanName")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName")) {
			throw new IllegalStateException("PatientHumanName.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientHumanName.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
