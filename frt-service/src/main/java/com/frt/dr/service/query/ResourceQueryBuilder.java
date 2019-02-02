/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.service.query;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import com.frt.dr.model.Resource;
import com.frt.dr.service.query.impl.ResourceQueryImpl;

/**
 * ResourceQueryBuilder class
 * @author jimfu
 *
 * @param <T>
 */
public class ResourceQueryBuilder<T extends Resource> {

	private EntityManager em;
	private Class<Resource> resourceClazz;
	private Map<Class<?>, List<CompositeParameter>> parameters;
	
	public static ResourceQueryBuilder<Resource> createBuilder(EntityManager em, 
		Class<Resource> resourceClazz, Map<Class<?>, List<CompositeParameter>> parameters) {
		return new ResourceQueryBuilder<Resource>(em, resourceClazz, parameters);
	}
	
	private ResourceQueryBuilder(EntityManager em, Class<Resource> resourceClazz, 
								 Map<Class<?>, List<CompositeParameter>> parameters) {
		this.em = em;
		this.resourceClazz = resourceClazz;
		this.parameters = parameters;
	}
	
	public ResourceQuery<Resource> createQuery() {
		return new ResourceQueryImpl<Resource>(em, resourceClazz, parameters);
	}
}
