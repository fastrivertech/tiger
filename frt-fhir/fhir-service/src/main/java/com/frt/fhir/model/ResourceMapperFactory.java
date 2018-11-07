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
package com.frt.fhir.model;

import org.hl7.fhir.dstu3.model.DomainResource;
import com.frt.fhir.model.base.PatientResourceMapper;

/**
 * ResourceMapperFactory class
 * @author chaye
 */
public class ResourceMapperFactory {
	
	private static ResourceMapperFactory instance = new ResourceMapperFactory();
	
	private ResourceMapperFactory() {		
	}
	
	public static ResourceMapperFactory getInstance() {
		return instance;
	}
	
	public <R extends DomainResource> ResourceMapper create(String type) 
		throws MapperException {
		if (type.equalsIgnoreCase("Patient")) {
			return new PatientResourceMapper();			
		} else {
			throw new MapperException(type + " resource mapper not implemented yet");
		}
	}
}
