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
package com.frt.dr.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * RepositoryContext class
 * @author chaye
 */
public class RepositoryContext {
	@Autowired
	private ApplicationContext context;

	public RepositoryContext(java.lang.Class<?>... annotatedClasses) {
		context = new AnnotationConfigApplicationContext(annotatedClasses);			
	}
		
	public Object getBean(java.lang.Class<?> annotatedClass) 
		throws RepositoryContextException {
		try {
			return context.getBean(annotatedClass);
		} catch (BeansException bex) {
			throw new RepositoryContextException(bex);
		}
	}
	
}
