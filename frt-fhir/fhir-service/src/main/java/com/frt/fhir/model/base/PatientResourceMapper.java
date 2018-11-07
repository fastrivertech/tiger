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

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;
import com.frt.dr.model.base.Patient;

/**
 * PatientResourceMapper class
 * @author chaye
 */
public class PatientResourceMapper implements ResourceMapper {
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
	public Object map(Object source) 
		throws MapperException {
		// org.hl7.fhir.dstu3.model.Patient vs com.frt.dr.model.base.Patient
				
		return null;		
	}
	
	
}
