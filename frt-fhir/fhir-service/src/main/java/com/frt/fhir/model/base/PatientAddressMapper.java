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

public class PatientAddressMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientAddressMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientAddressMapper() {
	}

	@Override
	public PatientAddressMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientAddressMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientAddress.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}

		com.frt.dr.model.base.PatientAddress frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Address")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientAddress")) {
			frt = new com.frt.dr.model.base.PatientAddress();
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			Set<String> attributes = root.keySet();
			Iterator<String> it = attributes.iterator();
			JsonObject jobj = null;
			frt.setPath("Patient.address");
			while (it.hasNext()) {
				String key = it.next();
				logger.debug(localizer.x("Patient.Address <n, v> paire - name=" + key));
				
				if (key.equals("city")) {
					frt.setCity(root.get(key).getAsString());
				}

				if (key.equals("district")) {
					frt.setDistrict(root.get(key).getAsString());
				}

				if (key.equals("state")) {
					frt.setState(root.get(key).getAsString());
				}

				if (key.equals("country")) {
					frt.setCountry(root.get(key).getAsString());
				}

				if (key.equals("line")) {
					// line is a json array
					if ((root.getAsJsonArray(key)) != null) {
						frt.setLine(root.getAsJsonArray(key).toString());
					}
				}

				if (key.equals("postalCode")) {
					frt.setPostalcode(root.get(key).getAsString());
				}

				if (key.equals("type")) {
					frt.setType(root.get(key).getAsString());
				}

				if (key.equals("use")) {
					frt.setUse(root.get(key).getAsString());
				}

				if (key.equals("txt")) {
					frt.setTxt(root.get(key).getAsString());
				}

				if (System.getenv("DERBY_DB")!=null&&System.getenv("DERBY_DB").equalsIgnoreCase("YES")) {
					if (key.equals("period")) {
						if ((jobj = root.getAsJsonObject(key)) != null) {
							frt.setPeriod(SqlHelper.toClob(jobj.toString()));
						}
					}
				}
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAddress")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Address")) {
			throw new IllegalStateException("PatientAddress.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientAddress.map() from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}

}
