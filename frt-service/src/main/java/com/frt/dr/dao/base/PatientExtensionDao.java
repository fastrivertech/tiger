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
package com.frt.dr.dao.base;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.model.Resource;
import com.frt.dr.model.base.PatientExtension;

/**
 * PatientExtensionDao class
 * 
 * @author chaye
 */
@Transactional
@Repository
public class PatientExtensionDao extends BaseDao<PatientExtension, String> {

	public PatientExtensionDao() {
		super.initialize();				
	}
	
	public Optional<PatientExtension> save(PatientExtension entry) 
		throws DaoException {
		throw new UnsupportedOperationException();
	}

	public Optional<PatientExtension> findById(String id) 
		throws DaoException {
		try {
			Query query = ts.getEntityManager().createNamedQuery("getPatientExtensionById");
			query.setParameter("resourceId", BigInteger.valueOf(Long.parseLong(id)));
			query.setParameter("path", "patient.status");			
			List<PatientExtension> extensions = (List<PatientExtension>) query.getResultList();
			Optional<PatientExtension> extension = Optional.empty();
			if (extensions.size() > 0) {
				extension = Optional.ofNullable(extensions.get(0));
			} 
			return extension;
		} catch (IllegalArgumentException | 
				 QueryTimeoutException | 
				 TransactionRequiredException | 
				 PessimisticLockException | 
				 LockTimeoutException ex) {
			throw new DaoException(ex);
		} catch (PersistenceException ex) {
			throw new DaoException(ex);
		}		
	}

	public Optional<List<PatientExtension>> query(Class<PatientExtension> resourceClazz, QueryCriteria criterias) 
		throws DaoException {
		throw new UnsupportedOperationException();
	}

	public Optional<PatientExtension> update(PatientExtension entry) 
		throws DaoException {
		throw new UnsupportedOperationException();
	}

}
