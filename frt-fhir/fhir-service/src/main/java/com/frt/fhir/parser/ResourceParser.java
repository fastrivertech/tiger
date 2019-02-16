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
package com.frt.fhir.parser;

import java.util.List;
import org.hl7.fhir.dstu3.model.Resource;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hl7.fhir.dstu3.model.Element;

public class ResourceParser {
	private JsonParser delegate;
	
	public ResourceParser() {
		FhirContext context = FhirContext.forDstu3();
		delegate = (JsonParser)context.newJsonParser();		
	}
	
	@SuppressWarnings("unchecked")
	public <R extends Resource, E extends Element> String serialize(Class<R> resourceClazz, 
																	String fieldName, 
																	E element) 
		throws ResourceFormatException {
		try {
			R resource = resourceClazz.newInstance();
			Method setMethod = ResourceHelper.getMethod(resourceClazz, fieldName, "set");
			setMethod.invoke(resource, element);
			String encoded = delegate.encodeResourceToString(resource);
			return encoded.substring(encoded.indexOf(",") + 1, encoded.length() - 1);			
		} catch(IllegalAccessException | InstantiationException | InvocationTargetException  ex) {
			throw new ResourceFormatException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public <R extends Resource, E extends Element> String serializes(Class<R> resourceClazz, 
																	 String fieldName, 
																	 List<E> elements) 
		throws ResourceFormatException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public <R extends Resource, E extends Element> E deserialize(Class<R> resourceClazz, 
															     String fieldName, 
																 String message) 
		throws ResourceFormatException {
		try {
			String name =  ResourceHelper.getName(resourceClazz);
			message = "{ \"resourceType\" : \"" + name + "\"," + message + "}";
			R resource = delegate.parseResource(resourceClazz, message);
			Method getMethod = ResourceHelper.getMethod(resourceClazz, fieldName, "get");
			E element = (E)getMethod.invoke(resource);
			return element;
		} catch (IllegalAccessException | InvocationTargetException  ex) {
			throw new ResourceFormatException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <R extends Resource, E extends Element> List<E> deserializes(Class<R> resourceClazz, 
																        String fieldName, 
																        String message) 
		throws ResourceFormatException {
		throw new UnsupportedOperationException();
	}	
	
}
