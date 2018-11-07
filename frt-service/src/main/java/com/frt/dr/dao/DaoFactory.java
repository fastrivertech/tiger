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
package com.frt.dr.dao;

import com.frt.dr.model.DomainResource;
import com.frt.dr.model.base.Patient;

public class DaoFactory {
	
	private static DaoFactory instance = new DaoFactory();
	
	private DaoFactory() {	
	}
	
	public static DaoFactory getInstance() {
		return instance;
	}
	
	public <D extends BaseDao> D createResourceDao(Class<? extends DomainResource> resourceClazz) 
		throws DaoException {
		if (resourceClazz.equals(Patient.class) ) {
			D resourceDao = null;
			return resourceDao;
		} else {
			throw new DaoException(resourceClazz.getName() + "Dao Not Implemented Yet");
		}
	}
	
}
