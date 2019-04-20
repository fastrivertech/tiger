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
package com.frt.dr.service;

import java.util.Optional;
import java.util.List;
import com.frt.dr.model.Extension;
import com.google.gson.JsonObject;
import com.frt.dr.model.DomainResource;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientExtension;

public interface RepositoryServiceHelper {

	@SuppressWarnings("unchecked")
	public static <D extends DomainResource, E extends Extension> void setResourceStatus(java.lang.Class<?> resourceClazz, 
																	 					 D resourceObject, String status) 
		throws RepositoryServiceException {
		try {
			// need to be generic
			if (resourceObject instanceof Patient) {
				Optional<PatientExtension> extension = ((Patient)resourceObject).getExtension();
				if (extension.isPresent()) {
					extension.get().setValue(status);					
				} else {
					java.lang.Class<?> resourceExtensionClazz = Class.forName(resourceClazz.getName() + "Extension");		
					List<PatientExtension> exts = ((Patient)resourceObject).getExtensions();
					PatientExtension ext = (PatientExtension)resourceExtensionClazz.newInstance();
					ext.setResource((D)resourceObject);
					ext.setPath("patient.status");
					ext.setValue(status);
					ext.setUrl("http://hl7.org/fhir/StructureDefinition/patient-status");
					exts.add(ext);					
				}				
			}			
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <D extends DomainResource> void generateDefaultNarrative(D resource) {
		if (resource.getTxt() == null) {
			JsonObject text = new JsonObject();
			text.addProperty("status", "generated");
			text.addProperty("div", resource.toString());
			resource.setTxt(text.toString());
		}
	}
	
}
