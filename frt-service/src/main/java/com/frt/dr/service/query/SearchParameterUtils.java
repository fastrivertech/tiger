package com.frt.dr.service.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class SearchParameterUtils {
	/**
	 * 
	 * @param params - query parameters captured from rest API
	 * @param groupParams - IN/OUT - actual parameters that represent a group search parameter e.g. Patient.name, Patient.address etc.
	 * @return list of actual parameters extracted from query parameters
	 */
	public static Map<Class<?>, List<ActualParameter>> processParameters(MultivaluedMap<String, String> params) {
		Map<Class<?>, List<ActualParameter>> actualParamsMap = null;
		for (Map.Entry<Class<?>, List<String>> paramsPerClazz : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS.entrySet()) {
			List<ActualParameter> actualParams = null;
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
						ActualParameter actualParam = new ActualParameter(key, mValues);
						actualParam.parse(sp);
						if (actualParams==null) {
							actualParams = new ArrayList<ActualParameter>();
						}
						actualParams.add(actualParam);
					}
				}
				if (actualParams!=null) {
					if (actualParamsMap==null) {
						actualParamsMap = new HashMap<Class<?>, List<ActualParameter>>();
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
	public static String getPlaceHolder(int i, ActualParameter ap) {
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
		return parts;
	}

}
