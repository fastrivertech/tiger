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
