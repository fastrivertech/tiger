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
package com.frt.dr.update;

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

public class ResourceUpdateHelper {
	
	public static Set<String> primitives = Stream.of("long",
												     "java.math.BigInteger",
												     "java.lang.Boolean",
											  	     "java.lang.String",
												     "java.util.Date",
												     "java.sql.Timestamp",
											         "java.lang.Integer").collect(Collectors.toCollection(HashSet::new));
	public static Set<String> complexes = Stream.of("com.frt.dr.model.base.PatientHumanName",
													"com.frt.dr.model.base.PatientIdentifier",
												    "com.frt.dr.model.base.PatientAddress",
												    "com.frt.dr.model.base.PatientContact",
												    "com.frt.dr.model.base.PatientAttachment",
												    "com.frt.dr.model.base.PatientContactPoint",
												    "com.frt.dr.model.base.PatientCodeableConcept",
												    "com.frt.dr.model.base.PatientReference",
												    "com.frt.dr.model.base.PatientAnimal",
												    "com.frt.dr.model.base.PatientCommunication",
												    "com.frt.dr.model.base.PatientLink",
												    "com.frt.dr.model.base.PatientExtension").collect(Collectors.toCollection(HashSet::new));	
	/**
	 * Set the value of the primitive field of primary object, e.g., Patient.gender
	 * @param clazz primary class 
	 * @param field primitive field 
	 * @param object primary object 
	 * @param value primitive field value 
	 * @throws ResourceUpdateException throws exception if any error occurs
	 */
	public static void setValue(Class clazz, String field, Object object, String value) 
		throws ResourceUpdateException {
		String dataType = getDateType(clazz, field).get();	
		Optional<Object> fieldValue = stringToObject(value, dataType);
		if (fieldValue.isPresent()) {
			setValue(clazz, field, object, fieldValue.get());
		}
	}
	
