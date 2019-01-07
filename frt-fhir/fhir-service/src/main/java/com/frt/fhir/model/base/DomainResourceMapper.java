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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;

import com.frt.dr.model.DomainResource;
import com.frt.dr.model.Resource;
import com.frt.dr.model.ResourceComplexType;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientExtension;
import com.frt.dr.model.base.PatientAddress;
import com.frt.dr.model.base.PatientAttachment;
import com.frt.dr.model.base.PatientCodeableConcept;
import com.frt.dr.model.base.PatientCommunication;
import com.frt.dr.model.base.PatientContact;
import com.frt.dr.model.base.PatientContactPoint;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientIdentifier;
import com.frt.dr.model.base.PatientLink;
import com.frt.dr.model.base.PatientReference;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceDictionary.ResourcePair;
import com.frt.fhir.model.ResourceMapperInterface;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * DomainResourceMapper class
 * 
 * @author jfu
 */
public class DomainResourceMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(DomainResourceMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public DomainResourceMapper() {
	}

	@Override
	public DomainResourceMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public DomainResourceMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		return map(source, null);
	}

	@Override
	public Object map(Object source, Object target) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("DomainResourceMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}
		JsonObject root = ((JsonElement) source).getAsJsonObject();
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.DomainResource")
				&& targetClz.getName().equals("com.frt.dr.model.DomainResource")) {
			com.frt.dr.model.DomainResource frt = null;
			if (target instanceof com.frt.dr.model.DomainResource) {
				frt = (com.frt.dr.model.DomainResource) target;
			} else {
				throw new IllegalArgumentException(
						"Expect instance of org.hl7.fhir.dstu3.model.DomainResource as 'source' of mapping, actual class:"
								+ source.getClass().getCanonicalName());
			}

			if (target == null) {
				frt = ResourceDictionary.getResourceInstance(DOMAINRESOURCE);
			}

			// map super class : Resource
			ResourceMapper drm = ResourceDictionary.getMapper(RESOURCE);
			ResourcePair pair = ResourceDictionary.get(RESOURCE);
			drm = drm.from(pair.getFhir()).to(pair.getFrt());
			drm.map(root, frt);
			// we already work around before come here, so skip it
			//frt.setContained(root.get("contained") != null?root.get("contained").toString():null);
			frt.setTxt(root.get("text") != null?root.get("text").toString():null);

			return (Object) frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.DomainResource")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.DomainResource")) {
			throw new IllegalStateException("DomainResourceMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("DomainResourceMapper.map(source) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
	}
}
