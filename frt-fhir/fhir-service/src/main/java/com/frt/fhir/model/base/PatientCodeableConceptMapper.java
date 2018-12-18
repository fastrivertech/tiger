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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientCodeableConceptMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientCodeableConceptMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientCodeableConceptMapper() {
	}

	@Override
	public PatientCodeableConceptMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientCodeableConceptMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientIdentifier.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientCodeableConcept frt = null;

		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.CodeableConcept")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientCodeableConcept")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_CODEABLECONCEPT);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setCoding(root.get("coding")!=null?root.get("coding").toString():null);
			frt.setTxt(root.get("text")!=null?root.get("text").getAsString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientCodeableConcept")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.CodeableConcept")) {
			throw new IllegalStateException("PatientCodeableConcept.map() called source=" + sourceClz.getCanonicalName()
					+ ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientCodeableConcept.map(source) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}

}
