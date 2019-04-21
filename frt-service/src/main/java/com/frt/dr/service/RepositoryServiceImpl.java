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
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import com.frt.dr.model.DomainResource;
import com.frt.dr.model.Extension;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.transaction.TransactionHelper;
import com.frt.dr.transaction.TransactionService;
import com.frt.dr.transaction.model.Meta;
import com.frt.dr.transaction.model.Transaction;
import com.frt.dr.update.ResourceUpdateManager;
import com.frt.dr.dao.DaoFactory;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.transaction.model.dao.TransactionDao;
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
    private ResourceUpdateManager resourceUpdateManager;
    
    public RepositoryServiceImpl() {	
    	resourceUpdateManager = new ResourceUpdateManager();
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
   
    private <R extends DomainResource> boolean checkStatus(R resource, Transaction.ActionCode status) {
    	boolean checked = false;
		List<Extension> extensions = resource.getExtensions();
		for (Extension extension : extensions) {
			if("patient.status".equals(extension.getPath())) {
				if (status.name().equals(extension.getValue())) {							
					checked = true;
					break;
				}
			}
		}	
    	return checked;
    }
    
	@Override
	public <R extends DomainResource> Optional<R> read(Class<?> resourceClazz, String id) 
		throws RepositoryServiceException {
		try {		    
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
			Optional<R> resource = dao.findById(id);
			if (resource.isPresent()) {
				boolean deleted = checkStatus(resource.get(), Transaction.ActionCode.D);
				if (deleted) {
				    Optional<NamedCache> cache = CacheService.getInstance().getCache();
				    if (cache.isPresent()) {
				    	 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.D.name());
				    }				
				    resource = Optional.empty();					
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
					boolean deleted = checkStatus(r, Transaction.ActionCode.D);
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
		  //Generate default narrative
		    RepositoryServiceHelper.generateDefaultNarrative(resource);
		    // create transaction log
			Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.C);
			BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);
			// create resource status extension
			RepositoryServiceHelper.setResourceStatus(resourceClazz, resource, Transaction.ActionCode.C.name());
			// create resource meta data
			resource.setMeta((new Meta()).toString());						
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
	@SuppressWarnings("unchecked")
	public <R extends DomainResource> R update(java.lang.Class<?> resourceClazz, String id, R resource)
		throws RepositoryServiceException {
		R updatedResource = resource;
		TransactionService ts  = TransactionService.getInstance();
		Optional<NamedCache> cache = CacheService.getInstance().getCache();
		try {			
			 ts.start();		
			 BaseDao resourceDao = DaoFactory.getInstance().createResourceDao(resourceClazz);	
			 Optional<R> found = resourceDao.findById(id);	
			 
			 if (found.isPresent()) {
				 R changed = found.get();			
				 
				 // compute changes
				 resourceUpdateManager.cleanChanges();
				 resourceUpdateManager.change(resourceClazz, resourceClazz, resource, changed);
				 Optional<String> changes = resourceUpdateManager.getChanges();

				 boolean deleted = checkStatus(changed, Transaction.ActionCode.D);
				 
				 if (changes.isPresent() || deleted) {
					 // save changes
					 String meta = changed.getMeta();
					 String changedMeta = TransactionHelper.updateMeta(meta);
					 changed.setMeta(changedMeta);
					 RepositoryServiceHelper.setResourceStatus(resourceClazz, changed, Transaction.ActionCode.U.name());
					 resourceDao.update(changed);
					 // generate default narrative
					 RepositoryServiceHelper.generateDefaultNarrative(changed);
					 // create transaction log
					 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.U);
					 transaction.setMeta(meta);
					 transaction.setDelta(changes.isPresent() ? changes.get() : "");
					 // save transaction log
					 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
					 transaction.setResource(changed);			
					 transactionDao.save(transaction);						     					
					 if (cache.isPresent()) {
						 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.U.name());
					 }
					 updatedResource = changed;
				 } else {
					 // no changes
					 if (cache.isPresent()) {
						 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.N.name());
					 }											 
					 updatedResource = changed;					 
				 }
			 } else {
				 // create resource
				 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.C);
				 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);
				// generate default narrative
				 RepositoryServiceHelper.generateDefaultNarrative(resource);
				 resource.setMeta((new Meta()).toString());
				 // create resource status extension 
				 RepositoryServiceHelper.setResourceStatus(resourceClazz, resource, Transaction.ActionCode.C.name());
				 transaction.setResource(resource);			
				 transactionDao.save(transaction);				
			     if (cache.isPresent()) {
			    	 cache.get().put(NamedCache.ACTION_CODE, Transaction.ActionCode.C.name());
			     }							 
			 }
			 ts.commit();
			 return updatedResource;
		} catch (DaoException dex) {
			ts.rollback();
			throw new RepositoryServiceException(dex); 
		} finally {
			resourceUpdateManager.cleanChanges();
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
				 if (extension.isPresent()) {
					 if(!Transaction.ActionCode.D.name().equals(extension.get().getValue())) {
						 // change the resource status extension to 'deleted'
						 extension.get().setValue(Transaction.ActionCode.D.name());
						 ts.getEntityManager().merge(extension.get());
						 // create the log transaction
						 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.D);
						 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
						 transaction.setResource(deleted);			
						 transactionDao.save(transaction);
					 } else {
						 // the resource has been deleted previously
						 found = Optional.empty();
					 }
				 } else {
					 // for a case that a resource does not have a status extension, 
					 // this is not valid case, need to fix when the resource gets created or updated 
					 // with the right status extension
					 // update the resource extension status 
					 RepositoryServiceHelper.setResourceStatus(resourceClazz, deleted, Transaction.ActionCode.D.name());
					 resourceDao.update(deleted);
					 // create the log transaction
					 Transaction transaction = TransactionHelper.createTransaction(Transaction.ActionCode.D);
					 BaseDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);			
					 transaction.setResource(deleted);			
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
	@SuppressWarnings("unchecked")
	public <R extends DomainResource> Optional<List<R>> history(java.lang.Class<?> resourceClazz, String id)
		throws RepositoryServiceException {
		try {
			Optional<List<R>> history = Optional.empty();		
			Optional<R> found = read(resourceClazz, id);
			if (found.isPresent()) {
				history = Optional.of(new ArrayList());			
				TransactionDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);	 
				Optional<List<Transaction>> transactions = transactionDao.findByResourceId(found.get().getResourceId()); 
				history.get().add(found.get());
				if (transactions.isPresent()) {
					for (Transaction transaction : transactions.get()) {
						if (transaction.getAction().equals("U")) {
							// clone a resource
							R versioned = (R)resourceClazz.newInstance();
							resourceUpdateManager.change(resourceClazz, resourceClazz, found.get(), versioned);
							resourceUpdateManager.cleanChanges();
							// apply changes
							String delta = transaction.getDelta();
							if (delta != null && !delta.isEmpty()) {
								List<String> changes = Arrays.asList(delta.split(Pattern.quote(ResourceUpdateManager.DELIMITER)));
								for (String change : changes) {
									String[] tokens = change.split("=");
									if (tokens != null && tokens.length == 2) {
										resourceUpdateManager.update(resourceClazz, tokens[0], versioned, tokens[1]);
									}
								}
							}
							// apply meta
							String meta = transaction.getMeta();
						    versioned.setMeta(meta);
							history.get().add(versioned);
							found = Optional.of(versioned);
						}
					}
				}			
			}
			return history;
	    } catch (IllegalAccessException | InstantiationException ex) {
			throw new RepositoryServiceException(ex);
		}
	}
	
	@Override
	public <R extends DomainResource> Optional<R> vRead(Class<?> resourceClazz, String id, String vid) 
		throws RepositoryServiceException {
		try {
			Optional<R> vidResource = Optional.empty();
			Optional<R> found = read(resourceClazz, id);
			if (found.isPresent()) {
				if (found.get().getMeta().contains("\"versionId\":\"" + vid + "\"")) {
					vidResource = found;
				} else {						
					TransactionDao transactionDao = DaoFactory.getInstance().createTransactionDao(resourceClazz);	
					Optional<List<Transaction>> transactions = transactionDao.findByResourceId(found.get().getResourceId());
					if (transactions.isPresent()) {
						boolean vidMatched = false;
						for (Transaction transaction : transactions.get()) {		
							if (transaction.getAction().equals("U")) {
								// clone a resource
								R versioned = (R)resourceClazz.newInstance();
								resourceUpdateManager.change(resourceClazz, resourceClazz, found.get(), versioned);
								resourceUpdateManager.cleanChanges();	
								// apply changes
								String delta = transaction.getDelta();
								List<String> changes = Arrays.asList(delta.split(Pattern.quote(ResourceUpdateManager.DELIMITER)));
								for (String change : changes) {
									String[] tokens = change.split("=");
									if (tokens != null && tokens.length == 2) {
										resourceUpdateManager.update(resourceClazz, tokens[0], versioned, tokens[1]);
									}
								}
								// apply meta
								String meta = transaction.getMeta();
								versioned.setMeta(meta);
								vidResource = Optional.of(versioned);
								if (meta.contains("\"versionId\":\"" + vid + "\"")) {
									vidMatched = true;
									break;
								}
							}
						} // end of for
						if (!vidMatched) {
							vidResource = Optional.empty();
						}						
					} // end of if
				} // end of else
			}
			return vidResource;
		} catch (IllegalAccessException | InstantiationException ex) {
			throw new RepositoryServiceException(ex);
		}	
	}	

}
