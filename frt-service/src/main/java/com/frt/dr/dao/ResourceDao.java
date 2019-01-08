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

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import com.frt.dr.model.Resource;

/**
 * ResourceDao class
 * @author jfu
 */
public class ResourceDao extends BaseDao<Resource,String> {

	@Override
	public Optional<Resource> save(Resource entry) throws DaoException {
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			em.persist(entry);
			transaction.commit();
			return Optional.of(entry);
		} catch (IllegalStateException |
				 RollbackException ex) {
			try {
				if (transaction != null) {
					transaction.rollback();
				}
			} catch (IllegalStateException | RollbackException ignore) {
			}
			throw new DaoException(ex);
		}
	}

	@Override
	public Optional<Resource> findById(String id) throws DaoException {
		try {
	   	    Query query = em.createNamedQuery("getResourceById");
            query.setParameter("id", id);
            List<Resource> resources = (List<Resource>) query.getResultList();          							
            Optional<Resource> resource = null;
            if (resources.size()>0) {
            	
            	resource = Optional.ofNullable(resources.get(0));
            }
            else {
            	resource = Optional.empty();
            }
			return resource;
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

}
