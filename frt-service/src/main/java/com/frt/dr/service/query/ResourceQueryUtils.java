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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

/**
 * SearchParameterUtils class
 * @author jfu
 */
public class ResourceQueryUtils {
	/**
	 * 
	 * @param params - query parameters captured from rest API
	 * @param groupParams - IN/OUT - actual parameters that represent a group search parameter e.g. Patient.name, Patient.address etc.
	 * @return list of actual parameters extracted from query parameters
	 */
	public static Map<Class<?>, List<CompositeParameter>> processParameters(MultivaluedMap<String, String> params) {
		Map<Class<?>, List<CompositeParameter>> actualParamsMap = null;
		for (Map.Entry<Class<?>, List<String>> paramsPerClazz : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS.entrySet()) {
			List<CompositeParameter> actualParams = null;
			Class<?> clazz = paramsPerClazz.getKey();
			for (String paramName : paramsPerClazz.getValue()) {
				Iterator<String> it = params.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					List<String> mValues = (List)params.get(key);
					String[] name_parts = parseParamName(key);
					String baseName = name_parts[0];
					if (baseName.equals(paramName)) {
						SearchParameter sp = SearchParameterRegistry.getParameterDescriptor(baseName);
						CompositeParameter actualParam = new CompositeParameter(key, mValues);
						actualParam.parse(sp);
						if (actualParams==null) {
							actualParams = new ArrayList<CompositeParameter>();
						}
						actualParams.add(actualParam);
					}
				}
				if (actualParams!=null) {
					if (actualParamsMap==null) {
						actualParamsMap = new HashMap<Class<?>, List<CompositeParameter>>();
					}
					actualParamsMap.put(clazz, actualParams);
				}
			}
		}
		return actualParamsMap;
	}

	/**
	 * 
	 * @param i - the position of the query parameter
	 * @param aparam - the actual parameter
	 * @return the mangled parameter place holder 
	 */
	public static String getPlaceHolder(int i, CompositeParameter ap) {
		StringBuilder sb = new StringBuilder(ap.getBaseName());
		String m = ap.getModifier()!=null?ap.getModifier():"";
		String c = ap.getComparator()!=null&&ap.getComparator().size()>0?ap.getComparator().get(i):"";
		sb.append("_").append(m).append(c).append("_").append(i);
		return sb.toString();
	}

	/**
	 * helper - given a string value, generate SQL predicate LIKE pattern
	 * @param value - the given string value
	 * @return the SQL LIKE predicate matching pattern 
	 */
	public static String convertToLikePattern(String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("%").append(value).append("%");
		return sb.toString();
	}

	/**
	 * helper - given a raw rest query parameter name, decompose it into base name and modifier if modifier presents
	 * @param n - the raw name
	 * @return the base name and modifier (if present)
	 */
	public static String[] parseParamName(String n) {
		String[] parts = n.split(SearchParameterRegistry.PARAM_MODIFIER_DELIMETER);
		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed parameter name: " + n + ", parameter name format: <name> or <name>:<modifier>.");
		}
		parts[0]=parts[0].trim();
		if (parts.length == 2) {
			String md = parts[1].trim();
			SearchParameter.Modifier m = SearchParameterRegistry.getModifier(md);
			if (m == null) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + n);
			}
			parts[1]=md;
		}
		return parts;
	}


	public static Object parseNumeric(Class<?> type, String value) {
		Object parsedValue = null;
		// number parameter
		// BigInteger, Byte, Double, Float, Integer, Long, Short
		if (type.equals(Integer.class)) {
			parsedValue = Integer.valueOf(value);
		} else if (type.equals(Long.class)) {
			parsedValue = Long.valueOf(value);
		} else if (type.equals(Short.class)) {
			parsedValue = Short.valueOf(value);
		} else if (type.equals(Float.class)) {
			parsedValue = Float.valueOf(value);
		} else if (type.equals(Double.class)) {
			parsedValue = Double.valueOf(value);
		} else {
			throw new IllegalArgumentException(
					"Numeric parameter of type :" + type.getCanonicalName() + " not supported yet, value=" + value);
		}
		return parsedValue;
	}

	public static Date parseDate(String value) {
		Date d = null;
		for (int i = 0; i < SearchParameterRegistry.DF_FMT_SUPPORTED.length; i++) {
			try {
				d = SearchParameterRegistry.DF_FMT_SUPPORTED[i].parse(value);
				break;
			} catch (ParseException e) {
				continue;
			}
		}
		return d;
	}
}
