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

import java.lang.reflect.Method;
import org.hl7.fhir.dstu3.model.Resource;
import com.frt.dr.update.ResourceUpdateException;

/**
 * ResourceHelper Interface
 * @author cqye
 *
 */
public interface ResourceHelper {

	public static <R extends Resource> Method getMethod(Class<R> resourceClazz, String fieldName, String operator)
		throws ResourceUpdateException {
		String methodName = buldMethodName(fieldName, operator);
		Method method = null;
		Method[] methods = resourceClazz.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				method = m;
				break;
			}
		};
		return method;
	}

	public static String buldMethodName(String field, String operator) {
		StringBuilder strBuilder = new StringBuilder(field);
		strBuilder.setCharAt(0, Character.toUpperCase(strBuilder.charAt(0)));
		strBuilder.insert(0, operator);
		return strBuilder.toString();
	}

	public static String getName(Class clazz) {
		String[] tokens = clazz.getName().split("\\.");
		return tokens[tokens.length-1];
	}
	
}
