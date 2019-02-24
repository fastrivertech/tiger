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
	 // removed the space between type and the regex - because spring boot path var matching does not like the space
	 // - will result in 404 http return when retrieve patient with id ../Patient/10000
	 public static final String TYPE_PATH = "/{type:[a-ln-zA-Z]+}";
	 // removed the space between id: and the regex - same reason as in case of TYPE_PATH 
	 public static final String ID_PATH = "/{id:[A-Za-z0-9\\-\\.]+}";
	 public static final String VID_PATH = "/{vid:[A-Za-z0-9\\-\\.]+}";	 
	 public static final String HISTORY_PATH = "/_history";	 	
	 public static final String METADATA_PATH = "/metadata";	 	
	 public static final String PATIENT_PATH = "/Patient";	 	
	 public static final String MPI_POST_OPERATION_PATH = "/${operation:(match)|(search)}";	 	
	 public static final String MPI_PUT_OPERATION_PATH = "/${operation:.(merge)|(unmerge)|(link)|(unlink)}";	 	
}
