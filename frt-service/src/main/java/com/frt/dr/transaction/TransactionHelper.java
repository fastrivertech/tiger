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
import java.time.ZonedDateTime;
import java.math.BigInteger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.frt.dr.transaction.model.Meta;
import com.frt.dr.transaction.model.Transaction;
import com.frt.dr.transaction.model.PatientTransaction;

public class TransactionHelper {

	public static <T extends Transaction> T createTransaction(Transaction.ActionCode code) {
		PatientTransaction transaction = new PatientTransaction();	
		transaction.setMeta(new Meta().toString());
		transaction.setAction(code.name());
		transaction.setActor("frt");		
		transaction.setTimestamp(new Timestamp(new Date().getTime()));
		return (T)transaction;		
	}
	
	public static String updateMeta(String json) {
		Meta meta = jsonToObject(json);
		int version = Integer.parseInt(meta.getversionId());
		meta.setVersionId(Integer.toString(version));
		meta.setLastUpdated(ZonedDateTime.now().toString());
		return objectToJson(meta);
	}
	
	public static Meta jsonToObject(String json) {
		Gson gson = new Gson();
		Meta meta = gson.fromJson(json, Meta.class);
		return meta;
	}

	public static String objectToJson(Meta meta) {
		Gson gson = new Gson();
		String json = gson.toJson(meta);
		return json;		
	}
	
}
