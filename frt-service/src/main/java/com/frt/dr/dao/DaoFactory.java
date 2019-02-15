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

import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientExtension;
import com.frt.dr.dao.base.PatientExtensionDao;
import com.frt.dr.transaction.model.dao.PatientTransactionDao;
import com.frt.dr.transaction.model.dao.TransactionDao;

/**
 * DaoFactory class
 * @author chaye
 */
public class DaoFactory {
	
	private static DaoFactory instance = new DaoFactory();
	
	private DaoFactory() {	
	}
	
	public static DaoFactory getInstance() {
		return instance;
	}
	
	public <D extends BaseDao> D createResourceDao(Class<?> resourceClazz) 
		throws DaoException {
		if (resourceClazz.equals(Patient.class) ) {
			D resourceDao = (D)new ResourceDao();
			return resourceDao;
		} else if (resourceClazz.equals(PatientExtension.class)) {
			D resourceDao = (D)new PatientExtensionDao();		
			return resourceDao;			
		} else {
			throw new DaoException(resourceClazz.getName() + "Dao Not Implemented Yet");
		}
	}

	public <D extends TransactionDao> D createTransactionDao(Class<?> resourceClazz) 
			throws DaoException {
			if (resourceClazz.equals(Patient.class) ) {
				D transactionDao = (D)new PatientTransactionDao();
				return transactionDao;
			} else {
				throw new DaoException(resourceClazz.getName() + "Dao Not Implemented Yet");
			}
		}
	
}
