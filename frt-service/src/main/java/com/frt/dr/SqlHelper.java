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

/**
 * SqlHelper class
 * @author chaye
 */
public class SqlHelper {

	@SuppressWarnings("finally")
	public static Clob toClob(String string, Connection con) {
		Clob clob = null;
		try {
			clob = con.createClob();
			clob.setString(1, string);
		} catch (SQLException sqlex) {
		} finally {
			return clob;
		}
	}
	
	@SuppressWarnings("finally")
	public static String toString(Clob clob) {
		String string = null;
		try {
			string = clob.getSubString(1, (int)clob.length());
		} catch (SQLException sqlex) {
		} finally {
			return string;
		}
	}

}
