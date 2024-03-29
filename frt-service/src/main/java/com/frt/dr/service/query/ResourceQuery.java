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

import java.util.List;
import java.util.Optional;
import com.frt.dr.model.Resource;

/**
 * ResourceQuery interface
 * @author jimfu
 * @param <T>
 */
public interface ResourceQuery<T extends Resource> {
	
	enum QUERY_STATES {
		CREATED, PREPARED, EXECUTED
	}
	
	public void prepareQuery();
	public Optional<List<Resource>> doQuery();
}
