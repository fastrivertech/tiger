package com.frt.dr.service.query;

import java.util.Map;

import com.frt.dr.service.query.SearchParameter.Comparator;

public interface SearchParameter {
	public static final Map<String, Modifier> MODIFIERMAP = Map.ofEntries(
			Map.entry("missing", Modifier.MISSING),
			Map.entry("below", Modifier.BELOW),
			Map.entry("above", Modifier.ABOVE),
			Map.entry("exact", Modifier.EXACT),
			Map.entry("contains", Modifier.CONTAINS),
			Map.entry("text", Modifier.TEXT),
			Map.entry("not", Modifier.NOT),
			Map.entry("in", Modifier.IN),
			Map.entry("not-in", Modifier.NOT_IN),
			Map.entry("ofType", Modifier.OFTYPE),
			Map.entry("type", Modifier.TYPE),
			Map.entry("identifier", Modifier.IDENTIFIER)
		); 
	public static final Map<String, Comparator> COMPARATORMAP = Map.ofEntries(
			Map.entry("ap", Comparator.AP),
			Map.entry("eb", Comparator.EB),
			Map.entry("sa", Comparator.SA),
			Map.entry("le", Comparator.LE),
			Map.entry("lt", Comparator.LT),
			Map.entry("ge", Comparator.GE),
			Map.entry("gt", Comparator.GT),
			Map.entry("ne", Comparator.NE),
			Map.entry("eq", Comparator.EQ)
		); 
	public static enum Modifier {MISSING, BELOW, ABOVE, EXACT, CONTAINS, TEXT, NOT, IN, NOT_IN, OFTYPE, TYPE, IDENTIFIER};
	// for FHIR type: date, number, quantity
	public static enum Comparator {EQ, NE, GT, GE, LT, LE, SA, EB, AP};
	public String getName();
	public void setName(String name);
	public Class<?> getType();
	public void setType(Class<?> type);
	public Class<?> getEntityClass();
	public void setEntityClass(Class<?> clazz);
	public String getFieldName();
	public void setFieldName(String fldName);
	public String getExpression();
	public void setExpression(String expression);
	public String[] getBase(); // name of related resources: Patient, Practitioner
	public void setBase(String[] base);
	public boolean isMultipleAnd();
	public void setMultipleAnd(boolean b);
	public boolean isMultipleOr();
	public void setMultipleOr(boolean b);
	public Modifier[] getModifiers();
	public void setModifiers(Modifier[] modifiers);
	
	public Comparator[] getComparators();
	public void setComparators(Comparator[] comparators);

	public Modifier getModifier(String sm);
	public Comparator getComparator(String sc);
	public Comparator checkComparator(String value, String[] comparator);
	public boolean accept(Modifier m);
	public boolean accept(Comparator c);
}
