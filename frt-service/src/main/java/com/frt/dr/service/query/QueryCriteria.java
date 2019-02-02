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
package com.frt.dr.service.query;

import java.io.Serializable;
import javax.ws.rs.core.MultivaluedMap;

/**
 * QueryCriteria class
 * @author cqye
 */
public class QueryCriteria implements Serializable {
	
	private MultivaluedMap params;
	
	public QueryCriteria() {
	}
	
	public MultivaluedMap getParams() {
		return this.params;
	}
	
	public void setParams(final MultivaluedMap params) {
		this.params = params;
	}
	
}
