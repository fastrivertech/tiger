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

import java.sql.Types;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;
import com.frt.dr.model.base.PatientHumanName;

/**
 * PatientHumanNameDao class
 * 
 * @author chaye
 */
@Transactional
@Repository
public class PatientHumanNameDao extends BaseDao<PatientHumanName, Long> {
//	private static final String SQL_INSERT = "INSERT INTO PATIENT_HUMANNAME (" + "NEXT VALUE FOR PATIENT_HUMANNAME_SEQ, " + "patient_id, " + "use, " + "family)"
//			+ "VALUES (?, ?, ?, ?)";
	private static final String SQL_INSERT = "INSERT INTO PATIENT_HUMANNAME (humanname_id, patient_id, use, family)"
			+ "VALUES (NEXT VALUE FOR PATIENT_HUMANNAME_SEQ, ?, ?, ?)";
	private static final String SQL_SELECT_BYID = "SELECT humanname_id, patient_id, path, use, txt, family, given, prefix, suffix, period FROM PATIENT_HUMANNAME WHERE patient_id = ? ";
		
	public PatientHumanNameDao() {
	}

	@Override
	public Optional<PatientHumanName> save(PatientHumanName name) 
	    throws DaoException {
		try {
			Object[] params = new Object[] {name.getPatientId(), name.getUse(), name.getFamily() };
			int[] types = new int[] {Types.BIGINT, Types.VARCHAR, Types.VARCHAR };
			int row = this.jdbcTemplate.update(SQL_INSERT, params, types);
			if (row > 0) {
				return Optional.of(name);
			} else {
				throw new DaoException("failed to persist patient humanname resource");
			}		
		} catch (DataAccessException dex) {
			throw new DaoException(dex);
		}
	}

	@Override
	public Optional<PatientHumanName> findById(Long id) throws DaoException {
		try {
			RowMapper<PatientHumanName> rowMapper = new PatientHumanNameRowMapper();
			Optional<PatientHumanName> name = Optional.ofNullable(this.jdbcTemplate.queryForObject(SQL_SELECT_BYID, new Object[] { id }, rowMapper));
			return name;
		} catch (DataAccessException dex) {
			throw new DaoException(dex);
		}
	}
}
