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
	 public static final String TYPE_PATH = "/{type:[a-ln-zA-Z]+}";
	 public static final String ID_PATH = "/{id:[A-Za-z0-9\\-\\.]+}";
	 public static final String VID_PATH = "/{vid:[A-Za-z0-9\\-\\.]+}";	 
	 public static final String HISTORY_PATH = "/_history";	 	
	 public static final String METADATA_PATH = "/metadata";	
	 public static final String RESOURCE_PATH = "/{resource:[a-ln-zA-Z]+}";	 
	 public static final String OPERATION_PATH = "/${operation:[A-Za-z]+}";	
	 public static final String SEARCH_PATH = "/_search";	 		 
	 
	 // FHIR MPI operations
	 public static final String PATIENT_PATH = "/Patient";	 	
	 public static final String MPI_MATCH_PATH = "/$match";	
	 public static final String MPI_SEARCH_PATH = "/$search";	
	 public static final String MPI_MERGE_PATH = "/$merge";	
	 public static final String MPI_UNMERGE_PATH = "/$unmerge";	
	 public static final String MPI_LINK_PATH = "/$link";	
	 public static final String MPI_UNLINK_PATH = "/$unlink";		 
	 public static final String MPI_PD_PATH = "/$potential";
	 public static final String MPI_OPERATION_PATH = "/${operation:(macth)|(search)|(merge)|(unmerge)|(link)|(unlink)}";	 	
}
