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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import org.hl7.fhir.exceptions.FHIRException;

import com.frt.dr.model.base.PatientExtension;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.map.MapperException;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * ResourceMapper class
 * 
 * @author jfu
 */
public class ResourceMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(ResourceMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public ResourceMapper() {
	}

	@Override
	public ResourceMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public ResourceMapper to(Class targetClz) {
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
			throw new IllegalArgumentException("ResourceMapper.map(source) expects instance of JsonElement as 'source', got source of type: "
					+ source.getClass().getCanonicalName());
		}
		
		JsonObject root = ((JsonElement) source).getAsJsonObject();
		
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Resource")
				&& targetClz.getName().equals("com.frt.dr.model.Resource")) {
			
			com.frt.dr.model.Resource frt = null;
			
			if (target == null) {
				frt = ResourceDictionary.getResourceInstance(RESOURCE);
			}

			if (target instanceof com.frt.dr.model.Resource) {
				frt = (com.frt.dr.model.Resource) target;
			} else {
				throw new IllegalArgumentException(
						"Expect instance of org.hl7.fhir.dstu3.model.Resource as 'target' of mapping, actual class:"
								+ target.getClass().getCanonicalName());
			}

			frt.setMeta(root.get("meta") != null?root.get("meta").toString():null);
			frt.setImplicitRules(root.get("implicitRules") != null?root.get("implicitRules").getAsString():null);
			frt.setLanguage(root.get("language") != null?root.get("language").getAsString():null);

			return (Object) frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.Resource")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Resource")) {
			throw new IllegalStateException("ResourceMapper.map(source, target) called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("ResourceMapper.map(source, target) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
	}
	
}
