package com.frt.dr.service;

import com.frt.dr.model.DomainResource;

public class RepositoryServiceImpl implements RepositoryService {
	
	@Override
	public <R extends DomainResource> R findById(Class<R> resourceClazz, String id) 
			throws RepositoryServiceException {
		R resource = null;
		return resource;
	}
		
	@Override
	public <R extends DomainResource> R save(R resource)
		   throws RepositoryServiceException {
		return resource;
	}
	
}
