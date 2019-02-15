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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;
import java.lang.reflect.ParameterizedType;

public class ResourceUpdateManager {

	private List<String> changes = new ArrayList<>();
	
	public ResourceUpdateManager() {	
	}
	
	public Optional<String> getChanges(){
		Optional<String> changed = Optional.empty();
		StringBuilder strBuilder = new StringBuilder();
		changes.forEach(change->{
			if (strBuilder.length() > 0) {
				strBuilder.append(",");
			}
			strBuilder.append(change.trim());
		});		
		if (strBuilder.length() > 0) {
			changed = Optional.of(strBuilder.toString());
		}		
		return changed;
	}
	
	public void cleanChanges(){
		changes.clear();
	}
	
	/**
	 * Set the value of the field
	 * @param clazz primary class
	 * @param path path to field
	 * @param object primary object
	 * @param value value of field
	 * @throws ResourceUpdateException throws exception if any error occurs 
	 */
	@SuppressWarnings("unchecked")
	public void update(Class clazz, String path, Object object, String value) 
		throws ResourceUpdateException {		
		// Primitive data type: Path = Patient.gender
		// Complex data type: Path = Patient.maritalStatus.coding
		// List Complex data type: Path = Patient.names.family (List)	
		// Only 2 tiers supported 
		String[] tokens = path.split("\\.");
		if (tokens.length == 2 && 
			ResourceUpdateHelper.equals(clazz, tokens[0])) {
			ResourceUpdateHelper.setValue(clazz, tokens[1], object, value);
		} else if (tokens.length == 3 &&
				   ResourceUpdateHelper.equals(clazz, tokens[0])) {
			ResourceUpdateHelper.setValue(clazz, tokens[1], tokens[2], object, value);								
		} else {
			throw new  ResourceUpdateException("invalid path: " + path);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void change(Class clazz, Class root, Object source, Object target) 
		throws ResourceUpdateException {
		
		List<Field> fieldList = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
		fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));		
		Field[] fields = fieldList.toArray(new Field[] {});
		
		Stream.of(fields).forEach(field->{
			
			if (field.getType().getName().equals("java.util.List")) {
				// List
				ParameterizedType listType = (ParameterizedType)field.getGenericType();				
				Type type = listType.getActualTypeArguments()[0];
				/*
				if (!ResourceUpdateHelper.primitives.contains(type.toString())) {
					// List complex data type					
					try {
						
						Optional<Object> sourceFieldValues = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), source);					
						if (!((List)sourceFieldValues.get()).isEmpty()) {
						
							Object sourceFieldValue = ((List)sourceFieldValues.get()).get(0);
							Optional<Object> targetFieldValues = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), target);					
							Object targetFieldValue = null;
							if (((List)targetFieldValues.get()).isEmpty()) {
								targetFieldValue = Class.forName(type.getTypeName()).newInstance();
								((List)targetFieldValues.get()).add(targetFieldValue);
							} else {
								targetFieldValue = ((List)targetFieldValues.get()).get(0);
							}		
							String path = ResourceUpdateHelper.name(root) + "." + field.getName();
							copyObjectValue(path, Class.forName(type.getTypeName()), sourceFieldValue, targetFieldValue);
						}
						
					} catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
						throw new ResourceUpdateException(ex);
					}
				} else {
					// List primitive data type
					throw new ResourceUpdateException("List primitive type " + type.toString() + " not supported");
				}
				*/
			} else if (ResourceUpdateHelper.primitives.contains(field.getType().getName())) {
				// primitive data type				
				if (!field.getName().equals("serialVersionUID")){					
					Optional<Object> sourceFieldValue = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), source);
					Optional<Object> targetFieldValue = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), target);				
											
					if (sourceFieldValue.isPresent()) {						
						if (targetFieldValue.isPresent()) {
							if (!ResourceUpdateHelper.equals(field.getType().getName(), sourceFieldValue.get(), targetFieldValue.get())) {
								
								String changed = ResourceUpdateHelper.name(root) + "." + field.getName() + "=" + targetFieldValue.get().toString();
								changes.add(changed);								
								ResourceUpdateHelper.setFieldValue(clazz, field.getName(), target, sourceFieldValue.get());
							}
						} else {							
							String changed = ResourceUpdateHelper.name(root) + "." + field.getName() + "=NULL";
							changes.add(changed);
							ResourceUpdateHelper.setFieldValue(clazz, field.getName(), target, sourceFieldValue.get());							
						}
					}
				}
			} else if (!field.getType().getName().equals(root.getName())) {
				// complex data type				
				try {
					Optional<Object> sourceFieldValue = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), source);
					if (sourceFieldValue.isPresent()) {
						Optional<Object> targetFieldValue = ResourceUpdateHelper.getFieldValue(clazz, field.getName(), target);
						if (!targetFieldValue.isPresent()) {
							targetFieldValue = Optional.of(field.getType().newInstance());
							ResourceUpdateHelper.setFieldValue(clazz, field.getName(), target, targetFieldValue.get());														
						}
						String path = ResourceUpdateHelper.name(root) + "." + field.getName();						
						copyObjectValue(path, field.getType(), sourceFieldValue.get(),  targetFieldValue.get());					
					}								
				} catch (IllegalAccessException | InstantiationException ex) {
					throw new ResourceUpdateException(ex);
				}
			} else {
				// Unknown data type
				if (!field.getType().getName().equals(root.getName())) {
					throw new ResourceUpdateException(clazz.getName() + " unknown Field " + field.getName());
				}
			}
			
		});		
	}
	
	@SuppressWarnings("unchecked")	
	public void copyObjectValue(String path, Class clazz, Object source, Object target) throws ResourceUpdateException {

		List<Field> fieldList = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
		fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));		
		Field[] fields = fieldList.toArray(new Field[] {});
		
		Stream.of(fields).forEach(field -> {
			if (ResourceUpdateHelper.primitives.contains(field.getType().getName())) {
				if (!field.getName().equals("serialVersionUID")) {
					copyFieldValue(path, clazz, field, source, target);
				}
			} // enhance to support child object with complex data type
		});
	}

	public void copyFieldValue(String path, Class clazz, Field field, Object source, Object target)
		throws ResourceUpdateException {
		try {
			Optional<Method> getMethod = ResourceUpdateHelper.getClazzMethod(clazz,
					ResourceUpdateHelper.buldMethodName(field.getName(), "get"));
			if (getMethod.isPresent()) {
				Object sourceFieldValue = getMethod.get().invoke(source);
				if ( sourceFieldValue != null) {
					Object targetFieldValue = getMethod.get().invoke(target);
					if (targetFieldValue == null || 
						ResourceUpdateHelper.notEquals(field.getType().getTypeName(), sourceFieldValue, targetFieldValue)) { 
					
						Optional<Method> setMethod = ResourceUpdateHelper.getClazzMethod(clazz,
								ResourceUpdateHelper.buldMethodName(field.getName(), "set"));
						if (setMethod.isPresent()) {
							setMethod.get().invoke(target, sourceFieldValue);
							String changed = path + "." + field.getName() + "="
									+ ResourceUpdateHelper.objectToString(sourceFieldValue, field.getType().getTypeName()).get();
							System.out.println("333333: " + changed);
							changes.add(changed);
						}
						
					}
				}
			} else {
				throw new ResourceUpdateException(
						"get method for " + field + " of " + clazz.getName() + " is not defined");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ResourceUpdateException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void copyFieldValue(String path, Class clazz, String field, Object source, Object target)
			throws ResourceUpdateException {
		try {
			Optional<Method> getMethod = ResourceUpdateHelper.getClazzMethod(clazz,
					ResourceUpdateHelper.buldMethodName(field, "get"));
			if (getMethod.isPresent()) {
				Object value = getMethod.get().invoke(source);
				if (value != null) {
					Optional<Method> setMethod = ResourceUpdateHelper.getClazzMethod(clazz,
							ResourceUpdateHelper.buldMethodName(field, "set"));
					if (setMethod.isPresent()) {
						setMethod.get().invoke(target, value);
					  //String changed = path + "." + field + "=" + value.toString();								
					  //changes.add(changed);
					}
				}
			} else {
				throw new ResourceUpdateException(
						"get method for " + field + " of " + clazz.getName() + " is not defined");
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new ResourceUpdateException(ex);
		}
	}

}

