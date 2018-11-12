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
import java.sql.SQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;
import com.frt.dr.model.base.Patient;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;
import com.frt.dr.dao.DaoFactory;
import com.frt.dr.model.base.PatientHumanName;

/**
 * PatientDao class
 * @author chaye
 */
@Transactional
@Repository
public class PatientDao extends BaseDao<Patient,Long> {
	private static final String SQL_INSERT = "INSERT INTO PATIENT (" +
											 "patient_id, " +
	 								  		 "active, " +
	 								  		 "gender )" +
	 								  		 "VALUES (?, ?, ?)";
	private static final String SQL_SELECT_BYID = "SELECT active, gender FROM PATIENT WHERE patient_id = ? ";
	
	public PatientDao() {	
	}
	
	@Override	
	public Optional<Patient> save(Patient patient) 
		throws DaoException {
		try {
			Object[] params = new Object[] {patient.getPatientId(), patient.getActive(), patient.getGender()};
			int[] types = new int[] {Types.BIGINT, Types.BOOLEAN, Types.VARCHAR};
			int row = this.jdbcTemplate.update(SQL_INSERT, params, types);			
			if (row > 0) {
				BaseDao dao = DaoFactory.getInstance().createResourceDao(PatientHumanName.class);
				dao.setJdbcTemplate(this.jdbcTemplate);
				patient.getNames().forEach(name->dao.save(name));
				return Optional.of(patient);
			} else {
				throw new DaoException("failed to persist patient resource");
			}									
		} catch (DataAccessException dex) {
			throw new DaoException(dex);
		}
	}
	 
	@Override
	public Optional<Patient> findById(Long id) 
		throws DaoException {
		try {
			RowMapper<Patient> rowMapper = new PatientRowMapper();
			Optional<Patient> patient = Optional.ofNullable(this.jdbcTemplate.queryForObject(SQL_SELECT_BYID, new Object[]{id}, rowMapper));
			BaseDao dao = DaoFactory.getInstance().createResourceDao(PatientHumanName.class);
			dao.setJdbcTemplate(this.jdbcTemplate);
			Optional<PatientHumanName> name = dao.findById(id);
			if (patient.isPresent()) {
				patient.get().getNames().add(name.get());
			}
			return patient;
		} catch (DataAccessException dex) {
			throw new DaoException(dex);			
		}
	}
}
