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
package com.frt.fhir.rest;

/**
 * ResourcePath class
 * 
 * @author cqye
 */
public interface ResourcePath {
	 public static final String BASE_PATH = "/1.0";
	 public static final String TYPE_PATH = "/{type: [a-zA-Z]+}";
	 public static final String ID_PATH = "/{id: [a-zA-Z0-9]+}";
	 public static final String HISTORY_PATH = "/_history";	 	 
}
