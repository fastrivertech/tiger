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
package com.frt.dr.cache;

import java.util.Optional;

/**
 * CacheService class
 * @author cqye
 */
public class CacheService {	
	private static CacheService instance;
	
	private ThreadLocal<NamedCache<String, String>> cache;
		
	private CacheService() {	
	}
	
	public static CacheService getInstance() {	
		if (instance == null) {
			instance = new CacheService();
		}
		return instance;
	}
	
	public void createCache() {
		if (cache == null) {
			cache = new ThreadLocal<NamedCache<String, String>>();
			cache.set(new NamedCache<String, String>());
		}
	}
	
	public Optional<NamedCache<String, String>> getCache() {
		if (cache != null) {
			return Optional.of(cache.get());
		} else {
			return Optional.empty();
		}
	}
	
	public void destroyCache() {		
		if (cache != null) {
			cache.set(null);
			cache = null;
		}
	}
}
