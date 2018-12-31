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
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
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
import com.frt.fhir.model.ResourceMapper;
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

			com.frt.dr.model.base.Patient frtPatient = ResourceDictionary.getResourceInstance(PATIENT);
			org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) source;
			
			// resource
			// frtPatient.setPatientId(hapiPatient.getId());

			// patient.domainresource.extension
			if (hapiPatient.hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getExtension();
				addExtensions(frtPatient, extensions, "patient");
			}
			
			String jp = this.parser.encodeResourceToString(hapiPatient);
			JsonElement el = gparser.parse(jp);
			JsonObject root = el.getAsJsonObject();
			
			// patient.domainresource.resource.id	
			if (root.get("id") != null) {
				frtPatient.setPatientId(root.get("id").getAsString());
			}
			
			// patient.identifier: array of FHIR complex data type Identifier
			if (root.getAsJsonArray("identifier") != null) {
				JsonArray l = root.getAsJsonArray("identifier");
				if (l != null) {
					List<PatientIdentifier> arr = frtPatient.getIdentifiers();
					Iterator<JsonElement> i = l.iterator();
					PatientIdentifierMapper m = ResourceDictionary.getMapper(PATIENT_IDENTIFIER);
					m = m.from(org.hl7.fhir.dstu3.model.Identifier.class)
							.to(com.frt.dr.model.base.PatientIdentifier.class);
					while (i.hasNext()) {
						JsonObject id = (JsonObject) i.next();
						PatientIdentifier t = (PatientIdentifier) m.map(id);
						t.setPath("Patient.identifier");
						t.setPatient(frtPatient);
						arr.add(t);
					}
					frtPatient.setIdentifiers(arr);
				}
			}

			// patient.active
			if (root.get("active") != null) {
				frtPatient.setActive(root.get("active").getAsBoolean());
			}
				
			// patient.name: array of FHIR complex data type HumanName
			if (root.getAsJsonArray("name") != null) {
				JsonArray l = root.getAsJsonArray("name");
				if (l != null) {
					List<PatientHumanName> arr = frtPatient.getNames();
					Iterator<JsonElement> i = l.iterator();
					PatientHumanNameMapper m = ResourceDictionary.getMapper(PATIENT_HUMANNAME);
					m = m.from(org.hl7.fhir.dstu3.model.HumanName.class)
							.to(com.frt.dr.model.base.PatientHumanName.class);
					while (i.hasNext()) {
						JsonObject e = (JsonObject) i.next();
						PatientHumanName t = (PatientHumanName) m.map(e);
						t.setPath("Patient.name");
						t.setPatient(frtPatient);
						arr.add(t);
					}
					frtPatient.setNames(arr);
				}
			}

			// patient.telecom: array of FHIR complex data type ContactPoint
			if (root.getAsJsonArray("telecom") != null) {
				JsonArray l = root.getAsJsonArray("telecom");
				if (l != null) {
					List<PatientContactPoint> arr = frtPatient.getTelecoms();
					Iterator<JsonElement> i = l.iterator();
					PatientContactPointMapper m = ResourceDictionary.getMapper(PATIENT_CONTACTPOINT);
					m = m.from(org.hl7.fhir.dstu3.model.ContactPoint.class)
							.to(com.frt.dr.model.base.PatientContactPoint.class);
					while (i.hasNext()) {
						JsonObject e = (JsonObject) i.next();
						PatientContactPoint t = (PatientContactPoint) m.map(e);
						t.setPath("Patient.telecom");
						t.setPatient(frtPatient);
						arr.add(t);
					}
					frtPatient.setTelecoms(arr);
				}
			}
			
			// patient.gender
			frtPatient.setGender(root.get("gender") != null ? root.get("gender").getAsString() : null);
			
			// patient.birthDate
			frtPatient.setBirthDate(root.get("birthDate") != null ? Date.valueOf(root.get("birthDate").getAsString()) : null);
			if (hapiPatient.getBirthDateElement().hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getBirthDateElement().getExtension();
				addExtensions(frtPatient, extensions, "patient.birthdate");				
			}
			
			// patient.deceased[x].deceasedBoolean
			frtPatient.setDeceasedBoolean(root.get("deceasedBoolean") != null ? root.get("deceasedBoolean").getAsBoolean() : null);
			
			// patient.deceased[x].deceasedDateTime
			frtPatient.setDeceasedDateTime(root.get("deceasedDateTime") != null ? new Timestamp(Date.valueOf(root.get("deceasedDateTime").getAsString()).getTime()) : null);
			
			// patient.address: array of FHIR complex data type Address
			if (root.getAsJsonArray("address") != null) {
				JsonArray l = root.getAsJsonArray("address");
				if (l != null) {
					List<PatientAddress> arr = frtPatient.getAddresses();
					Iterator<JsonElement> i = l.iterator();
					PatientAddressMapper m = ResourceDictionary.getMapper(PATIENT_ADDRESS);
					m = m.from(org.hl7.fhir.dstu3.model.Address.class).to(com.frt.dr.model.base.PatientAddress.class);
					while (i.hasNext()) {
						JsonObject e = (JsonObject) i.next();
						PatientAddress t = (PatientAddress) m.map(e);
						t.setPath("Patient.address");
						t.setPatient(frtPatient);
						arr.add(t);
					}
					frtPatient.setAddresses(arr);
				}
			}

			// patient.maritalStatus:  FHIR complex data type CodeableConcept
			if (root.getAsJsonObject("maritalStatus") != null) {
				PatientCodeableConceptMapper m = ResourceDictionary.getMapper(PATIENT_CODEABLECONCEPT);
				m = m.from(org.hl7.fhir.dstu3.model.CodeableConcept.class)
						.to(com.frt.dr.model.base.PatientCodeableConcept.class);
				PatientCodeableConcept ms = (PatientCodeableConcept) m.map(root.getAsJsonObject("maritalStatus"));
				ms.setPath("Patient.maritalStatus");
				ms.setPatient(frtPatient);
				frtPatient.setMaritalStatus(ms);
			}

			// patient.multipleBirth[x].multipleBirthBoolean
			frtPatient.setMultipleBirthBoolean(root.get("multipleBirthBoolean") != null ? Boolean.valueOf(root.get("multipleBirthBoolean").toString()) : null);
			
			// patient.multipleBirth[x].multipleBirthInteger
			frtPatient.setMultipleBirthInteger(root.get("multipleBirthInteger") != null ? Integer.valueOf(root.get("multipleBirthInteger").toString()) : null);
		
			// patient.photo: array of FHIR complex data type Attachment
			if (root.getAsJsonArray("photo") != null) {
				JsonArray l = root.getAsJsonArray("photo");
				List<PatientAttachment> arr = frtPatient.getPhotos();
				Iterator<JsonElement> i = l.iterator();
				PatientAttachmentMapper m = ResourceDictionary.getMapper(PATIENT_ATTACHMENT);
				m = m.from(org.hl7.fhir.dstu3.model.Attachment.class).to(com.frt.dr.model.base.PatientAttachment.class);
				while (i.hasNext()) {
					JsonObject e = (JsonObject) i.next();
					PatientAttachment t = (PatientAttachment) m.map(e);
					t.setPath("Patient.photo");
					t.setPatient(frtPatient);
					arr.add(t);
				}
				frtPatient.setPhotos(arr);
			}

			// patient.contact: array of object of FHIR BackboneElement
			if (root.getAsJsonArray("contact") != null) {
				JsonArray l = root.getAsJsonArray("contact");
				List<PatientContact> arr = frtPatient.getContacts();
				Iterator<JsonElement> i = l.iterator();
				PatientContactMapper m = ResourceDictionary.getMapper(PATIENT_CONTACT);
				m = m.from(org.hl7.fhir.dstu3.model.BackboneElement.class)
						.to(com.frt.dr.model.base.PatientContact.class);
				while (i.hasNext()) {
					JsonObject e = (JsonObject) i.next();
					PatientContact t = (PatientContact) m.map(e);
					t.setPatient(frtPatient);
					arr.add(t);
				}
				frtPatient.setContacts(arr);
			}

			// patient.animal: BackboneElement
			if (root.getAsJsonObject("animal") != null) {
				ResourceMapper m = ResourceDictionary.getMapper(PATIENT_ANIMAL);
				m = m.from(org.hl7.fhir.dstu3.model.BackboneElement.class)
						.to(com.frt.dr.model.base.PatientAnimal.class);
				com.frt.dr.model.base.PatientAnimal t = (com.frt.dr.model.base.PatientAnimal) m
						.map(root.getAsJsonObject("animal"));
				t.setPatient(frtPatient);
				frtPatient.setAnimal(t);
			}

			// patient.communication: array of FHIR BackboneElement
			if (root.getAsJsonArray("communication") != null) {
				JsonArray l = root.getAsJsonArray("communication");
				List<PatientCommunication> arr = frtPatient.getCommunications();
				Iterator<JsonElement> i = l.iterator();
				ResourceMapper m = ResourceDictionary.getMapper(PATIENT_COMMUNICATION);
				m = m.from(org.hl7.fhir.dstu3.model.BackboneElement.class)
						.to(com.frt.dr.model.base.PatientCommunication.class);
				while (i.hasNext()) {
					JsonObject e = (JsonObject) i.next();
					PatientCommunication t = (PatientCommunication) m.map(e);
					t.setPatient(frtPatient);
					arr.add(t);
				}
				frtPatient.setCommunications(arr);
			}

			// patient.generalPractitioner: array of FHIR complex data type Reference
			if (root.getAsJsonArray("generalPractitioner") != null) {
				JsonArray l = root.getAsJsonArray("generalPractitioner");
				List<PatientReference> arr = frtPatient.getGeneralPractitioners();
				Iterator<JsonElement> i = l.iterator();
				ResourceMapper m = ResourceDictionary.getMapper(PATIENT_REFERENCE);
				m = m.from(org.hl7.fhir.dstu3.model.Reference.class).to(com.frt.dr.model.base.PatientReference.class);
				while (i.hasNext()) {
					JsonObject e = (JsonObject) i.next();
					PatientReference t = (PatientReference) m.map(e);
					t.setPath("Patient.generalPractitioner");
					t.setPatient(frtPatient);
					arr.add(t);
				}
				frtPatient.setGeneralPractitioners(arr);
			}

			// patient.managingOrganization: array of FHIR complex data type Reference
			if (root.getAsJsonObject("managingOrganization") != null) {
				PatientReferenceMapper m = ResourceDictionary.getMapper(PATIENT_REFERENCE);
				m = m.from(org.hl7.fhir.dstu3.model.Reference.class).to(com.frt.dr.model.base.PatientReference.class);
				PatientReference pr = (PatientReference) m.map(root.getAsJsonObject("managingOrganization"));
				pr.setPath("Patient.managingOrganization");
				pr.setPatient(frtPatient);
				frtPatient.setManagingOrganization(pr);
			}

			// patient.link: array of BackboneElement
			if (root.getAsJsonArray("link") != null) {
				JsonArray l = root.getAsJsonArray("link");
				List<PatientLink> arr = frtPatient.getLinks();
				Iterator<JsonElement> i = l.iterator();
				ResourceMapper m = ResourceDictionary.getMapper(PATIENT_LINK);
				m = m.from(org.hl7.fhir.dstu3.model.BackboneElement.class).to(com.frt.dr.model.base.PatientLink.class);
				while (i.hasNext()) {
					JsonObject e = (JsonObject) i.next();
					PatientLink t = (PatientLink) m.map(e);
					t.setPatient(frtPatient);
					arr.add(t);
				}
				frtPatient.setLinks(arr);
			}

			return (Object) frtPatient;
			
		} else if (sourceClz.getName().equals("com.frt.dr.model.base.Patient")
				&& targetClz.getName().equals("org.hl7.fhir.dstu3.model.Patient")) {
			
			if (!(source instanceof com.frt.dr.model.base.Patient)) {
				throw new IllegalStateException("PatientResourceMapper.map() called source="
						+ sourceClz.getCanonicalName() + ", target=" + targetClz.getCanonicalName());
			}
			
			com.frt.dr.model.base.Patient frtPatient = (com.frt.dr.model.base.Patient) source;
			org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) this.parser
					.parseResource(BaseMapper.resourceToJson(frtPatient));
			
			List<PatientExtension> patientExtensions = frtPatient.getExtensions();
			getExtensions(hapiPatient, patientExtensions, "patient");
			
			return hapiPatient;
		} else {
			throw new MapperException("PatientResourceMapper.map(source) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
	}
	
	
	public void addExtensions(com.frt.dr.model.base.Patient frtPatient, 
							  List<org.hl7.fhir.dstu3.model.Extension> extensions,
							  String path) {
		
		List<PatientExtension> patientExtensions = frtPatient.getExtensions();
		extensions.forEach(extension->{
			PatientExtension patientExtension = new PatientExtension();
			patientExtension.setPatient(frtPatient);
			patientExtension.setPath(path);
			patientExtension.setUrl(extension.getUrl());
			if (extension.hasValue()) {
				patientExtension.setValue(extension.getValue().toString());
			}
			patientExtensions.add(patientExtension);
		});		
		
	}

	public void getExtensions(org.hl7.fhir.dstu3.model.Patient hapiPatient, 
							  List<PatientExtension> patientExtensions,
							  String path) {
		patientExtensions.forEach(patientExtension->{
			if (path.equalsIgnoreCase(patientExtension.getPath())) {
				org.hl7.fhir.dstu3.model.Extension extension = new org.hl7.fhir.dstu3.model.Extension();
				extension.setUrl(patientExtension.getUrl());
				extension.setValue(new StringType(patientExtension.getValue()));
				hapiPatient.addExtension(extension);	
			}
		});					
		
	}	
	
}
