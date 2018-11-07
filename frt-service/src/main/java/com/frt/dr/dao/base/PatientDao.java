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
package com.frt.dr.dao.base;

import java.util.Optional;
import javax.sql.DataSource;
import java.sql.Types;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import com.frt.dr.model.base.Patient;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;

/**
 * PatientDao class
 * @author chaye
 */
@Transactional
@Repository
public class PatientDao extends BaseDao<Patient,Long> {
	private static final String SQL_INSERT = "INSERT INTO Patient (" +
	 								  		 "title, " +
	 								  		 "release_date, " +
	 								  		 "created) " +
	 								  		 "VALUES (?, ?, ?, ?, ?)";
	private static final String SQL_SELECT_BYID = "SELECT * FROM Patient WHERE patient_id = ? ";
	
	public PatientDao() {	
	}
	
	@Override	
	public Optional<Patient> save(Patient patient) 
		throws DaoException {
		Object[] params = new Object[] {};
		int[] types = new int[] {};
		int row = this.jdbcTemplate.update(SQL_INSERT, params, types);
		return null;
	}
	 
	@Override
	public Optional<Patient> findById(Long id) 
		throws DaoException {
		RowMapper<Patient> rowMapper = new PatientRowMapper();
		Optional<Patient> patient = Optional.ofNullable(this.jdbcTemplate.queryForObject(SQL_SELECT_BYID, new Object[]{id}, rowMapper));
		return patient;
	}
}
