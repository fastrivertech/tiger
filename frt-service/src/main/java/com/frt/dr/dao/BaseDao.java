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
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.logging.*;
import org.springframework.data.repository.Repository;

/**
 * BaseDao class
 * @author chaye
 */
public abstract class BaseDao<T,ID> implements Repository {
	public static final String PARAM_PREFIX_EQ = "eq";
	//	 * eq	the value for the parameter in the resource is equal to the provided value	the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_NE = "ne";
	//	 * ne	the value for the parameter in the resource is not equal to the provided value	the range of the search value does not fully contain the range of the target value
	public static final String PARAM_PREFIX_GT = "gt";
	//	 * gt	the value for the parameter in the resource is greater than the provided value	the range above the search value intersects (i.e. overlaps) with the range of the target value
	public static final String PARAM_PREFIX_LT = "lt";
	//	 * lt	the value for the parameter in the resource is less than the provided value	the range below the search value intersects (i.e. overlaps) with the range of the target value
	public static final String PARAM_PREFIX_GE = "ge";
	//	 * ge	the value for the parameter in the resource is greater or equal to the provided value	the range above the search value intersects (i.e. overlaps) with the range of the target value, or the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_LE = "le";
	//	 * le	the value for the parameter in the resource is less or equal to the provided value	the range below the search value intersects (i.e. overlaps) with the range of the target value or the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_SA = "sa";
	//	 * sa	the value for the parameter in the resource starts after the provided value	the range of the search value does not overlap with the range of the target value, and the range above the search value contains the range of the target value
	public static final String PARAM_PREFIX_EB = "eb";
	//	 * eb	the value for the parameter in the resource ends before the provided value	the range of the search value does overlap not with the range of the target value, and the range below the search value contains the range of the target value
	public static final String PARAM_PREFIX_AP = "ap";
	//	 * ap	the value for the parameter in the resource is approximately the same to the provided value.
	  
	protected JdbcTemplate jdbcTemplate;
	
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    	this.jdbcTemplate = jdbcTemplate;
    }

	@PersistenceContext
	protected EntityManager em;
    
    public void setEntityManager(EntityManager em) {
    	this.em = em;
    }
	
    public abstract Optional<T> save(T entry) throws DaoException;

    public abstract Optional<T> findById(ID id) throws DaoException;
    
    public abstract Optional<List<T>> query(Map params) throws DaoException;
}
