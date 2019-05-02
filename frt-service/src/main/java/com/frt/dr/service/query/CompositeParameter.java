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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ActualParameter class
 * @author jfu
 */
public class CompositeParameter {
	// name from web query, name can be with modifier etc
	// if ActualParameter is created in group param expansion, the raw name is the
	// bare param name
	private String rawName; 
	// name with modifiers etc stripped off, this can be prefixed with grp param
	private String baseName; 
	private String grpName;
	private String modifier;
	private SearchParameter.Modifier enumModifier;
	private List<String> comparator;
	private List<SearchParameter.Comparator> enumComparator;
	// original values from query : may with comparator
	private List<String> values; 
	private Class<?> type;
	// value(s) in java object as indicated by type
	// if the ith entry is mv, then it is replaced by Map of mangled param name and resolved value;
	private List<Object> valObjs; 
	
	public CompositeParameter(String rawName, List<String> values) {
		super();
		this.rawName = rawName;
		this.values = values;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public SearchParameter.Modifier getEnumModifier() {
		return enumModifier;
	}

	public void setEnumModifier(SearchParameter.Modifier enumModifier) {
		this.enumModifier = enumModifier;
	}

	public List<String> getComparator() {
		return comparator;
	}

	public void setComparator(List<String> comparator) {
		this.comparator = comparator;
	}

	public List<SearchParameter.Comparator> getEnumComparator() {
		return enumComparator;
	}

	public void setEnumComparator(List<SearchParameter.Comparator> enumComparator) {
		this.enumComparator = enumComparator;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValue(List<String> values) {
		this.values = values;
	}

	public List getValueObject() {
		return this.valObjs;
	}

	public void setValueObject(List values) {
		this.valObjs = values;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getRawName() {
		return rawName;
	}

	public void setRawName(String rawName) {
		this.rawName = rawName;
	}

	public String getGroupName() {
		return grpName;
	}

	public void setGroupName(String grpName) {
		this.grpName = grpName;
	}

	public boolean isPartOfGroup() {
		boolean ret = false;
		if (this.grpName != null) {
			ret = true;
		}
		return ret;
	}
	
	public String getCannonicalName() {
		return this.isPartOfGroup() ? this.getRawName() : this.getBaseName();
	}

	public void parse(SearchParameter sp) {
		setType(sp.getType());
		String[] parts = ResourceQueryUtils.parseParamName(rawName);
		setBaseName(parts[0]);
		String md = null;
		if (parts.length == 2) {
			SearchParameter.Modifier m = SearchParameterRegistry.getModifier(parts[1]);
			if (!sp.accept(m)) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName
						+ ", parameter:" + this.getBaseName() + ", does not accept [" + md + "]");
			}
			setModifier(md);
			setEnumModifier(m);
		}
		List<String> values = getValues();
		String[] value_parts = new String[2];
		List<Object> valObjs = null;
		List<String> comparatorStrs = new ArrayList<String>();
		List<SearchParameter.Comparator> comparators = new ArrayList<SearchParameter.Comparator>();
		
		if (Number.class.isAssignableFrom(this.getType())) {
			valObjs = new ArrayList<Object>();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				// for now only number and date can have comparator
				String realValStr = value;
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + this.getBaseName() + ", does not accept [" + comparatorStr + "]");
					}
				}
				else {
					// if comparator not present, assume 'eq' as default
					comparatorStrs.add("eq");
					comparators.add(SearchParameter.Comparator.EQ);
				}
				valObjs.add(ResourceQueryUtils.parseNumeric(this.getType(), realValStr));
			}
			setComparator(comparatorStrs);
			setEnumComparator(comparators);
			setValueObject(valObjs);
		} else if (Boolean.class.isAssignableFrom(this.getType())) {
			valObjs = new ArrayList<Object>();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				// for now only number and date can have comparator
				String realValStr = value;
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + this.getBaseName() + ", does not accept [" + comparatorStr + "]");
					}
				}
				else {
					// if comparator not present, assume 'eq' as default
					comparatorStrs.add("eq");
					comparators.add(SearchParameter.Comparator.EQ);
				}
				valObjs.add(Boolean.valueOf(realValStr));
			}
			setComparator(comparatorStrs);
			setEnumComparator(comparators);
			setValueObject(valObjs);
		} else if (Date.class.isAssignableFrom(this.getType())) {
			valObjs = new ArrayList<Object>();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				String realValStr = value;
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + this.getBaseName() + ", does not accept [" + comparatorStr + "]");
					}
				}
				else {
					// if comparator not present, assume 'eq' as default
					comparatorStrs.add("eq");
					comparators.add(SearchParameter.Comparator.EQ);
				}
				Date d = ResourceQueryUtils.parseDate(realValStr);
				if (d == null) {
					throw new IllegalArgumentException(
							"Query parameter:" + getBaseName() + " expect date value in the format of: "
									+ SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
				}
				valObjs.add(d);
			}
			setComparator(comparatorStrs);
			setEnumComparator(comparators);
			setValueObject(valObjs);
		} else {
			// for now assume it is a string - no comparator prefix
			valObjs = new ArrayList<Object>();
			valObjs.addAll(values);
			setValueObject(valObjs);
			// ToDo: need to fix in the current query builder logic 
			if (sp.getName().equals("given")) {
				setEnumModifier(SearchParameter.Modifier.CONTAINS);
			}
		}
	}

	public void setMV(int i, String n, String v) {
		Object value = this.getValueObject().get(i);
		Map<String, String> mv = null;
		if (value instanceof Map) {
			mv = (Map)value;
		}
		else {
			mv = new HashMap<String, String>();
			this.getValueObject().set(i, mv);
		}
		mv.put(n, v);
	}

}
