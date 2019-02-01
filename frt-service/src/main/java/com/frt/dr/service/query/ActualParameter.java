package com.frt.dr.service.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActualParameter {
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
	// value(s) in java objet as indocated by type
	private List<Object> valObjs; 

	public ActualParameter(String rawName, List<String> values) {
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
	
	public void parse(SearchParameter sp) {
		String[] parts = parseParamName(rawName);
		String bn = parts[0];
		bn = bn.trim();
		String md = null;
		setType(sp.getType());

		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed query parameter: " + rawName + ", expects: <name> or <name>:<modifier>");
		}
		
		if (parts.length == 2) {
			md = parts[1];
			md = md.trim();
			SearchParameter.Modifier m = SearchParameterRegistry.getModifier(md);
			if (m == null) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName);
			}
			if (!sp.accept(m)) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName
						+ ", parameter:" + bn + ", does not accept [" + md + "]");
			}
			setModifier(md);
			setEnumModifier(m);
		}
		
		setBaseName(bn.trim());
		
		// process comparator on value side
		Class<?> t = sp.getType();
		List values = getValues();
		String[] value_parts = new String[2];
		List valObjs = values;
		List<String> comparatorStrs = new ArrayList<String>();
		List<SearchParameter.Comparator> comparators = new ArrayList<SearchParameter.Comparator>();
		
		if (Number.class.isAssignableFrom(t)) {
			valObjs = new ArrayList();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				// for now only number and date can have comparator
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				String realValStr = value;
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + bn + ", does not accept [" + comparatorStr + "]");
					}
				}
				valObjs.add(parseNumeric(t, realValStr));
			}
			setComparator(comparatorStrs);
			setEnumComparator(comparators);
			setValueObject(valObjs);
		} else if (Boolean.class.isAssignableFrom(t)) {
			valObjs = new ArrayList();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				// for now only number and date can have comparator
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				String realValStr = value;
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + bn + ", does not accept [" + comparatorStr + "]");
					}
				}
				valObjs.add(Boolean.valueOf(realValStr));
			}
			setComparator(comparatorStrs);
			setEnumComparator(comparators);
			setValueObject(valObjs);
		} else if (Date.class.isAssignableFrom(t)) {
			valObjs = new ArrayList();
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.get(i);
				SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
				String realValStr = value;
				if (c != null) {
					String comparatorStr = value_parts[0];
					if (sp.accept(c)) {
						comparatorStrs.add(comparatorStr);
						comparators.add(c);
						realValStr = value_parts[1];
					} else {
						throw new IllegalArgumentException(
								"Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
										+ ", parameter:" + bn + ", does not accept [" + comparatorStr + "]");
					}
				}
				Date d = parseDate(realValStr);
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
			// for now assume it is a string - no comparator
			setValueObject(valObjs);
		}
	}

	private Object parseNumeric(Class<?> type, String value) {
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

	private String getPlaceHolder(int i, ActualParameter ap) {
		StringBuilder sb = new StringBuilder(ap.getBaseName());
		String m = ap.getModifier()!=null?ap.getModifier():"";
		String c = ap.getComparator()!=null&&ap.getComparator().size()>0?ap.getComparator().get(i):"";
		sb.append("_").append(m).append(c).append("_").append(i);
		return sb.toString();
	}

	/**
	 * helper - gen SQL String column LIKE pattern
	 * 
	 * @param value
	 * @return
	 */
	private String convertToLikePattern(String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("%");
		sb.append(value);
		sb.append("%");
		return sb.toString();
	}

	private static String[] parseParamName(String n) {
		String[] parts = n.split(SearchParameterRegistry.PARAM_MODIFIER_DELIMETER);
		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed parameter name: " + n + ", parameter name format: <name> or <name>:<modifier>.");
		}
		return parts;
	}

	private Date parseDate(String value) {
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
