package com.frt.dr.dao.base;

import java.sql.Clob;
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
		name.setPath(row.getString("path"));
		name.setUse(row.getString("use"));
		name.setTxt(row.getString("txt"));		
		name.setFamily(row.getString("family"));		
		name.setGiven(row.getClob("given"));	
		name.setGiven(row.getClob("prefix"));	
		name.setGiven(row.getClob("suffix"));	
		name.setGiven(row.getClob("period"));
		return name;
	}	
	
}