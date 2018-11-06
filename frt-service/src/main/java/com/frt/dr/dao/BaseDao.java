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

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.repository.Repository;

public abstract class BaseDao<T,ID> implements Repository {
	  
	protected JdbcTemplate jdbcTemplate;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public abstract T save(T entry) throws DaoException;

    public abstract Optional<T> findById(ID id) throws DaoException;
    
}
