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

			frt = new com.frt.dr.model.base.PatientCodeableConcept();
			frt.setPath("Patient.maritalStatus");

			JsonObject root = ((JsonElement) source).getAsJsonObject();
			Set<String> attributes = root.keySet();
			Iterator<String> it = attributes.iterator();
			JsonObject jobj = null;
			while (it.hasNext()) {
				String key = it.next();
				logger.debug(localizer.x("Patient.CodeableConcept <n, v> paire - name=" + key));
				if (key.equals("text")) {
					frt.setTxt(root.get(key).getAsString());
				}
				if (key.equals("coding")) {
					List<String> codes = new ArrayList<String>();
					List<String> systems = new ArrayList<String>();
					List<String> versions = new ArrayList<String>();
					List<String> displays = new ArrayList<String>();
					List<String> userselects = new ArrayList<String>();
					// need to mangle coding[] into a row in PATIENT_CODEABLECONCEPT
					// say we have 3 codes in coding[]
					// all coding.code => [c1,c2,c3]
					// all coding.system => [s1,s2,s3]
					// all coding.version => [v1,v2,v3]
					// all coding.display => [d1,d2,d3]
					// all coding.userSelected => [u1,u2,u3]
					if (root.get(key) != null && root.get(key).isJsonArray()) {
						JsonArray codings = root.get(key).getAsJsonArray();
						JsonObject jo = null;
						for (int i = 0; i < codings.size(); i++) {
							JsonElement coding = codings.get(i);
							if (coding.isJsonObject()) {
								jo = coding.getAsJsonObject();
								codes.add(jo.get("code") != null ? jo.get("code").getAsString() : "");
								systems.add(jo.get("system") != null ? jo.get("system").getAsString() : "");
								versions.add(jo.get("version") != null ? jo.get("version").getAsString() : "");
								displays.add(jo.get("display") != null ? jo.get("display").getAsString() : "");
								userselects.add(
										jo.get("userSelected") != null ? jo.get("userSelected").getAsString() : "");
							} else {
								// malformed FHIR json codings
								throw new MapperException("Malformed coding[], array of code struct expected.");
							}
						}

						frt.setCoding_code(gconverter.toJson(codes));
						frt.setCoding_system(gconverter.toJson(systems));
						frt.setCoding_version(gconverter.toJson(versions));
						frt.setCoding_display(gconverter.toJson(displays));
						frt.setCoding_userselected(gconverter.toJson(userselects));
					} else {
						// malformed CodeableConcept json
					}
				}
			}
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
