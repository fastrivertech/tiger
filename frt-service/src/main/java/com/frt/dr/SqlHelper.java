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
package com.frt.dr;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import com.frt.dr.model.datatype.Period;

/**
 * SqlHelper class
 * @author chaye
 */
public class SqlHelper {

	@SuppressWarnings("finally")
	public static Clob toClob(List<String> strings, Connection con) {
		Clob clob = null;
		try {
			StringBuilder strBuild = new StringBuilder();
			strings.forEach(string -> {
				if (strBuild.length() > 0) {
					strBuild.append(",");
				}
				strBuild.append(string);
			});
			clob = con.createClob();
			clob.setString(1, strBuild.toString());
		} catch (SQLException sqlex) {
		} finally {
			return clob;
		}
	}
	
	@SuppressWarnings("finally")
	public static List<String> toString(Clob clob) {
		List<String> strings = null;
		try {
			String tokens = clob.getSubString(1, (int)clob.length());
			strings = Arrays.asList(tokens.split(","));
		} catch (SQLException sqlex) {
		} finally {
			return strings;
		}
	}

	@SuppressWarnings("finally")
	public static List<Period> toPeriod(Clob clob) {
		List<Period> periods = null;
		return periods;
	}	
}
