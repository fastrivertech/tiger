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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * RepositoryApplication class
 * @author chaye
 */
@ComponentScan(basePackages = "com.frt.dr")
public class RepositoryApplication {

	private RepositoryService repositoryService;
	
	@Autowired
	public RepositoryApplication(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	
	public void create()
		throws RepositoryServiceException {
		//FHIR model to FRT model
		
	}

	public void read()
		throws RepositoryServiceException {			
		//FRT model to FHIR model 
				
	}
	
	public static void main(String[] args) {
		try {
			ApplicationContext context = new AnnotationConfigApplicationContext(RepositoryApplication.class);			
			RepositoryApplication repositoryApplication = context.getBean(RepositoryApplication.class);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}				
	}
	
}
