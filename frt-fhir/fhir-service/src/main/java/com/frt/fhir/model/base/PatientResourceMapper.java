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
import java.util.Set;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.exceptions.FHIRException;

import com.frt.dr.model.base.PatientAddress;
import com.frt.dr.model.base.PatientCodeableConcept;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientIdentifier;
import com.frt.dr.model.base.PatientReference;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceDictionary;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * PatientResourceMapper class
 * 
 * @author chaye
 */
public class PatientResourceMapper extends BaseMapper {
	private static Logger logger = Logger.getLog(PatientResourceMapper.class.getName());
	private static Localization localizer = Localization.getInstance();

	private Class sourceClz;
	private Class targetClz;

	public PatientResourceMapper() {
	}

	@Override
	public PatientResourceMapper from(Class sourceClz) {
		this.sourceClz = sourceClz;
		return this;
	}

	@Override
	public PatientResourceMapper to(Class targetClz) {
		this.targetClz = targetClz;
		return this;
	}

	@Override
	public Object map(Object source) throws MapperException {
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")
				&& targetClz.getName().equals("com.frt.dr.model.base.Patient")) {

			com.frt.dr.model.base.Patient frtPatient = new com.frt.dr.model.base.Patient();
			org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) source;
			// resource
			frtPatient.setPatientId(Long.valueOf(hapiPatient.getId().replace("Patient/", "")));

