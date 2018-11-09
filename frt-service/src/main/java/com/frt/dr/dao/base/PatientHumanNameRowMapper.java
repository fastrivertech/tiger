package com.frt.dr.dao.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.frt.dr.model.base.PatientHumanName;

public class PatientHumanNameRowMapper implements RowMapper<PatientHumanName> {

	@Override
	public PatientHumanName mapRow(ResultSet row, int rowNum) 
		throws SQLException {
		PatientHumanName name = new PatientHumanName();
		name.setHumannameId(row.getLong("humanname_id"));
		name.setPatientId(row.getLong("patient_id"));
		name.setUse(row.getString("use"));
		name.setFamily(row.getString("family"));		
		return name;
	}	
}
