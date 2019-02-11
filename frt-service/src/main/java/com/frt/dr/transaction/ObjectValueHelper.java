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
package com.frt.dr.transaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.sql.Timestamp;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * ObjectValueHelper class
 * @author cqye
 */
public class ObjectValueHelper {

	private static Set<String> primitives = Stream.of("long", "java.math.BigInteger", "java.lang.Boolean",
			"java.lang.String", "java.util.Date", "java.sql.Timestamp", "java.lang.Integer")
			.collect(Collectors.toCollection(HashSet::new));
	
	private static Set<String> complexes = Stream
			.of("com.frt.dr.model.base.PatientHumanName", "com.frt.dr.model.base.PatientIdentifier",
					"com.frt.dr.model.base.PatientAddress", "com.frt.dr.model.base.PatientContact",
					"com.frt.dr.model.base.PatientAttachment", "com.frt.dr.model.base.PatientContactPoint",
					"com.frt.dr.model.base.PatientCodeableConcept", "com.frt.dr.model.base.PatientReference",
					"com.frt.dr.model.base.PatientAnimal", "com.frt.dr.model.base.PatientCommunication",
					"com.frt.dr.model.base.PatientLink", "com.frt.dr.model.base.PatientExtension")
			.collect(Collectors.toCollection(HashSet::new));

