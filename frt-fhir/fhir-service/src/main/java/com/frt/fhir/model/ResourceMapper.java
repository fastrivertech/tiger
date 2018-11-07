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

import com.frt.fhir.model.MapperException;

/**
 * ResourceMapper Interface
 * @author chaye
 */
public interface ResourceMapper {
	
	ResourceMapper from(Class source);
	
	ResourceMapper to(Class target);
	
	Object map(Object source) throws MapperException;
	
}
