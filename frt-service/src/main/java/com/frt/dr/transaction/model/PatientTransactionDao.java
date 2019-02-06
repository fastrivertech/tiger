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
package com.frt.dr.transaction.model;

import java.util.List;
import java.util.Optional;
import javax.persistence.TransactionRequiredException;
import javax.persistence.EntityExistsException;
import javax.persistence.Query;
import javax.persistence.PersistenceException;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;
import com.frt.dr.service.query.QueryCriteria;

/**
 * PatientTransactionDao clas
 * @author cqye
 *
 */
public class PatientTransactionDao extends BaseDao<PatientTransaction, String> {

	public PatientTransactionDao() {		
	}
	
	@Override
    public Optional<PatientTransaction> save(PatientTransaction entry) 
    	throws DaoException {
    	
    	try {
    		// a transaction started
    		em.persist(entry);
    		return Optional.of(entry);
    	} catch (EntityExistsException | 
    			 IllegalArgumentException | 
    			 TransactionRequiredException ex) {
    		throw new DaoException(ex);
    	}
		
    }

	@Override	
    public Optional<PatientTransaction> findById(String id) 
    	throws DaoException {
    	
		try {
			Query query = em.createNamedQuery("PatientTransaction.getPatientTransactionById");
			query.setParameter("id", id);
			List<PatientTransaction> transactions = (List<PatientTransaction>) query.getResultList();			
			if (transactions != null && 
				transactions.size() >0 ) {
				return Optional.ofNullable(transactions.get(0));
			} else {
				return Optional.empty();
			}
		} catch (PersistenceException ex) {
			throw new DaoException(ex);			
		}    	
    }
	
    public Optional<List<PatientTransaction>> findByResourceId(String id) 
        	throws DaoException {
        	
    		try {
    			Query query = em.createNamedQuery("PatientTransaction.getPatientTransactionByResourceId");
    			query.setParameter("id", id);
    			List<PatientTransaction> transactions = (List<PatientTransaction>) query.getResultList();			
    			if (transactions != null && 
    				transactions.size() >0 ) {
    				return Optional.ofNullable(transactions);
    			} else {
    				return Optional.empty();
    			}
    		} catch (PersistenceException ex) {
    			throw new DaoException(ex);			
    		}    	
        }
	
	@Override    
    public Optional<List<PatientTransaction>> query(Class<PatientTransaction> resourceClazz, QueryCriteria criterias) 
    	throws DaoException {
    	throw new DaoException("Not Implemented Yet");
    }
	
}
