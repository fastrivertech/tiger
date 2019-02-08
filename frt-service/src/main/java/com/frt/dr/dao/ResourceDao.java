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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.ws.rs.core.MultivaluedMap;
import com.frt.dr.model.Resource;
import com.frt.dr.model.ResourceComplexType;
import com.frt.dr.service.query.CompositeParameter;
import com.frt.dr.service.query.GroupParameter;
import com.frt.dr.service.query.ResourceQuery;
import com.frt.dr.service.query.ResourceQueryBuilder;
import com.frt.dr.service.query.SearchParameter;
import com.frt.dr.service.query.SearchParameterRegistry;
import com.frt.dr.service.query.ResourceQueryUtils;
import com.frt.dr.service.query.QueryCriteria;
import com.frt.dr.transaction.TransactionService;

/**
 * ResourceDao class
 * @author jfu
 */
public class ResourceDao extends BaseDao<Resource, String> {
			
	public ResourceDao() {
		super.initialize();		
	}
	
	@Override
	public Optional<Resource> save(Resource entry) 
		throws DaoException {
		try {
			ts.getEntityManager().persist(entry);
			return Optional.of(entry);			
		} catch (IllegalStateException | 
				 RollbackException ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	public Optional<Resource> findById(String id) 
		throws DaoException {
		try {
			Query query = ts.getEntityManager().createNamedQuery("getResourceById");
			query.setParameter("id", id);
			List<Resource> resources = (List<Resource>) query.getResultList();
			Optional<Resource> resource = Optional.empty();
			if (resources.size() > 0) {
				resource = Optional.ofNullable(resources.get(0));
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

	@Override
	public Optional<List<Resource>> query(Class<Resource> resourceClazz, QueryCriteria criterias) 
		throws DaoException {
		try {
			Map<Class<?>, List<CompositeParameter>> parameters = ResourceQueryUtils.processParameters(criterias.getParams());	
			ResourceQueryBuilder<Resource> rb = ResourceQueryBuilder.createBuilder(ts.getEntityManager(), resourceClazz, parameters);
			ResourceQuery<Resource> rq = rb.createQuery();
			rq.prepareQuery();
			return rq.doQuery();
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
	
	@Override
	public Optional<Resource> update(Resource entry) 
		throws DaoException {
		try {
			ts.getEntityManager().merge(entry);
			return Optional.of(entry);			
		} catch (IllegalStateException | 
				 RollbackException ex) {
			throw new DaoException(ex);
		}
	}
	
}
