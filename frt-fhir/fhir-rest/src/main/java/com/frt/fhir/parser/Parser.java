/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.parser;

import org.hl7.fhir.dstu3.model.Resource;

/**
 * Parser Interface
 * 
 * @author cqye
 */
public interface Parser {
	
	<R extends Resource> String serialize(R resource) ;
	<R extends Resource> R deserialize(Class<R> resourceClz, String message) ;
	
}
