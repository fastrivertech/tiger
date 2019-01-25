package com.frt.dr.service.query;

import java.util.List;

public abstract class AbstractParameter implements SearchParameter {
	protected String name;
	protected Class<?> type; // java type of the parameter
	protected String fldName; // corresponding attribute name in the resource JPA POJO
	protected String[] base;
	protected Class<?> entityClazz; // the JPA entity POJO class
	protected String expression;
	protected List<Modifier> modifiersApplicable;
	protected List<Comparator> comparatorsApplicable;
	protected Boolean multipleAnd;
	protected Boolean multipleOr;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFieldName() {
		return fldName;
	}

	public void setFieldName(String fldname) {
		this.fldName = fldname;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String[] getBase() {
		return base;
	}

	public void setBase(String[] base) {
		this.base = base;
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public boolean isMultipleAnd() {
		return this.multipleAnd;
	}

	@Override
	public boolean isMultipleOr() {
		return this.multipleOr;
	}

	@Override
	public boolean accept(Modifier m) {
		return this.modifiersApplicable.contains(m);
	}

	@Override
	public boolean accept(Comparator c) {
		return this.comparatorsApplicable.contains(c);
	}

	@Override
	public void setMultipleAnd(boolean b) {
		this.multipleAnd=b;
	}

	@Override
	public void setMultipleOr(boolean b) {
		this.multipleOr=b;
	}

	@Override
	public Class<?> getEntityClass() {
		return this.entityClazz;
	}

	@Override
	public void setEntityClass(Class<?> clazz) {
		this.entityClazz = clazz;
	}
	
	public String getFldName() {
		return fldName;
	}

	public void setFldName(String fldName) {
		this.fldName = fldName;
	}

	public List<Modifier> getModifiersApplicable() {
		return modifiersApplicable;
	}

	public void setModifiersApplicable(List<Modifier> modifiersApplicable) {
		this.modifiersApplicable = modifiersApplicable;
	}

	public List<Comparator> getComparatorsApplicable() {
		return comparatorsApplicable;
	}

	public void setComparatorsApplicable(List<Comparator> comparatorsApplicable) {
		this.comparatorsApplicable = comparatorsApplicable;
	}

	public Boolean getMultipleAnd() {
		return multipleAnd;
	}

	public void setMultipleAnd(Boolean multipleAnd) {
		this.multipleAnd = multipleAnd;
	}

	public Boolean getMultipleOr() {
		return multipleOr;
	}

	public void setMultipleOr(Boolean multipleOr) {
		this.multipleOr = multipleOr;
	}

}
