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

		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientHumanName")) {
			// org.hl7.fhir.dstu3.model.HumanName => com.frt.dr.model.base.PatientHumanName
			// hapi human name => frt human name

			com.frt.dr.model.base.PatientHumanName frtName = new com.frt.dr.model.base.PatientHumanName();

			if (source instanceof JsonElement) {
				// source is JsonObject representing instance of FHIR composite type Identifier
				JsonObject root = ((JsonElement) source).getAsJsonObject();
				Set<String> attributes = root.keySet();
				Iterator<String> it = attributes.iterator();
				JsonObject jobj = null;
				while (it.hasNext()) {
					String key = it.next();
					logger.debug(localizer.x("Patient.HumanName <n, v> paire - name=" + key));

					if (key.equals("use")) {
						frtName.setUse(root.get(key).getAsString());
					}

					if (key.equals("family")) {
						frtName.setFamily(root.get(key).getAsString());
					}

					// if (key.equals("given")) {
					// frtName.setGiven(SqlHelper.toClob(root.get(key).getAsString()));
					// }

					// if (key.equals("prefix")) {
					// frtName.setPrefix(SqlHelper.toClob(root.get(key).getAsString()));
					// }

					// if (key.equals("suffix")) {
					// frtName.setSuffix(SqlHelper.toClob(root.get(key).getAsString()));
					// }

					if (key.equals("text")) {
						frtName.setTxt(root.get(key).getAsString());
					}
				}
				frtName.setPath("Patient.name");
			} else {
				org.hl7.fhir.dstu3.model.HumanName hapiName = (org.hl7.fhir.dstu3.model.HumanName) source;

				// Element
				frtName.setHumannameId(Long.valueOf(hapiName.getId()));

				// HumanName element: use
				frtName.setUse(hapiName.getUse().name());

				// HumanName element: text
				frtName.setTxt(hapiName.getText());

				// HumanName element: family
				frtName.setFamily(hapiName.getFamily());

				// HumanName element: given

				// HumanName element: prefix

				// HumanName element: suffix

				// HumanName element: period
			}

			return (Object) frtName;

		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientHumanName")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.HumanName")) {
			// com.frt.dr.model.base.PatientHumanName => org.hl7.fhir.dstu3.model.HumanName
			// frt human name => hapi human name

			org.hl7.fhir.dstu3.model.HumanName hapiName = new org.hl7.fhir.dstu3.model.HumanName();

			if (source instanceof JsonElement) {
				// mapping done at Patient level, should never be here
			} else {
				com.frt.dr.model.base.PatientHumanName frtName = (com.frt.dr.model.base.PatientHumanName) source;

				// Element
				hapiName.setId(frtName.getHumannameId().toString());

				// HumanName element: use
				hapiName.setUse(org.hl7.fhir.dstu3.model.HumanName.NameUse.valueOf(frtName.getUse()));

				// HumanName element: family
				hapiName.setFamily(frtName.getFamily());

				// HumanName element: given

				// HumanName element: prefix

				// HumanName element: suffix

				// HumanName element: period
			}

			return (Object) hapiName;

		} else {
			throw new MapperException(
					"map from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}
}
