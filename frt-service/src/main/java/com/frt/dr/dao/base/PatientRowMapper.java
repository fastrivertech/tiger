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

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.frt.dr.model.base.Patient;

public class PatientRowMapper implements RowMapper<Patient> {

	@Override
	public Patient mapRow(ResultSet row, int rowNum) 
		throws SQLException {
		Patient patient = new Patient();
		
		return patient;
	}	
	
}
