/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.fhir.rest;

import java.util.Set;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

/**
 * ResourceInteractionApplication class
 * 
 * @author cqye
 */
@ApplicationPath("/")
public class ResourceOperationApplication extends Application {

	public ResourceOperationApplication() {
		super();
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new java.util.HashSet<>();
		resources.add(CreateResourceOperation.class);
		resources.add(ReadResourceOperation.class);		
		resources.add(CapabilityResourceOperation.class);		
		return resources;
	}
}
