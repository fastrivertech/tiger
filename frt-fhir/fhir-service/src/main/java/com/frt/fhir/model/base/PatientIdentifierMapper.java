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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;

import com.frt.dr.SqlHelper;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;

public class PatientIdentifierMapper extends BaseMapper {

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
	public Object map(Object source) 
		throws MapperException {
		// org.hl7.fhir.dstu3.model.HumanName vs com.frt.dr.model.base.PatientHumanName
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier") &&
		    targetClz.getName().equals("com.frt.dr.model.base.PatientIdentifier")) {
			com.frt.dr.model.base.PatientIdentifier frt = new com.frt.dr.model.base.PatientIdentifier();
			org.hl7.fhir.dstu3.model.Identifier hapi = (org.hl7.fhir.dstu3.model.Identifier)source;

			if (hapi.hasId()) {
				frt.setIdentifierId(Long.valueOf(hapi.getId()));
			}
			frt.setPath("Patient.Identifier");
			frt.setUse(hapi.getUse().name());
			frt.setSystem(hapi.getSystem());
			frt.setValue(hapi.getValue());
			// commented out for splice machine clob insert issue
//			frt.setType(SqlHelper.toClob(serializeToJson(hapi.getType(), "type")));
//			frt.setPeriod(SqlHelper.toClob(serializeToJson(hapi.getPeriod(), "period")));
//			frt.setAssigner(SqlHelper.toClob(serializeToJson(hapi.getAssigner(), "assigner")));

			return (Object)frt;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.PatientIdentifier") &&
			       targetClz.getName().equals("org.hl7.fhir.dstu3.model.Identifier")) {
			org.hl7.fhir.dstu3.model.Identifier hapi = new org.hl7.fhir.dstu3.model.Identifier();
			com.frt.dr.model.base.PatientIdentifier frt = (com.frt.dr.model.base.PatientIdentifier)source;
			
			hapi.setId(String.valueOf(frt.getIdentifierId()));
			hapi.setSystem(frt.getSystem());
			hapi.setValue(frt.getValue());
			hapi.setUse(getIdentifierUse(frt.getUse()));
			// comment out for now
//			hapi.setType(getType(frt.getType()));
//			hapi.setPeriod(getPeriod(frt.getPeriod()));
//			hapi.setAssigner(getAssigner(frt.getAssigner()));
			
			return (Object)hapi;
		} else {
			throw new MapperException("map from " + sourceClz.getName() + 
								           " to " + targetClz.getName() + 
								           " Not Implemented Yet");
		}		
	}

	private org.hl7.fhir.dstu3.model.Reference getAssigner(String assigner) {
		// Patient.Identifier.assigner json string : e.g. "assigner": {"display": "Acme Healthcare"}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(assigner).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getAssigner();
	}

	private org.hl7.fhir.dstu3.model.Period getPeriod(String period) {
		// Patient.Identifier.period json string : e.g. "period": {"start": "2001-05-06"}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(period).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getPeriod();
	}

	private org.hl7.fhir.dstu3.model.CodeableConcept getType(String type) {
		// Patient.Identifier.type json string : "type": {"coding": [{"system": "http://hl7.org/fhir/v2/0203","code": "MR"}]}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(type).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getType();
	}

	private IdentifierUse getIdentifierUse(String use) {
		if (use==null||use.isEmpty()) {
			return IdentifierUse.NULL;
		} else {
			return IdentifierUse.valueOf(use.trim());
		}
	}

	/**
	 * 
	 * @param obj - the HAPI instance of a Composite Type e.g. Period, Reference, Type etc.
	 * @param fieldName - Patient.Identifier field name (json), e.g. "period", "assigner", "type", etc.
	 * @return the corresponding json string;
	 */
	private <T extends org.hl7.fhir.dstu3.model.Type> String serializeToJson(T obj, String fieldName) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append(fieldName).append(":{");
		if (obj instanceof org.hl7.fhir.dstu3.model.Reference) {
			// Identifier.assigner
			org.hl7.fhir.dstu3.model.Reference r = ((org.hl7.fhir.dstu3.model.Reference)obj);
			if (r.hasDisplay()) {
				sb.append("display:").append(r.getDisplay());
				first = false;
			}
			if (r.hasReference()) {
				if (first) {
					sb.append(",");
				}
				sb.append("reference:").append(r.getReference());
			}
//			if (r.hasIdentifier()) {
//				if (first) {
//					sb.append(",");
//				}
//				sb.append("identifier:").append(serializeToJson(r.getIdentifier(), "identifier"));
//			}
		}
		else if (obj instanceof org.hl7.fhir.dstu3.model.Period) {
			// Identifier.period
			org.hl7.fhir.dstu3.model.Period p = ((org.hl7.fhir.dstu3.model.Period)obj); 
			if (p.hasStart()) {
				sb.append("start:").append(p.getStart().toString());
			};
			if (p.hasEnd()) {
				if (p.hasStart()) {
					sb.append(",");
				}
				sb.append(p.getEnd().toString());
			}
		}
		else if (obj instanceof org.hl7.fhir.dstu3.model.CodeableConcept) {
			// Identifier.type
			org.hl7.fhir.dstu3.model.CodeableConcept c = ((org.hl7.fhir.dstu3.model.CodeableConcept)obj); 
			if (c.hasCoding()) {
				
			}
			if (c.hasText()) {
				
			}
		}
		else {
			throw new UnsupportedOperationException("Serialization of instance of type: " + obj.getClass().getCanonicalName() + " not supported yet.");
		}
		sb.append("}");
		return sb.toString();
	}

}
