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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import com.frt.dr.model.Extension;
import com.frt.dr.model.DomainResource;

public interface RepositoryServiceHelper {

	@SuppressWarnings("unchecked")
	public static <D extends DomainResource, E extends Extension> void setResourceStatus(java.lang.Class<?> resourceClazz, 
																	 					 D resourceObject, String status) 
		throws RepositoryServiceException {
		try {
			java.lang.Class<?> resourceExtensionClazz = Class.forName(resourceClazz.getName() + "Extension");		
			List<E> extensions = resourceObject.getExtensions();
			E extension = (E)resourceExtensionClazz.newInstance();
			extension.setResource((D)resourceObject);
			extension.setPath("patient.status");
			extension.setValue(status);
			extension.setUrl("http://hl7.org/fhir/StructureDefinition/patient-status");
			extensions.add(extension);
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {			
		}
	}
	
}
