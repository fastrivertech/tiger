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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.frt.dr.service.query.FieldParameter;
import com.frt.dr.service.query.GroupParameter;
import com.frt.dr.service.query.SearchParameter;

import org.springframework.data.repository.Repository;

/**
 * BaseDao class
 * @author chaye
 */
public abstract class BaseDao<T,ID> implements Repository {
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
    
    public abstract Optional<List<T>> query(Map<String, String> params) throws DaoException;
    
}
