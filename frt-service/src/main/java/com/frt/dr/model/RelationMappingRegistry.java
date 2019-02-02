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
package com.frt.dr.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.JoinColumn;

public class RelationMappingRegistry {
	
	enum MAPPING_TYPES {ONETOONE, ONETOMANY, MANYTOONE, MANYTOMANY, UNKNOWN};
	private static final List<Class<?>> REF_CLASSES = Arrays.asList(new Class<?>[] {
			com.frt.dr.model.base.PatientAddress.class,
			com.frt.dr.model.base.PatientAttachment.class,
			com.frt.dr.model.base.PatientCodeableConcept.class,
			com.frt.dr.model.base.PatientContactPoint.class,
			com.frt.dr.model.base.PatientExtension.class,
			com.frt.dr.model.base.PatientHumanName.class,
			com.frt.dr.model.base.PatientIdentifier.class,
			com.frt.dr.model.base.PatientReference.class,
	});
	
	/**
	 * relationship mapping registry
	 * <containing class name> -> <attributeName> -> {mappingType[1:M, M:1, 1:1, N:M], joinColumn(s), extraFieldandValuePair(s)}
	 */
	public static final Map<Class, List<MappingDescriptor>> relationships = new HashMap<Class, List<MappingDescriptor>>();
	
	static {
		// currently cover all relationships in FRT resource Patient
		extractRelationMappings(new Class[] {com.frt.dr.model.base.Patient.class});
	}
	
	private static void extractRelationMappings(Class<?>[] resourceClasses) {
		for (Class<?> clazz: resourceClasses) {
			Iterable<Field> fields = getFieldsUpTo(clazz, Object.class);
			List<MappingDescriptor> maps = new ArrayList<MappingDescriptor>();
			for (Field f: fields) {
				ParameterizedType type = (ParameterizedType)f.getGenericType();
				Class<?> atype = (Class<?>)type.getActualTypeArguments()[0];
				if (REF_CLASSES.contains(atype)) {
					JoinColumn col = getJoinColumnAnnotation(f);
					if (col!=null) {
						col.name();
						col.referencedColumnName();
						MAPPING_TYPES mtype = MAPPING_TYPES.UNKNOWN;
						if (f.getAnnotation(javax.persistence.OneToOne.class)!=null) {
							mtype = MAPPING_TYPES.ONETOONE;
						}
						else if (f.getAnnotation(javax.persistence.OneToMany.class)!=null) {
							mtype = MAPPING_TYPES.ONETOMANY;
						}
						if (mtype!=MAPPING_TYPES.UNKNOWN) {
							//new MappingDescriptor();
						}
						f.getName();
					}
				}
			}
			relationships.put(clazz, maps);
		}
	};
	
	private static JoinColumn getJoinColumnAnnotation(Field f) {
		return f.getAnnotation(javax.persistence.JoinColumn.class);
	}

	public static Iterable<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {
		List<Field> currentClassFields = Arrays.asList(startClass.getDeclaredFields());
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}
	
	class MappingDescriptor {
		private String attributeName;
		private MAPPING_TYPES type;
		private Map<String, String> columnJoinPairs;
		private Map<String, Object> columnValuePairs;
		
		public MappingDescriptor() {
		}

		public MappingDescriptor(String attributeName, MAPPING_TYPES type, Map<String, String> colPairs, Map<String, Object> colValPairs) {
			this.attributeName=attributeName;
			this.type=type;
			this.columnJoinPairs=colPairs;
			this.columnValuePairs=colValPairs;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public MAPPING_TYPES getType() {
			return type;
		}

		public void setType(MAPPING_TYPES type) {
			this.type = type;
		}

		public Map<String, String> getColumnJoinPairs() {
			return columnJoinPairs;
		}

		public void setColumnJoinPairs(Map<String, String> columnJoinPairs) {
			this.columnJoinPairs = columnJoinPairs;
		}

		public Map<String, Object> getColumnValuePairs() {
			return columnValuePairs;
		}

		public void setColumnValuePairs(Map<String, Object> columnValuePairs) {
			this.columnValuePairs = columnValuePairs;
		}

	}
}
