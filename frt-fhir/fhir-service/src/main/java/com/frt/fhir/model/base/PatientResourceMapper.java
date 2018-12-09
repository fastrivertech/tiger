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
			// org.hl7.fhir.dstu3.model.Patient => com.frt.dr.model.base.Patient
			// hapi Patient ==> frt Patient
			try {
				com.frt.dr.model.base.Patient frtPatient = new com.frt.dr.model.base.Patient();
				org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) source;
				// resource
				frtPatient.setPatientId(Long.valueOf(hapiPatient.getId().replace("Patient/", "")));

				if (source instanceof org.hl7.fhir.dstu3.model.Patient) {
					String jp = this.parser.encodeResourceToString(hapiPatient);
					// com.frt.dr.model.base.Patient patient = g.fromJson(pt,
					// com.frt.dr.model.base.Patient.class);
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
							if (root.getAsJsonArray(key)!=null) {
								JsonArray l = root.getAsJsonArray(key);
								if (l != null) {
									List<PatientIdentifier> ids = frtPatient.getIdentifiers();
									Iterator<JsonElement> i = l.iterator();
									while (i.hasNext()) {
										PatientIdentifierMapper m = new PatientIdentifierMapper();
										m = m.from(org.hl7.fhir.dstu3.model.Identifier.class).to(com.frt.dr.model.base.PatientIdentifier.class);
										JsonObject id = (JsonObject) i.next();
										PatientIdentifier pid = (PatientIdentifier) m.map(id);
										pid.setPatient(frtPatient);;
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
							if (root.getAsJsonArray(key)!=null) {
								JsonArray l = root.getAsJsonArray(key);
								if (l != null) {
									List<PatientHumanName> nms = frtPatient.getNames();
									Iterator<JsonElement> i = l.iterator();
									while (i.hasNext()) {
										PatientHumanNameMapper m = new PatientHumanNameMapper();
										m = m.from(org.hl7.fhir.dstu3.model.HumanName.class).to(com.frt.dr.model.base.PatientHumanName.class);
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
							frtPatient.setDeceasedDateTime(new Timestamp(Date.valueOf(root.get(key).getAsString()).getTime()));
						}

						if (key.equals("address")) {
							// array of FHIR type Address
							if (root.getAsJsonArray(key)!=null) {
								JsonArray l = root.getAsJsonArray(key);
								if (l != null) {
									List<PatientAddress> addrs = frtPatient.getAddresses();
									Iterator<JsonElement> i = l.iterator();
									while (i.hasNext()) {
										PatientAddressMapper m = new PatientAddressMapper();
										m = m.from(org.hl7.fhir.dstu3.model.Address.class).to(com.frt.dr.model.base.PatientAddress.class);
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
								m = m.from(org.hl7.fhir.dstu3.model.CodeableConcept.class).to(com.frt.dr.model.base.PatientCodeableConcept.class);
								frtPatient.setMaritalStatus(((PatientCodeableConcept)m.map(jobj)));
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
							logger.debug(localizer
									.x("skip 'managingOrganization' attribute (Reference(Organization) for now."));
						}
					}
					// hapi -> json -> JsonObject -> traversal and populate frt
				} else {
					// domain resource
					// patient resource: active
					frtPatient.setActive(Boolean.valueOf(hapiPatient.getActive()));

					// patient resource: name
					// org.hl7.fhir.dstu3.model.HumanName => com.frt.dr.model.base.PatientHumanName
					List<org.hl7.fhir.dstu3.model.HumanName> names = hapiPatient.getName();
					ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");
					PatientHumanNameMapper humanNameMapper = new PatientHumanNameMapper();
					names.forEach(name -> {
						Object mappedName = humanNameMapper.from(resourcePair.getFhir()).to(resourcePair.getFrt())
								.map(name);
						((com.frt.dr.model.base.PatientHumanName) mappedName)
								.setPatient((com.frt.dr.model.base.Patient) frtPatient);
						frtPatient.getNames().add((com.frt.dr.model.base.PatientHumanName) mappedName);
					});

					// patient resource: identifier
					// org.hl7.fhir.dstu3.model.Identifier =>
					// com.frt.dr.model.base.PatientIdentifier
					List<org.hl7.fhir.dstu3.model.Identifier> identifiers = hapiPatient.getIdentifier();
					ResourceDictionary.ResourcePair resourcePair2 = ResourceDictionary.get("PATIENT_IDENTIFIER");
					PatientIdentifierMapper identifierMapper = new PatientIdentifierMapper();
					identifiers.forEach(identifier -> {
						Object mappedIdentifier = identifierMapper.from(resourcePair2.getFhir())
								.to(resourcePair2.getFrt()).map(identifier);
						((com.frt.dr.model.base.PatientIdentifier) mappedIdentifier)
								.setPatient((com.frt.dr.model.base.Patient) frtPatient);
						frtPatient.getIdentifiers().add((com.frt.dr.model.base.PatientIdentifier) mappedIdentifier);
					});

					// patient resource: address
					// org.hl7.fhir.dstu3.model.Address => com.frt.dr.model.base.PatientAddress
					List<org.hl7.fhir.dstu3.model.Address> addresses = hapiPatient.getAddress();
					ResourceDictionary.ResourcePair resourcePair3 = ResourceDictionary.get("PATIENT_ADDRESS");
					PatientAddressMapper addressMapper = new PatientAddressMapper();
					addresses.forEach(address -> {
						Object mappedAddress = addressMapper.from(resourcePair3.getFhir()).to(resourcePair3.getFrt())
								.map(address);
						((com.frt.dr.model.base.PatientAddress) mappedAddress)
								.setPatient((com.frt.dr.model.base.Patient) frtPatient);
						frtPatient.getAddresses().add((com.frt.dr.model.base.PatientAddress) mappedAddress);
					});

					// patient resource: telecom

					// patient resource: gender
					frtPatient.setGender(hapiPatient.getGender().toString());

					// patient resource: birthDate
					frtPatient.setBirthDate(hapiPatient.getBirthDate());

					// patient resource: deceased, per FHIR 3.0.1 spec - assume patient is alive
					// patient resource: deceasedBoolean
					if (hapiPatient.getDeceased() != null && hapiPatient.getDeceased() instanceof BooleanType) {
						frtPatient.setDeceasedBoolean(hapiPatient.getDeceasedBooleanType().booleanValue());
					} else if (hapiPatient.getDeceased() != null && hapiPatient.getDeceased() instanceof DateTimeType) {
						// patient resource: deceasedDateTime
						frtPatient.setDeceasedDateTime(
								new java.sql.Timestamp(hapiPatient.getDeceasedDateTimeType().getValue().getTime()));
					} else {
						frtPatient.setDeceasedBoolean(false);
						frtPatient.setDeceasedDateTime(null);
					}

					// patient resource: address

					// patient resource: maritalStatus

					// patient resource: multipleBirth, per FHIR 3.0.1 spec, assume not
					// multiplebirth
					// patient resource: multipleBirthBoolean
					// note, have to do check as below to prevent class casting exception, e.g. try
					// to cast hapi boolean to integer
					// when the MultipleBirth is a boolean
					// FRT MultipleBoolean and MultipleInteger can be:
					// (1) Boolean is NULL and Integer NOT NULL
					// (2) Boolean is NOT NULL and Integer is NULL
					// (3) Boolean is NULL and Integer is NULL ===> HAPI Patient.hasMultipleBirth()
					// must be set to false
					if (hapiPatient.hasMultipleBirth()) {
						if (hapiPatient.getMultipleBirth() instanceof BooleanType) {
							frtPatient
									.setMultipleBirthBoolean(hapiPatient.getMultipleBirthBooleanType().booleanValue());
							frtPatient.setMultipleBirthInteger(null);
						} else {
							// patient resource: multipleBirthInteger
							frtPatient.setMultipleBirthInteger(hapiPatient.getMultipleBirthIntegerType().getValue());
							frtPatient.setMultipleBirthBoolean(null);
						}
					} else {
						// the default
						frtPatient.setMultipleBirthBoolean(null);
						frtPatient.setMultipleBirthInteger(null);
					}

					// patient resource: photo

					// patient resource: contact

					// patient resource: animal

					// patient resource: communication

					// patient resource: generalPractitioner

					// patient resource: managingOrganization

					// patient resource: link
				}
				return (Object) frtPatient;
			} catch (FHIRException ex) {
				throw new MapperException(ex);
			}
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			// com.frt.dr.model.base.Patient => org.hl7.fhir.dstu3.model.Patient
			// frt Patient ==> hapi Patient
			org.hl7.fhir.dstu3.model.Patient hapiPatient = new org.hl7.fhir.dstu3.model.Patient();
			com.frt.dr.model.base.Patient frtPatient = (com.frt.dr.model.base.Patient) source;

			// frt.toString -> JsonObject -> Traversal and populate hapi
			if (source instanceof com.frt.dr.model.base.Patient) {
				// override Patient toString so that it is a json
				return (Object)this.parser.parseResource(frtPatient.toString());
			} else {
				// resource
				hapiPatient.setId(frtPatient.getPatientId().toString());

				// domain resource

				// patient resource: active
				hapiPatient.setActive(frtPatient.getActive());

				// patient resource: name
				// com.frt.dr.model.base.PatientHumanName => org.hl7.fhir.dstu3.model.HumanName
				List<com.frt.dr.model.base.PatientHumanName> names = frtPatient.getNames();
				ResourceDictionary.ResourcePair resourcePair = ResourceDictionary.get("PATIENT_HUMANNAME");
				PatientHumanNameMapper humanNameMapper = new PatientHumanNameMapper();
				names.forEach(name -> {
					Object mappedName = humanNameMapper.from(resourcePair.getFrt()).to(resourcePair.getFhir())
							.map(name);
					hapiPatient.getName().add((org.hl7.fhir.dstu3.model.HumanName) mappedName);
				});

				// patient resource: identifier
				// com.frt.dr.model.base.PatientIdentifier =>
				// org.hl7.fhir.dstu3.model.Identifier
				List<com.frt.dr.model.base.PatientIdentifier> identifiers = frtPatient.getIdentifiers();
				ResourceDictionary.ResourcePair resourcePair2 = ResourceDictionary.get("PATIENT_IDENTIFIER");
				PatientIdentifierMapper identifierMapper = new PatientIdentifierMapper();
				identifiers.forEach(identifier -> {
					Object mappedIdentifier = identifierMapper.from(resourcePair2.getFrt()).to(resourcePair2.getFhir())
							.map(identifier);
					hapiPatient.getIdentifier().add((org.hl7.fhir.dstu3.model.Identifier) mappedIdentifier);
				});

				// patient resource: address
				// com.frt.dr.model.base.PatientAddress => org.hl7.fhir.dstu3.model.Address
				List<com.frt.dr.model.base.PatientAddress> addresses = frtPatient.getAddresses();
				ResourceDictionary.ResourcePair resourcePair3 = ResourceDictionary.get("PATIENT_ADDRESS");
				PatientAddressMapper addressMapper = new PatientAddressMapper();
				addresses.forEach(address -> {
					Object mappedAddress = addressMapper.from(resourcePair3.getFrt()).to(resourcePair3.getFhir())
							.map(address);
					hapiPatient.getAddress().add((org.hl7.fhir.dstu3.model.Address) mappedAddress);
				});

				// patient resource: telecom

				// patient resource: gender
				hapiPatient.setGender(
						org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.valueOf(frtPatient.getGender()));

				// patient resource: birthDate
				hapiPatient.setBirthDate(frtPatient.getBirthDate());

				// patient resource: deceased, per FHIR 3.0.1 spec - assume patient is alive
				// patient resource: deceasedBoolean
				hapiPatient.setDeceased(new BooleanType(frtPatient.getDeceasedBoolean()));
				// patient resource: deceasedDateTime
				hapiPatient.setDeceased(new DateTimeType(frtPatient.getDeceasedDateTime()));

				// patient resource: address

				// patient resource: maritalStatus

				// patient resource: multipleBirth, per FHIR 3.0.1 spec, assume not
				// multiplebirth
				// patient resource: multipleBirthBoolean
				if (frtPatient.getMultipleBirthBoolean() != null) {
					hapiPatient.setMultipleBirth(new BooleanType(frtPatient.getMultipleBirthBoolean()));
				}
				// patient resource: multipleBirthInteger
				if (frtPatient.getMultipleBirthInteger() != null) {
					hapiPatient.setMultipleBirth(new IntegerType(frtPatient.getMultipleBirthInteger()));
				}

				// patient resource: photo

				// patient resource: contact

				// patient resource: animal

				// patient resource: communication

				// patient resource: generalPractitioner

				// patient resource: managingOrganization

				// patient resource: link
			}

			return (Object) hapiPatient;

		} else {
			throw new MapperException(
					"map from " + sourceClz.getName() + " to " + targetClz.getName() + " Not Implemented Yet");
		}
	}

}