			if (source instanceof org.hl7.fhir.dstu3.model.Patient) {
				String jp = this.parser.encodeResourceToString(hapiPatient);
				JsonElement el = gparser.parse(jp);
				JsonObject root = el.getAsJsonObject();
				Set<String> attributes = root.keySet();
				Iterator<String> it = attributes.iterator();
				JsonObject jobj = null;
				while (it.hasNext()) {
					String key = it.next();
					logger.debug(localizer.x("<n, v> paire - name=" + key));

					if (key.equals("id")) {
						frtPatient.setPatientId(root.get(key).getAsLong());
					}

					if (key.equals("text")) {
						// skip narrative for now - DomainResource attribute
						logger.debug(localizer.x("skip 'text' attribute (Narrative)"));
					}

					if (key.equals("identifier")) {
						// array of FHIR type Identifier
						if (root.getAsJsonArray(key) != null) {
							JsonArray l = root.getAsJsonArray(key);
							if (l != null) {
								List<PatientIdentifier> ids = frtPatient.getIdentifiers();
								Iterator<JsonElement> i = l.iterator();
								while (i.hasNext()) {
									PatientIdentifierMapper m = new PatientIdentifierMapper();
									m = m.from(org.hl7.fhir.dstu3.model.Identifier.class)
											.to(com.frt.dr.model.base.PatientIdentifier.class);
									JsonObject id = (JsonObject) i.next();
									PatientIdentifier pid = (PatientIdentifier) m.map(id);
									pid.setPatient(frtPatient);
									ids.add(pid);
								}
								frtPatient.setIdentifiers(ids);
							}
						} else {
							throw new MapperException("Patient.identifier (Identifier[]) expects value of array.");
						}
					}

					if (key.equals("active")) {
						frtPatient.setActive(root.get(key).getAsBoolean());
					}

					if (key.equals("name")) {
						// array of FHIR type HumanName
						if (root.getAsJsonArray(key) != null) {
							JsonArray l = root.getAsJsonArray(key);
							if (l != null) {
								List<PatientHumanName> nms = frtPatient.getNames();
								Iterator<JsonElement> i = l.iterator();
								while (i.hasNext()) {
									PatientHumanNameMapper m = new PatientHumanNameMapper();
									m = m.from(org.hl7.fhir.dstu3.model.HumanName.class)
											.to(com.frt.dr.model.base.PatientHumanName.class);
									JsonObject id = (JsonObject) i.next();
									PatientHumanName pnm = (PatientHumanName) m.map(id);
									pnm.setPatient(frtPatient);
									nms.add(pnm);
								}
								frtPatient.setNames(nms);
							}
						} else {
							throw new MapperException("Patient.name (HumanName[]) expects value of array.");
						}
					}

					if (key.equals("telecom")) {
						// array of FHIR type ContactPoint
						logger.debug(localizer.x("skip 'telecom' attribute (ContactPoint)"));
					}

					if (key.equals("gender")) {
						frtPatient.setGender(root.get(key).getAsString());
					}

					if (key.equals("birthDate")) {
						frtPatient.setBirthDate(Date.valueOf(root.get(key).getAsString()));
					}

					if (key.equals("deceasedBoolean")) {
						frtPatient.setDeceasedBoolean(root.get(key).getAsBoolean());
					}

					if (key.equals("deceasedDateTime")) {
						frtPatient.setDeceasedDateTime(
								new Timestamp(Date.valueOf(root.get(key).getAsString()).getTime()));
					}

					if (key.equals("address")) {
						// array of FHIR type Address
						if (root.getAsJsonArray(key) != null) {
							JsonArray l = root.getAsJsonArray(key);
							if (l != null) {
								List<PatientAddress> addrs = frtPatient.getAddresses();
								Iterator<JsonElement> i = l.iterator();
								while (i.hasNext()) {
									PatientAddressMapper m = new PatientAddressMapper();
									m = m.from(org.hl7.fhir.dstu3.model.Address.class)
											.to(com.frt.dr.model.base.PatientAddress.class);
									JsonObject e = (JsonObject) i.next();
									PatientAddress t = (PatientAddress) m.map(e);
									t.setPatient(frtPatient);
									addrs.add(t);
								}
								frtPatient.setAddresses(addrs);
							}
						} else {
							throw new MapperException("Patient.address (Address[]) expects value of array.");
						}
					}

					if (key.equals("maritalStatus")) {
						// Patient attribute maritalStatus 0..1 CodeableConcept
						if ((jobj = root.getAsJsonObject(key)) != null) {
							PatientCodeableConceptMapper m = new PatientCodeableConceptMapper();
							m = m.from(org.hl7.fhir.dstu3.model.CodeableConcept.class)
									.to(com.frt.dr.model.base.PatientCodeableConcept.class);
							PatientCodeableConcept ms = (PatientCodeableConcept) m.map(jobj);
							ms.setPatient(frtPatient);
							frtPatient.setMaritalStatus(ms);
						}
					}

					if (key.equals("multipleBirthBoolean")) {
						frtPatient.setMultipleBirthBoolean(Boolean.valueOf(root.get(key).toString()));
					}

					if (key.equals("multipleBirthInteger")) {
						frtPatient.setMultipleBirthInteger(Integer.valueOf(root.get(key).toString()));
					}

					if (key.equals("contact")) {
						logger.debug(localizer.x("skip 'contact' attribute (BackboneElement) for now."));
					}

					if (key.equals("managingOrganization")) {
						if ((jobj = root.getAsJsonObject(key)) != null) {
							PatientReferenceMapper m = new PatientReferenceMapper();
							m = m.from(org.hl7.fhir.dstu3.model.Reference.class)
									.to(com.frt.dr.model.base.PatientReference.class);
							PatientReference pr = (PatientReference) m.map(jobj);
							pr.setPatient(frtPatient);
							frtPatient.setManagingOrganization(pr);;
						}
					}
				}
			}
			return (Object) frtPatient;
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			if (!(source instanceof com.frt.dr.model.base.Patient)) {
				throw new IllegalStateException("Patient.map() called source=" + sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
			}
			com.frt.dr.model.base.Patient frtPatient = (com.frt.dr.model.base.Patient) source;
			org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) this.parser
					.parseResource(BaseMapper.resourceToJson(frtPatient));
			return hapiPatient;
		} else {
			throw new MapperException(
					"Patient.map(source) from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}

}
