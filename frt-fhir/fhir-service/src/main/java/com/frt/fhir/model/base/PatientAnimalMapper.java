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

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientAnimalMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientAnimalMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientAnimalMapper() {
	}

	@Override
	public PatientAnimalMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientAnimalMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientAnimalMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientAnimal frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.BackboneElement")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientAnimal")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_ANIMAL);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setSpecies(root.getAsJsonObject("species")!=null?root.getAsJsonObject("species").toString():null);
			frt.setBreed(root.getAsJsonObject("breed")!=null?root.getAsJsonObject("breed").toString():null);
			frt.setGenderStatus(root.getAsJsonObject("genderstatus")!=null?root.getAsJsonObject("genderstatus").toString():null);
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAnimal")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.BackboneElement")) {
			throw new IllegalStateException("PatientAnimalMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientAnimalMapper.map() from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}

}
