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
package com.frt.dr.service;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import com.frt.dr.model.DomainResource;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.transaction.TransactionHelper;
import com.frt.dr.transaction.TransactionService;
import com.frt.dr.transaction.model.PatientTransaction;
import com.frt.dr.transaction.model.Transaction;
import com.frt.dr.dao.DaoFactory;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;
import com.frt.dr.cache.CacheService;
import com.frt.dr.cache.NamedCache;

/**
 * RepositoryServiceImpl class
 * @author chaye
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
	
    private DataSource dataSource;
    private JpaTransactionManager jpaTransactionManager;
 
    public RepositoryServiceImpl() {	
    }
    
    @Autowired
    public void setDataSource(DataSource dataSource) {
    	this.dataSource = dataSource;
    }
	 
    @Autowired
    public void setJpaTransactionManager(JpaTransactionManager jpaTransactionManager) {
    	this.jpaTransactionManager = jpaTransactionManager;
    }
   
	@Override
	public <R extends DomainResource> Optional<R> read(Class<?> resourceClazz, String id) 
		throws RepositoryServiceException {
		try {
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();			
		    TransactionService.getInstance().setEntityManager(em);
		    
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
			Optional<R> resource = dao.findById(id);
			return resource;
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
		
	@Override
	public <R extends DomainResource> Optional<List<R>> query(Class<?> resourceClazz, QueryCriteria criterias)
		throws RepositoryServiceException {
		try {
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();			
		    TransactionService.getInstance().setEntityManager(em);
			
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);
			Optional<List<R>> resources = dao.query(resourceClazz, criterias);
			return resources;
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
	
	@Override
	public <R extends DomainResource> R save(Class<?> resourceClazz, R resource)
		throws RepositoryServiceException {
		TransactionService ts  = TransactionService.getInstance();
		try {			
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();
			ts.setEntityManager(em);
			
		    ts.start();			
		  //BaseDao resourceDao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
		  //Optional<R> created = resourceDao.save(resource);					
			Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.C);
			BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
		    transaction.setResource(resource);			
			transactionDao.save(transaction);						
			ts.commit();
			return resource;
			
		} catch (DaoException dex) {
			ts.rollback();
			throw new RepositoryServiceException(dex); 
		}
	}

	@Override
	public <R extends DomainResource> R update(java.lang.Class<?> resourceClazz, String id, R resource)
		throws RepositoryServiceException {
		TransactionService ts  = TransactionService.getInstance();
		try {			
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();
			ts.setEntityManager(em);	
			 ts.start();		
			 BaseDao resourceDao = DaoFactory.getInstance().createResourceDao(resourceClazz);	
			 Optional<R> found = resourceDao.findById(id);	
			 
			 CacheService.getInstance().createCache();
			 
			 if (found.isPresent()) {
				 R changed = found.get();				 
				 // ToDo: 1) delta: resource and found.get()  2) copy resource to found.get()
				 String meta = TransactionHelper.updateMeta(changed.getMeta());
				 changed.setMeta(meta);
				 resourceDao.update(changed);
				 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.U);
				 transaction.setMeta(meta);
				 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
				 transaction.setResource(changed);			
			     transactionDao.save(transaction);		
			     
			     Optional<NamedCache> cache = CacheService.getInstance().getCache();
			     if (cache.isPresent()) {
			    	 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.U.name());
			     }
			     
			 } else {
				 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.C);
				 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
				 transaction.setResource(resource);			
				 transactionDao.save(transaction);	
			
			     Optional<NamedCache> cache = CacheService.getInstance().getCache();
			     if (cache.isPresent()) {
			    	 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.C.name());
			     }
							 
			 }
			 ts.commit();
			 return resource;
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
	
	@Override
	public void delete(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		// ToDo:
	}	
	
	@Override
	public <R extends DomainResource> Optional<List<R>> history(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		Optional<List<R>> history = Optional.empty();		
		Optional<R> found = read(resourceClazz, id);
		if (found.isPresent()) {
			history = Optional.of(new ArrayList());
			history.get().add(found.get());
			BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);	
			Optional<List<Transaction>> transactions = transactionDao.findById(id); 
			//ToDo 
		}
		return history;
	}
}
