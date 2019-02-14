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
package com.frt.dr.model.update;

/**
 * ResourceUpdateException class
 * @author cqye
 */
public class ResourceUpdateException extends RuntimeException {
	
	public ResourceUpdateException(String m) {
		super(m);
	}

	public ResourceUpdateException(Throwable t) {
		super(t);
	}	
}
