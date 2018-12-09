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
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
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
		// org.hl7.fhir.dstu3.model.HumanName vs com.frt.dr.model.base.PatientHumanName
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.CodeableConcept")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientCodeableConcept")) {

			com.frt.dr.model.base.PatientCodeableConcept frt = new com.frt.dr.model.base.PatientCodeableConcept();

			if (source instanceof JsonElement) {
				// source is JsonObject representing instance of FHIR composite type
				// CodeableConcept
				JsonObject root = ((JsonElement) source).getAsJsonObject();
				Set<String> attributes = root.keySet();
				Iterator<String> it = attributes.iterator();
				JsonObject jobj = null;
				while (it.hasNext()) {
					String key = it.next();
					logger.debug(localizer.x("Patient.CodeableConcept <n, v> paire - name=" + key));

					if (key.equals("code")) {
						frt.setCoding_code(root.get(key).getAsString());
					}

					if (key.equals("system")) {
						frt.setCoding_system(root.get(key).getAsString());
					}

					// if (key.equals("version")) {
					// if ((jobj = root.getAsJsonObject(key)) != null) {
					// frt.setCoding_version(SqlHelper.toClob(jobj.getAsString()));
					// }
					// }

					if (key.equals("display")) {
						frt.setCoding_display(root.get(key).getAsString());
					}

					if (key.equals("userSelected")) {
						frt.setCoding_userselected(root.get(key).getAsBoolean());
					}
				}
				frt.setPath("Patient.maritalStatus");
			} else {
			}
			return (Object) frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientCodeableConcept")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.CodeableConcept")) {
			org.hl7.fhir.dstu3.model.CodeableConcept hapi = new org.hl7.fhir.dstu3.model.CodeableConcept();

			if (source instanceof JsonElement) {
				// mapping done at Patient level, should never be here
			} else {
//				com.frt.dr.model.base.PatientCodeableConcept frt = (com.frt.dr.model.base.PatientCodeableConcept) source;
			}
			return (Object) hapi;
		} else {
			throw new MapperException(
					"map from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}

}
