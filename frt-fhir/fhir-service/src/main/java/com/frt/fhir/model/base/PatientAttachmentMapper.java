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
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PatientAttachmentMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientAttachmentMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientAttachmentMapper() {
	}

	@Override
	public PatientAttachmentMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientAttachmentMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (!(source instanceof JsonElement)) {
			throw new IllegalArgumentException("PatientAttachmentMapper.map(source) expects JsonElement, got source of type: "
					+ source.getClass().getCanonicalName());
		}
		com.frt.dr.model.base.PatientAttachment frt = null;
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Attachment")
				&& targetClz.getName().equals("com.frt.dr.model.base.PatientAttachment")) {
			frt = ResourceDictionary.getComplexInstance(PATIENT_ATTACHMENT);
			JsonObject root = ((JsonElement) source).getAsJsonObject();
			frt.setPath("Patient.photo");
			frt.setContenttype(root.get("contentType")!=null?root.get("contentType").getAsString():null);
			frt.setLanguage(root.get("language")!=null?root.get("language").getAsString():null);
			frt.setUrl(root.get("url")!=null?root.get("url").getAsString():null);
			frt.setSize(root.get("size")!=null?root.get("size").getAsInt():null);
			frt.setTitle(root.get("title")!=null?root.get("title").getAsString():null);
			frt.setCreation(root.get("creation")!=null? new Timestamp(Date.valueOf((root.get("creation").getAsString())).getTime()):null);
			if (System.getenv("DERBY_DB")!=null&&System.getenv("DERBY_DB").equalsIgnoreCase("YES")) {
				// remove ENV var checking when splice machine resolves the JDBC insert LOB error
				frt.setData(root.get("data")!=null?root.get("data").getAsString():null);
				frt.setHash(root.get("hash")!=null?root.get("hash").getAsString():null);
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientAttachment")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Attachment")) {
			throw new IllegalStateException("PatientAttachmentMapper.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
		} else {
			throw new MapperException("PatientAttachmentMapper.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
		return (Object) frt;
	}
}
