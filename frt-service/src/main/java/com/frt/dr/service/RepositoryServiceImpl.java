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
import com.frt.dr.model.Resource;
import com.frt.dr.model.Extension;
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
	    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();    	
	    TransactionService.getInstance().setEntityManager(em);    	
    }
   
	@Override
	public <R extends DomainResource> Optional<R> read(Class<?> resourceClazz, String id) 
		throws RepositoryServiceException {
		try {		    
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
			Optional<R> resource = dao.findById(id);
			if (resource.isPresent()) {
				List<Extension> extensions = resource.get().getExtensions();
				for (Extension extension : extensions) {
					if("patient.status".equals(extension.getPath())) {
						if (Transaction.ActionCode.D.name().equals(extension.getValue())) {
							
						    Optional<NamedCache> cache = CacheService.getInstance().getCache();
						    if (cache.isPresent()) {
						    	 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.D.name());
						    }				
						    resource = Optional.empty();
							break;							
						}
					}
				}			     
			}			
			return resource;
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
		
	@Override
	public <R extends DomainResource> Optional<List<R>> query(Class<?> resourceClazz, QueryCriteria criterias)
		throws RepositoryServiceException {
		try {
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);
			Optional<List<R>> resources = dao.query(resourceClazz, criterias);
			List<R> undeleteds =  new ArrayList<>();
			if (resources.isPresent()) {
				for (R r : resources.get()) {
					List<Extension> extensions = r.getExtensions();
					boolean deleted = false;
					for (Extension extension : extensions) {						
						if("patient.status".equals(extension.getPath())) {
							if (Transaction.ActionCode.D.name().equals(extension.getValue())) {
								deleted = true;
								break;
							}
						}
					}
					if (!deleted) {
						undeleteds.add(r);
					}					
				}
			}
			return Optional.of(undeleteds);
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
	
	@Override
	public <R extends DomainResource> R save(Class<?> resourceClazz, R resource)
		throws RepositoryServiceException {
		TransactionService ts  = TransactionService.getInstance();
		try {			
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
			 ts.start();		
			 BaseDao resourceDao = DaoFactory.getInstance().createResourceDao(resourceClazz);	
			 Optional<R> found = resourceDao.findById(id);	
			 
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
			ts.rollback();
			throw new RepositoryServiceException(dex); 
		}
	}
	
	@Override
	public  <R extends DomainResource> Optional<R> delete(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		TransactionService ts  = TransactionService.getInstance();
		try {			
			 BaseDao resourceDao = DaoFactory.getInstance().createResourceDao(resourceClazz);	
			 Optional<R> found = resourceDao.findById(id);	
			 if (found.isPresent()) {
				 ts.start();		 
				 R deleted = found.get();
				 java.lang.Class<?> resourceExtensionClazz = Class.forName(resourceClazz.getName() + "Extension");
				 BaseDao resourceExtensionDao = DaoFactory.getInstance().createResourceDao(resourceExtensionClazz);	
				 Optional<Extension> extension = resourceExtensionDao.findById(deleted.getResourceId().toString());
				 if (extension.isPresent() &&
					 !Transaction.ActionCode.D.name().equals(extension.get().getValue())) {
					 // change the status to 'deleted'
					 extension.get().setValue(Transaction.ActionCode.D.name());
					 ts.getEntityManager().merge(extension.get());
					 // log transaction
					 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.C);
					 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
					 transaction.setResource(found.get());			
					 transactionDao.save(transaction);						 
				 }
				 ts.commit();			
				 return found;
			 } else {
				 throw new RepositoryServiceException("resource " + resourceClazz.getName() + " " +id + " not found");
			 }
			 
		} catch (DaoException | ClassNotFoundException dex) {
			ts.rollback();
			throw new RepositoryServiceException(dex); 
		}
		
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
