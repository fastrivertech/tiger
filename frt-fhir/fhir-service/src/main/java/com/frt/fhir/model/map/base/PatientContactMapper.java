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

public class PatientContactMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientContactMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientContactMapper() {
	}

	@Override
	public PatientContactMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientContactMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientContactMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientContact frt = null;

		if (sourceClz.getName().equals("org.hl7.fhir.r4.model.BackboneElement")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientContact")) {

			frt = ResourceDictionary.getComplexInstance(PATIENT_CONTACT);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setGender(root.get("gender")!=null?root.get("gender").getAsString():null);
			frt.setRelationship(root.get("relationship")!=null?root.get("relationship").toString():null);
			frt.setName(root.get("name")!=null?root.get("name").toString():null);
			frt.setTelecom(root.get("telecom")!=null?root.get("telecom").toString():null);
			frt.setAddress(root.get("address")!=null?root.get("address").toString():null);
			frt.setPeriod(root.get("period")!=null?root.get("period").toString():null);
			frt.setOrganization(root.get("organization")!=null?root.get("organization").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientContact")
				&& targetClz.getName().equals("org.hl7.fhir.r4.model.BackboneElement")) {
			throw new IllegalStateException("PatientContactMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientContactMapper.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
