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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.repository.Repository;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.transaction.TransactionService;

/**
 * BaseDao class
 * @author chaye
 */
public abstract class BaseDao<T,ID> implements Repository {
	
	protected TransactionService ts;

	public void initialize () {
		ts = TransactionService.getInstance();				
	}
	
    public abstract Optional<T> save(T entry) 
    	throws DaoException;

    public abstract Optional<T> findById(ID id) 
    	throws DaoException;
    
    public abstract Optional<List<T>> query(Class<T> resourceClazz, QueryCriteria criterias) 
    	throws DaoException;
    
    public abstract Optional<T> update(T entry) 
        throws DaoException;
  
}
