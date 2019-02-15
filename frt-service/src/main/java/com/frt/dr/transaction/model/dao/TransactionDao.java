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
package com.frt.dr.transaction.model.dao;

import java.util.List;
import java.util.Optional;
import java.math.BigInteger;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;

public abstract class TransactionDao<T,ID> extends BaseDao<T,ID> {

	public abstract Optional<List<T>> findByResourceId(BigInteger id) 
		throws DaoException;      
	
}
