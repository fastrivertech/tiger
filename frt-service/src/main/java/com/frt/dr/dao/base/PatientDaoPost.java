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
package com.frt.dr.dao.base;

import java.util.List;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientExtension;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.dao.DaoException;

/**
 * PatientDaoPost Class
 * @author cqye
 *
 */
public class PatientDaoPost {
	
	public static final String PATIENT = "patient";
	public static final String PATIENT_ADDRESS = "patient.address";
	public static final String PATIENT_ANIMAL = "patient.animal";
	public static final String PATIENT_ATTACHMENT = "patient.attachment";
	public static final String PATIENT_CODEABLECONCEPT = "patient.codeableconcept";
	public static final String PATIENT_COMMUNICATION = "patient.communication";
	public static final String PATIENT_CONTACT = "patient.contact";
	public static final String PATIENT_CONTACTPOINT = "patient.contactpoint";
	public static final String PATIENT_HUMANNAME = "patient.humanname";
	public static final String PATIENT_IDENTIFIER = "patient.identifier";
	public static final String PATIENT_LINK = "patient.link";
	public static final String PATIENT_REFERENCE = "patient.reference";

	public static void processExtensions(Patient patient) {

		List<PatientExtension> extensions = patient.getExtensions();
		patient.setExtensions(null);
		
		extensions.forEach(extension->{
			switch (extension.getPath()) {
			case PATIENT:
				patient.getExtensions().add(extension);
				break;
			case PATIENT_ADDRESS:
				
				break;
			case PATIENT_ANIMAL:
				
				break;
			case PATIENT_ATTACHMENT:
				
				break;
			case PATIENT_CODEABLECONCEPT:
				
				break;
			case PATIENT_COMMUNICATION:
				
				break;
			case PATIENT_CONTACT:
				
				break;
			case PATIENT_CONTACTPOINT:
				
				break;
			case PATIENT_HUMANNAME:
				break;
			case PATIENT_IDENTIFIER:
				
				break;
			case PATIENT_LINK:
				break;
			case PATIENT_REFERENCE:
				
				break;
			default:
				throw new DaoException("unknown extension: " + extension.getPath());
			}
		});
		
	}
	
}
