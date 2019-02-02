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
import com.frt.fhir.model.ResourceDictionary;
import com.frt.fhir.model.ResourceDictionary.ResourcePair;
import com.frt.fhir.model.map.MapperException;
import com.frt.fhir.model.map.ResourceMapperInterface;
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
		if (sourceClz.getName().equals("org.hl7.fhir.dstu3.model.Patient") && 
			targetClz.getName().equals("com.frt.dr.model.base.Patient")) {

			com.frt.dr.model.base.Patient frtPatient = ResourceDictionary.getResourceInstance(PATIENT);
			org.hl7.fhir.dstu3.model.Patient hapiPatient = (org.hl7.fhir.dstu3.model.Patient) source;
			// resource
			frtPatient.setId(hapiPatient.getId());
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
				frtPatient.setId(root.get("id").getAsString());
			}
			if (hapiPatient.hasIdElement() &&
				hapiPatient.getIdElement().hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getIdElement().getExtension();
				addExtensions(frtPatient, extensions, "patient.id");				
			}

			// map super class : DomainResource
			DomainResourceMapper drm = ResourceDictionary.getMapper(DOMAINRESOURCE);
			ResourcePair pair = ResourceDictionary.get(DOMAINRESOURCE);
			drm = drm.from(pair.getFhir()).to(pair.getFrt());
			drm.map(root, frtPatient);

			// patient.identifier: array of FHIR complex data type Identifier
			mapComponent(frtPatient, root, frtPatient.getIdentifiers(), "identifier", "Patient.identifier", PATIENT_IDENTIFIER);

			// patient.active
			if (root.get("active") != null) {
				frtPatient.setActive(root.get("active").getAsBoolean());
			}
			if (hapiPatient.hasActiveElement() &&
				hapiPatient.getActiveElement().hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getActiveElement().getExtension();
				addExtensions(frtPatient, extensions, "patient.active");				
			}
							
			// patient.name: array of FHIR complex data type HumanName
			mapComponent(frtPatient, root, frtPatient.getNames(), "name", "Patient.name", PATIENT_HUMANNAME);

			// patient.telecom: array of FHIR complex data type ContactPoint
			mapComponent(frtPatient, root, frtPatient.getTelecoms(), "telecom", "Patient.telecom", PATIENT_CONTACTPOINT);
			
			// patient.gender
			frtPatient.setGender(root.get("gender") != null ? root.get("gender").getAsString() : null);
			if (hapiPatient.hasGenderElement() &&
				hapiPatient.getGenderElement().hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getGenderElement().getExtension();
				addExtensions(frtPatient, extensions, "patient.gender");				
			}
			
			// patient.birthDate
			frtPatient.setBirthDate(root.get("birthDate") != null ? Date.valueOf(root.get("birthDate").getAsString()) : null);
			if (hapiPatient.hasBirthDateElement() &&
				hapiPatient.getBirthDateElement().hasExtension()) {
				List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getBirthDateElement().getExtension();
				addExtensions(frtPatient, extensions, "patient.birthDate");				
			}
			
			// patient.deceased[x].deceasedBoolean
			frtPatient.setDeceasedBoolean(root.get("deceasedBoolean") != null ? root.get("deceasedBoolean").getAsBoolean() : null);
			try {
				if (hapiPatient.hasDeceasedBooleanType() &&
					hapiPatient.getDeceasedBooleanType().hasExtension()) {
					List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getDeceasedBooleanType().getExtension();
					addExtensions(frtPatient, extensions, "patient.deceasedBoolean");				
				}
			} catch (FHIRException ignore) {								
			}
			
			// patient.deceased[x].deceasedDateTime
			frtPatient.setDeceasedDateTime(root.get("deceasedDateTime") != null ? new Timestamp(Date.valueOf(root.get("deceasedDateTime").getAsString()).getTime()) : null);
			try {
				if (hapiPatient.hasDeceasedDateTimeType() &&
					hapiPatient.getDeceasedDateTimeType().hasExtension()) {
					List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getDeceasedDateTimeType().getExtension();
					addExtensions(frtPatient, extensions, "patient.deceasedDateTime");				
				}
			} catch (FHIRException ignore) {								
			}
			
			// patient.address: array of FHIR complex data type Address
			mapComponent(frtPatient, root, frtPatient.getAddresses(), "address", "Patient.address", PATIENT_ADDRESS);

			// patient.maritalStatus:  FHIR complex data type CodeableConcept
			frtPatient.setMaritalStatus(mapComponent(frtPatient, root, "maritalStatus", "Patient.maritalStatus", PATIENT_CODEABLECONCEPT));

			// patient.multipleBirth[x].multipleBirthBoolean
			frtPatient.setMultipleBirthBoolean(root.get("multipleBirthBoolean") != null ? Boolean.valueOf(root.get("multipleBirthBoolean").toString()) : null);
			try {
				if (hapiPatient.hasMultipleBirthBooleanType() &&
					hapiPatient.getMultipleBirthBooleanType().hasExtension()) {
					List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getMultipleBirthBooleanType().getExtension();
					addExtensions(frtPatient, extensions, "patient.multipleBirthBoolean");				
				}
			} catch (FHIRException ignore) {				
			}
			
			// patient.multipleBirth[x].multipleBirthInteger
			frtPatient.setMultipleBirthInteger(root.get("multipleBirthInteger") != null ? Integer.valueOf(root.get("multipleBirthInteger").toString()) : null);
			try {
				if (hapiPatient.hasMultipleBirthIntegerType() && 
					hapiPatient.getMultipleBirthIntegerType().hasExtension()) {
					List<org.hl7.fhir.dstu3.model.Extension> extensions = hapiPatient.getMultipleBirthIntegerType().getExtension();
					addExtensions(frtPatient, extensions, "patient.multipleBirthInteger");				
				}
			} catch (FHIRException ignore) {				
			}
		
			// patient.photo: array of FHIR complex data type Attachment
			mapComponent(frtPatient, root, frtPatient.getPhotos(), "photo", "Patient.photo", PATIENT_ATTACHMENT);

			// patient.contact: array of object of FHIR BackboneElement
			mapComponent(frtPatient, root, frtPatient.getContacts(), "contact", "Patient.contact", PATIENT_CONTACT);

			// patient.animal: 0..1 of BackboneElement
			frtPatient.setAnimal(mapComponent(frtPatient, root, "animal", "Patient.animal", PATIENT_ANIMAL));

			// patient.communication: array of FHIR BackboneElement
			mapComponent(frtPatient, root, frtPatient.getCommunications(), "communication", "Patient.communication", PATIENT_COMMUNICATION);

			// patient.generalPractitioner: array of FHIR complex data type Reference
			mapComponent(frtPatient, root, frtPatient.getGeneralPractitioners(), "generalPractitioner", "Patient.generalPractitioner", PATIENT_REFERENCE);

			// patient.managingOrganization: 0..1 of FHIR complex data type Reference
			frtPatient.setManagingOrganization(mapComponent(frtPatient, root, "managingOrganization", "Patient.managingOrganization", PATIENT_REFERENCE));

			// patient.link: array of BackboneElement
			mapComponent(frtPatient, root, frtPatient.getLinks(), "link", "Patient.link", PATIENT_LINK);

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

			getExtensions(hapiPatient, patientExtensions, "patient.mulitpleBirthInteger");												
			getExtensions(hapiPatient, patientExtensions, "patient.mulitpleBirthBoolean");									
			getExtensions(hapiPatient, patientExtensions, "patient.deceasedDateTime");						
			getExtensions(hapiPatient, patientExtensions, "patient.deceasedBoolean");			
			getExtensions(hapiPatient, patientExtensions, "patient.birthDate");
			getExtensions(hapiPatient, patientExtensions, "patient.gender");
			getExtensions(hapiPatient, patientExtensions, "patient.active");			
			getExtensions(hapiPatient, patientExtensions, "patient.id");			
			getExtensions(hapiPatient, patientExtensions, "patient");
			
			return hapiPatient;
		} else {
			throw new MapperException("PatientResourceMapper.map(source) from " + sourceClz.getName() + " to "
					+ targetClz.getName() + " Not Implemented Yet");
		}
	}

	@Override
	public Object map(Object source, Object target) throws MapperException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
