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

import java.util.List;

/**
 * SearchParameter interface
 * @author jfu
 */
public interface SearchParameter {
	
	public static enum Modifier {
		MISSING("missing"), 
		BELOW("below"), 
		ABOVE("above"), 
		EXACT("exact"), 
		CONTAINS("contains"), 
		TEXT("text"), 
		NOT("not"), 
		IN("in"), 
		NOT_IN("not-in"), 
		OFTYPE("ofType"), 
		TYPE("type"), 
		IDENTIFIER("identifier");
		
        private String modifier;
        
        private Modifier(String m) {
            this.modifier = m;
        }
	};
	
	// for FHIR type: date, number, quantity
	public static enum Comparator {
		EQ("eq"), 
		NE("ne"), 
		GT("gt"), 
		GE("ge"), 
		LT("lt"), 
		LE("le"), 
		SA("sa"), 
		EB("eb"), 
		AP("ap");
		
        private String comparator;
        
        private Comparator(String c) {
            this.comparator = c;
        }
	};

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
	public List<Modifier> getModifiersApplicable();
	public void setModifiersApplicable(List<Modifier> modifiers);
	
	public List<Comparator> getComparatorsApplicable();
	public void setComparatorsApplicable(List<Comparator> comparators);

	public boolean accept(Modifier m);
	public boolean accept(Comparator c);
	
}