	/**
	 * Set the value of the primitive field of primary object, e.g., Patient.gender
	 * @param clazz primary class 
	 * @param field primitive field 
	 * @param object primary object 
	 * @param value primitive field value 
	 * @throws ResourceUpdateException throws exception if any error occurs
	 */	
	public static void setValue(Class clazz, String field, Object object, Object value) 
		throws ResourceUpdateException {
		try {
			Optional<Method> setMethod = getClazzMethod(clazz, buldMethodName(field, "set"));
			if (setMethod.isPresent()) {
				setMethod.get().invoke(object, value);
			} else {
				throw new ResourceUpdateException("set method for " + field + " of " + clazz.getName() + " is not defined");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ResourceUpdateException(ex);
		}
	}

	/**
	 * Set the value of the complex field or list of complex of primary object,e.g.,
	 * Patient.maritalStatus.coding, Patient.names.family (List)
	 * @param clazz primary class
	 * @param child child name
	 * @param field child primitive field name
	 * @param object primary object
	 * @param value value of child primitive field
	 * @throws ResourceUpdateException throws exception if any error occurs
	 */
	public static void setValue(Class clazz, String child, String field, Object object, String value)
		throws ResourceUpdateException {
		try {
			List<Field> fieldList = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
			fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));		
			Field[] fields = fieldList.toArray(new Field[] {});
			
			Optional<Field> childField = Optional.empty();
			for (Field f : fields) {
				if (f.getName().equalsIgnoreCase(child)) {
					childField = Optional.of(f);
					break;
				}
			}
			if (childField.isPresent()) {
				if (childField.get().getType().getName().equals("java.util.List")) {
					// List complex data type
					ParameterizedType listType = (ParameterizedType) childField.get().getGenericType();
					Type type = listType.getActualTypeArguments()[0];

					Object childValue = null;
					Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(child, "get"));
					Object childValues = getMethod.get().invoke(object);
					if (!((List) childValues).isEmpty()) {
						childValue = ((List) childValues).get(0);
						setValue(Class.forName(type.getTypeName()), field, childValue, value);
					} else {
						childValue = Class.forName(type.getTypeName()).newInstance();
						((List) childValues).add(childValue);
						setValue(Class.forName(type.getTypeName()), field, childValue, value);
					}
				} else {
					// complex data type
					Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(child, "get"));
					Object childValue = getMethod.get().invoke(object);
					if (childValue == null) {
						childValue = childField.get().getType().newInstance();
						Optional<Method> setMethod = getClazzMethod(clazz, buldMethodName(child, "set"));
						setMethod.get().invoke(object, childValue);
					}
					setValue(childField.get().getType(), field, childValue, value);
				}
			}
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException
				| ClassNotFoundException ex) {
			throw new ResourceUpdateException(ex);
		}
	}
		
	public static void setFieldValue(Class clazz, String field, Object object, Object fieldValue) 
		throws ResourceUpdateException {
		try {
			
			Optional<Method> setMethod = getClazzMethod(clazz, buldMethodName(field, "set"));
			if (setMethod.isPresent()) {
				setMethod.get().invoke(object, fieldValue);
			}
		}  catch(InvocationTargetException | IllegalAccessException ex) {
			throw new ResourceUpdateException(ex);
		}
	}	
	
	public static Optional<Object> getFieldValue(Class clazz, String field, Object source) 
		throws ResourceUpdateException {
		try {
			Optional<Object> value = Optional.empty();
			Optional<Method> getMethod = getClazzMethod(clazz, buldMethodName(field, "get"));
			if (getMethod.isPresent()) {
				value = Optional.ofNullable(getMethod.get().invoke(source));
			}
			return value;
		}  catch(InvocationTargetException | IllegalAccessException ex) {
			throw new ResourceUpdateException(ex);
		}
	}	
	
	public static boolean equals(Class clazz, String name) {
		boolean compared = false;
		String[] tokens = clazz.getName().split("\\.");
		if (tokens[tokens.length-1].equalsIgnoreCase(name)) {
			compared = true;
		}		
		return compared;
	}
	
	public static String name(Class clazz) {
		String[] tokens = clazz.getName().split("\\.");
		return tokens[tokens.length-1];
	}
	
	public static List<Method> getMethods(Class clazz) {
		List<Method> methods = new ArrayList<>();
		Method[] primaryMethods = clazz.getDeclaredMethods();
		if (clazz.getSuperclass() != null) {
			Class superClazz = clazz.getSuperclass();
			List<Method> superMethods = getMethods(superClazz);
			methods.addAll(superMethods);
		};
		methods.addAll(Arrays.asList(primaryMethods));
		return methods;
	}

	public static String buldMethodName(String field, String operator) {
		StringBuilder strBuilder = new StringBuilder(field);
		strBuilder.setCharAt(0, Character.toUpperCase(strBuilder.charAt(0)));
		strBuilder.insert(0, operator);
		return strBuilder.toString();
	}
	
	public static Optional<Method> getClazzMethod(Class clazz, String methodName) 
		throws ResourceUpdateException {
		List<Method> methods = getMethods(clazz);
		Optional<Method> found = Optional.empty();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				found = Optional.of(method);
				break;
			}
		};
		return found;
	}
	
	public static Optional<String> getDateType(Class clazz, String field) {
		
		Optional<String> dataType = Optional.empty();
		List<Field> fieldList = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
		fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));		
		Field[] fields = fieldList.toArray(new Field[] {});
		
		for (Field f : fields) {
			if (f.getName().equals(field)) {
				dataType = Optional.of(f.getType().getName());
				break;
			}
		}
		return dataType;
	}
	
	public static Optional<Object> stringToObject(String value, String dataType) 
		throws ResourceUpdateException {
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
			throw new ResourceUpdateException(dataType + " not supported yet");
		}
		return dataObject;
	}

	public static Optional<String> objectToString(Object value, String dataType) 
		throws ResourceUpdateException {
		Optional<String> dataString = Optional.empty();
		switch (dataType) {
		case "long":
			dataString = Optional.of(value.toString());
			break;
		case "java.lang.Integer":
			dataString = Optional.of(value.toString());
			break;
		case "java.math.BigInteger":
			dataString = Optional.of(value.toString());
			break;
		case "java.lang.Boolean":
			dataString = Optional.of(Boolean.toString(((Boolean)value).booleanValue()));
			break;
		case "java.lang.String":
			dataString = Optional.of(value.toString());
			break;
		case "java.util.Date":
			dataString = Optional.of(((Date)value).toString());
			break;
		case "java.sql.Timestamp":
			dataString = Optional.of(((Timestamp)value).toString());
			break;
		default:
			throw new ResourceUpdateException(dataType + " not supported yet");
		}
		return dataString;
	}
	
	public static boolean notEquals(String dataType, Object value1, Object value2) {
		return !equals(dataType, value1, value2);
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
			compared = ((Date)value1).compareTo((Date)value2) == 0 ? true : false;
			break;
		case "java.sql.Timestamp":
			compared = ((Timestamp)value1).compareTo((Timestamp)value2) == 0 ? true : false;
			break;
		default:
			throw new ResourceUpdateException(dataType + " not supported yet");
		}		
		return compared;
	}			
}
