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
 * FHIR Mime Type
 * @author jfu
 */
public interface MimeType {
	/* FHIR 4.0 mime type */
    public final static String APPLICATION_FHIR_JSON = "application/fhir+json";
    public final static String APPLICATION_FHIR_XML = "application/fhir+xml";  
    /* FHIR 3.x/2.x mime type also supports */
    public final static String APPLICATION_JSON = "application/json";
    public final static String APPLICATION_XML = "application/xml";      
}