	public static void setValue(Class clazz, String child, String field, Object object, String value)
			throws ObjectValueException {
		try {
			Field[] fields = clazz.getDeclaredFields();
			Optional<Field> childField = Optional.empty();
			for (Field f : fields) {
				if (f.getName().equals(child)) {
					childField = Optional.of(f);
					break;
				}
			}

			if (childField.isPresent()) {
				if (childField.get().getType().getName().equals("java.util.List")) {
					ParameterizedType listType = (ParameterizedType) childField.get().getGenericType();
					Type type = listType.getActualTypeArguments()[0];

					Object childValue = null;
					Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(child, "get"));
					Object childValues = getMethod.get().invoke(object);
					if (!((List) childValues).isEmpty()) {
						childValue = ((List) childValues).get(0);
						setValue(type.getClass(), field, childValue, value);
					} else {
						childValue = type.getClass().newInstance();
						((List) childValues).add(childValue);
						setValue(type.getClass(), field, childValue, value);
					}
				} else {
					Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(child, "get"));
					if (getMethod.isPresent()) {
						Object childValue = getMethod.get().invoke(object);
						if (childValue == null) {
							childValue = childField.get().getClass().newInstance();
						}
						setValue(childField.get().getClass(), field, childValue, value);
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
			throw new ObjectValueException(ex);
		}

	}

	public static void setValue(Class clazz, String field, Object object, String value) throws ObjectValueException {
		String dataType = getDateType(clazz, field).get();

		Object fieldValue = toValue(value, dataType);
		setValue(clazz, field, object, fieldValue);
	}

	public static void setValue(Class clazz, String field, Object object, Object value) throws ObjectValueException {
		try {
			Optional<Method> setMethod = getClazzMethod(clazz, buldMethodName(field, "set"));
			if (setMethod.isPresent()) {
				setMethod.get().invoke(object, value);
			} else {
				throw new ObjectValueException(
						"set method for " + field + " of " + clazz.getName() + " is not defined");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ObjectValueException(ex);
		}
	}

	public static Optional<String> getDateType(Class clazz, String field) {
		Optional<String> dataType = Optional.empty();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (f.getName().equals(field)) {
				dataType = Optional.of(f.getType().getName());
				break;
			}
		}
		return dataType;
	}

	public static void copyValue(Class clazz, String field, Object source, Object target) throws ObjectValueException {
		try {
			Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(field, "get"));
			if (getMethod.isPresent()) {
				Object value = getMethod.get().invoke(source);
				if (value != null) {
					Optional<Method> setMethod = getClazzMethod(clazz, buldMethodName(field, "set"));
					if (setMethod.isPresent())
						setMethod.get().invoke(target, value);
				} else {
					throw new ObjectValueException(
							"set method for " + field + " of " + clazz.getName() + " is not defined");
				}
			} else {
				throw new ObjectValueException(
						"get method for " + field + " of " + clazz.getName() + " is not defined");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ObjectValueException(ex);
		}
	}

	public static boolean equals(Class clazz, String name) {
		boolean compared = false;
		String[] tokens = clazz.getName().split(".");
		if (tokens[tokens.length - 1].equalsIgnoreCase(name)) {
			compared = true;
		}
		return compared;
	}

	public static Optional<Method> getClazzMethod(Class clazz, String methodName) throws ObjectValueException {
		List<Method> methods = getMethods(clazz);
		Optional<Method> found = Optional.empty();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				found = Optional.of(method);
				break;
			}
		}
		;
		return found;
	}

	public static List<Method> getMethods(Class clazz) {
		List<Method> methods = new ArrayList<>();
		Method[] primaryMethods = clazz.getDeclaredMethods();
		if (clazz.getSuperclass() != null) {
			Class superClazz = clazz.getSuperclass();
			List<Method> superMethods = getMethods(superClazz);
			methods.addAll(superMethods);
		}
		;
		methods.addAll(Arrays.asList(primaryMethods));
		return methods;
	}

	public static String buldMethodName(String field, String operator) {
		StringBuilder strBuilder = new StringBuilder(field);
		strBuilder.setCharAt(0, Character.toUpperCase(strBuilder.charAt(0)));
		strBuilder.insert(0, operator);
		return strBuilder.toString();
	}

	private static Optional<Object> toValue(String value, String dataType) throws ObjectValueException {
		Optional<Object> dataObject = Optional.empty();
		switch (dataType) {
		case "long":
			dataObject = Optional.of(Long.valueOf(value));
			break;
		case "java.lang.Integer":
			dataObject = Optional.of(Integer.parseInt(value));
			break;
		case "java.math.BigInteger":
			dataObject = Optional.of(BigInteger.valueOf(Long.parseLong(value)));
			break;
		case "java.lang.Boolean":
			dataObject = Optional.of(Boolean.valueOf(value));
			break;
		case "java.lang.String":
			dataObject = Optional.of(value);
			break;
		case "java.util.Date":
			dataObject = Optional.of(Date.parse(value));
			break;
		case "java.sql.Timestamp":
			dataObject = Optional.of(Timestamp.valueOf(value));
			break;
		default:
			throw new ObjectValueException(dataType + " not supported yet");
		}
		return dataObject;
	}

	public static boolean equals(String dataType, Object value1, Object value2) {
		boolean compared = false;
		switch (dataType) {
		case "long":
			compared = value1 == value2 ? true : false;
			break;
		case "java.lang.Integer":
			compared = value1 == value2 ? true : false;
			break;
		case "java.math.BigInteger":
			compared = value1 == value2 ? true : false;
			break;
		case "java.lang.Boolean":
			compared = value1 == value2 ? true : false;
			break;
		case "java.lang.String":
			compared = value1.equals(value2) ? true : false;
			break;
		case "java.util.Date":
			compared = ((Date) value1).compareTo((Date) value2) == 0 ? true : false;
			break;
		case "java.sql.Timestamp":
			compared = ((Timestamp) value1).compareTo((Timestamp) value2) == 0 ? true : false;
			break;
		default:
			throw new ObjectValueException(dataType + " not supported yet");
		}
		return compared;
	}

	public static void xinvoke(Method setMethod, Object object, String dataType, Object value)
			throws ObjectValueException {
		try {
			switch (dataType) {
			case "long":
				setMethod.invoke(object, value);
				break;
			case "java.lang.Integer":
				setMethod.invoke(object, value);
				break;
			case "java.math.BigInteger":
				setMethod.invoke(object, value);
				break;
			case "java.lang.Boolean":
				setMethod.invoke(object, value);
				break;
			case "java.lang.String":
				setMethod.invoke(object, value);
				break;
			case "java.util.Date":
				setMethod.invoke(object, value);
				break;
			case "java.sql.Timestamp":
				setMethod.invoke(object, value);
				break;
			default:
				throw new ObjectValueException(dataType + " not supported yet");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ObjectValueException(ex);
		}
	}

}
