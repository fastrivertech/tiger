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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.frt.dr.model.DomainResource;
import com.frt.dr.dao.DaoFactory;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;

/**
 * RepositoryServiceImpl class
 * @author chaye
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
	
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public void setDataSource(DataSource dataSource) {
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Override
	public <R extends DomainResource> R read(Class<R> resourceClazz, Long id) 
		throws RepositoryServiceException {
		try {
			BaseDao dao = DaoFactory.getInstance().createResourceDao(resourceClazz);
			
			R resource = null;
			return resource;
		} catch (DaoException dex) {
			throw new RepositoryServiceException(dex); 
		}
	}
		
	@Override
	public <R extends DomainResource> R save(Class<R> resourceClazz, R resource)
		   throws RepositoryServiceException {
		
		return resource;
	}
	
}
