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
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import com.frt.dr.model.DomainResource;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.transaction.TransactionService;
import com.frt.dr.dao.DaoFactory;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;

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
	public <R extends DomainResource> R read(Class<?> resourceClazz, String id) 
		throws RepositoryServiceException {
		try {
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();			
		    TransactionService.getInstance().setEntityManager(em);
		    
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
			Optional<R> resource = dao.findById(id);
			if (resource.isPresent()) {
				return resource.get();
			}
			else {
				return null;
			}
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
		
	@Override
	public <R extends DomainResource> List<R> query(Class<?> resourceClazz, QueryCriteria criterias)
		throws RepositoryServiceException {
		try {
		    EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();			
		    TransactionService.getInstance().setEntityManager(em);
			
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);
			Optional<List<R>> resources = dao.query(resourceClazz, criterias);
			if (resources.isPresent()) {
				return resources.get();
			}
			else {
				return null;
			}
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
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);						
			Optional<R> created = dao.save(resource);
			ts.commit();
			return created.get();
		} catch (DaoException dex) {
			ts.rollback();
			throw new RepositoryServiceException(dex); 
		}
	}

	@Override
	public <R extends DomainResource> void update(java.lang.Class<?> resourceClazz, String id, R resource)
		throws RepositoryServiceException {
		// ToDo:
	}
	
}
