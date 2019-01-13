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

import java.lang.reflect.InvocationTargetException;

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientReferenceMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientReferenceMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

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
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientReference.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}
		com.frt.dr.model.base.PatientReference frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Reference")
				&&targetClz.getName().equals("com.frt.dr.model.base.PatientReference")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_REFERENCE);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setReference(root.get("reference") != null ? root.get("reference").getAsString() : null);
			frt.setDisplay(root.get("display") != null ? root.get("display").getAsString() : null);
			if (System.getProperty("frt.persist.store.derby", "false").equalsIgnoreCase("TRUE")) {
				frt.setIdentifier(
						root.getAsJsonObject("identifier") != null ? root.getAsJsonObject("identifier").toString()
								: null);
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientReference")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Reference")) {
			throw new IllegalStateException("PatientReference.map() called source=" + sourceClz.getCanonicalName()
					+ ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientReference.map(source) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
