package com.frt.dr.service.query;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.frt.dr.model.Resource;
import com.frt.dr.service.query.impl.ResourceQueryImpl;

public class ResourceQueryBuilder<T extends Resource> {
	private EntityManager em;
	private Class<Resource> resourceClazz;
	private Map<Class<?>, List<ActualParameter>> parameters;
	
	public static ResourceQueryBuilder<Resource> createBuilder(EntityManager em, 
			Class<Resource> resourceClazz, Map<Class<?>, List<ActualParameter>> parameters) {
		return new ResourceQueryBuilder<Resource>(em, resourceClazz, parameters);
	}
	
	private ResourceQueryBuilder(EntityManager em, Class<Resource> resourceClazz, Map<Class<?>, List<ActualParameter>> parameters) {
		this.em = em;
		this.resourceClazz = resourceClazz;
		this.parameters = parameters;
	}
	
	public ResourceQuery<Resource> createQuery() {
		return new ResourceQueryImpl<Resource>(em, resourceClazz, parameters);
	}
}
