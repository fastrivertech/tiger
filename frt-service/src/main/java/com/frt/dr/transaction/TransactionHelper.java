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
package com.frt.dr.transaction;

import java.util.Date;
import java.sql.Timestamp;
import java.math.BigInteger;
import com.frt.dr.transaction.model.Transaction;
import com.frt.dr.transaction.model.PatientTransaction;

public class TransactionHelper {

	public static <T extends Transaction> T createTransaction() {
		PatientTransaction transaction = new PatientTransaction();	
		// ToDo
		transaction.setTransactionId(BigInteger.ONE);
		transaction.setResourceId(BigInteger.ONE);		
		transaction.setAction("created");
		transaction.setActor("foo");		
		transaction.setTimestamp(new Timestamp(new Date().getTime()));
		return (T)transaction;		
	}
}
