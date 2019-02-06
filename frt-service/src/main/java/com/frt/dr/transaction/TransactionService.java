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
package com.frt.dr.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 * TransactionService class
 * @author cqye
 */
public class TransactionService {

	@PersistenceContext
	private EntityManager em;
	private EntityTransaction transaction;
	
	public TransactionService() {		
	}
	
    public void setEntityManager(EntityManager em) {
    	this.em = em;
    }
	
	public void start()
		throws TransactionServiceException {
		try {
			if (transaction == null) {
				transaction = em.getTransaction();
				transaction.begin();
			} else {
				throw new TransactionServiceException("transaction is active");				
			}
		} catch (IllegalStateException ex) {
			throw new TransactionServiceException(ex);
		}
	}
	
	public void commit() 
		throws TransactionServiceException {
		try {
			if (transaction != null) {
				transaction.commit();
			} else {
				throw new TransactionServiceException("transaction is not started");
			}
		} catch (PersistenceException | IllegalStateException ex) {
			throw new TransactionServiceException(ex);
		} finally {
			transaction = null;
		}	
	}
	
	public void rollback() 
		throws TransactionServiceException {
		try {
			if (transaction != null) {
				transaction.rollback();
			} else {
				throw new TransactionServiceException("transaction is not started");				
			}
		} catch (PersistenceException | IllegalStateException ex) {
			throw new TransactionServiceException(ex);
		} finally {
			transaction = null;
		}
	}
	
}
