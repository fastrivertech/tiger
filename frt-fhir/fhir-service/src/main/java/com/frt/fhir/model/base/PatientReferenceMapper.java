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
		// org.hl7.fhir.dstu3.model.HumanName vs com.frt.dr.model.base.PatientHumanName
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Reference")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientReference")) {

			com.frt.dr.model.base.PatientReference frt = new com.frt.dr.model.base.PatientReference();

			if (source instanceof JsonElement) {
				// source is JsonObject representing instance of FHIR composite type Reference
				JsonObject root = ((JsonElement)source).getAsJsonObject();
				Set<String> attributes = root.keySet();
				Iterator<String> it = attributes.iterator();
				JsonObject jobj = null;
				while (it.hasNext()) {
					String key = it.next();
					logger.debug(localizer.x("Patient.Reference <n, v> paire - name=" + key));

					if (key.equals("reference")) {
						frt.setReference(root.get(key).getAsString());
					}
//					if (key.equals("identifier")) {
//						if ((jobj = root.getAsJsonObject(key)) != null) {
//							frt.setIdentifier(SqlHelper.toClob(jobj.toString()));
//						}
//					}
				}
			} else {
//				org.hl7.fhir.dstu3.model.Reference hapi = (org.hl7.fhir.dstu3.model.Reference) source;
			}
			return (Object) frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientReference")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Reference")) {
			org.hl7.fhir.dstu3.model.Reference hapi = new org.hl7.fhir.dstu3.model.Reference();

			if (source instanceof JsonElement) {
				// mapping done at Patient level, should never be here
			} else {
//				com.frt.dr.model.base.PatientReference frt = (com.frt.dr.model.base.PatientReference) source;
			}
			return (Object) hapi;
		} else {
			throw new MapperException(
					"map from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}
}
