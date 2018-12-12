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

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientIdentifierMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientIdentifierMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientIdentifierMapper() {
	}

	@Override
	public PatientIdentifierMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientIdentifierMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientIdentifier.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}
		com.frt.dr.model.base.PatientIdentifier frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")) {
			frt = new com.frt.dr.model.base.PatientIdentifier();
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			Set<String> attributes = root.keySet();
			Iterator<String> it = attributes.iterator();
			JsonObject jobj = null;
			while (it.hasNext()) {
				String key = it.next();
				logger.debug(localizer.x("Patient.Identifier <n, v> paire - name=" + key));

				frt.setPath("Patient.identifier");

				if (key.equals("use")) {
					frt.setUse(root.get(key).getAsString());
				}

				if (key.equals("value")) {
					frt.setValue(root.get(key).getAsString());
				}

				if (key.equals("system")) {
					frt.setSystem(root.get(key).getAsString());
				}

				if (System.getenv("DERBY_DB")!=null&&System.getenv("DERBY_DB").equalsIgnoreCase("YES")) {
									if (key.equals("period")) {
						if ((jobj = root.getAsJsonObject(key)) != null) {
							frt.setPeriod(SqlHelper.toClob(jobj.getAsString()));
						}
					}
					if (key.equals("type")) {
						if ((jobj = root.getAsJsonObject(key)) != null) {
							frt.setType(SqlHelper.toClob(jobj.getAsString()));
						}
					}
				}
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")) {
			throw new IllegalStateException("PatientIdentifier.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientIdentifier.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
