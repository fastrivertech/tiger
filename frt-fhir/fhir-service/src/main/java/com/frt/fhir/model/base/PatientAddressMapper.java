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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.exceptions.FHIRException;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		// org.hl7.fhir.dstu3.model.Address vs com.frt.dr.model.base.PatientAddress
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Address")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientAddress")) {
			com.frt.dr.model.base.PatientAddress frt = new com.frt.dr.model.base.PatientAddress();

			if (source instanceof JsonElement) {
				// source is JsonObject representing instance of FHIR composite type Address
				JsonObject root = ((JsonElement)source).getAsJsonObject();
				Set<String> attributes = root.keySet();
				Iterator<String> it = attributes.iterator();
				JsonObject jobj = null;
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
					
//					if (key.equals("period")) {
//						if ((jobj = root.getAsJsonObject(key)) != null) {
//							frt.setPeriod(SqlHelper.toClob(jobj.getAsString()));
//						}
//					}
				}
				frt.setPath("Patient.address");
			} else {
				org.hl7.fhir.dstu3.model.Address hapi = (org.hl7.fhir.dstu3.model.Address) source;

				if (hapi.hasId()) {
					frt.setAddressId(Long.valueOf(hapi.getId()));
				}
				frt.setPath("Patient.address");
				frt.setUse(hapi.getUse().name());
				if (hapi.hasType()) {
					frt.setType(hapi.getType().toCode());
				}
				frt.setTxt(hapi.getText());
				if (hapi.hasLine()) {
					org.hl7.fhir.dstu3.model.Patient p = new org.hl7.fhir.dstu3.model.Patient();
					List<org.hl7.fhir.dstu3.model.Address> a = new ArrayList<org.hl7.fhir.dstu3.model.Address>();
					a.add(new org.hl7.fhir.dstu3.model.Address().setLine(hapi.getLine()));
					p.setAddress(a);
					this.parser.encodeResourceToString(p);
					String prefix = "{\"resourceType\":\"Patient\",";
					String json = null;
					frt.setLine(json);
				}
				frt.setCity(hapi.getCity());
				;
				frt.setDistrict(hapi.getDistrict());
				;
				frt.setState(hapi.getState());
				;
				frt.setPostalcode(hapi.getPostalCode());
				;
				frt.setCountry(hapi.getCountry());
				;

				// commented out for splice machine clob insert issue
				// frt.setPeriod(SqlHelper.toClob(serializeToJson(hapi.getPeriod(), "period")));
			}
			return (Object) frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAddress")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Address")) {
			org.hl7.fhir.dstu3.model.Address hapi = new org.hl7.fhir.dstu3.model.Address();
			com.frt.dr.model.base.PatientAddress frt = (com.frt.dr.model.base.PatientAddress) source;

			if (source instanceof JsonElement) {
				// mapping done at Patient level, should never be here
			} else {
				if (frt.getAddressId() != null) {
					hapi.setId(String.valueOf(frt.getAddressId()));
				}
				hapi.setUse(org.hl7.fhir.dstu3.model.Address.AddressUse.valueOf(frt.getUse()));
				if (frt.getType() != null) {
					try {
						hapi.setType(org.hl7.fhir.dstu3.model.Address.AddressType.fromCode(frt.getType()));
					} catch (FHIRException e) {
						e.printStackTrace();
						throw new MapperException("FHIRException when convert FRT PatientAddress to HAPI Address.", e);
					}
				}

				hapi.setText(frt.getTxt());
				hapi.setLine(this.getAddressLine(frt.getLine()));
				hapi.setCity(frt.getCity());
				hapi.setDistrict(frt.getDistrict());
				hapi.setState(frt.getState());
				hapi.setCountry(frt.getCountry());
				hapi.setPostalCode(frt.getPostalcode());
				// comment out for now
				// hapi.setPeriod(getPeriod(frt.getPeriod()));
			}
			return (Object) hapi;
		} else {
			throw new MapperException(
					"map from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}

}
