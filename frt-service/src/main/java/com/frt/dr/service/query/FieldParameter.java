package com.frt.dr.service.query;

public class FieldParameter extends AbstractParameter {
	private String name; // parameter name - end user visible
	private String fldname; // JPA POJO attribute name
	private Class<?> type;
	private String[] base;
	private String expression;
	private Modifier[] modifiersApplicable;
	private Comparator[] comparatorsApplicable;
	private Boolean multipleAnd;
	private Boolean multipleOr;
	private Class<?> entityClazz;
	
	public FieldParameter(String name, String fldName, Class<?> type, String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.fldname = fldName;
		this.base = base;
		this.type = type;
		this.entityClazz = entityClazz;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFieldName() {
		return fldname;
	}

	public void setFieldName(String fldname) {
		this.fldname = fldname;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExpression(String expression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isMultipleAnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMultipleOr() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Modifier[] getModifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setModifiers(Modifier[] modifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Comparator[] getComparators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComparators(Comparator[] comparators) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Modifier getModifier(String sm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator getComparator(String sc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean accept(Modifier m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accept(Comparator c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMultipleAnd(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMultipleOr(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEntityClass(Class<?> clazz) {
		// TODO Auto-generated method stub
		
	}
	public String getFldname() {
		return fldname;
	}

	public void setFldname(String fldname) {
		this.fldname = fldname;
	}

	public Modifier[] getModifiersApplicable() {
		return modifiersApplicable;
	}

	public void setModifiersApplicable(Modifier[] modifiersApplicable) {
		this.modifiersApplicable = modifiersApplicable;
	}

	public Comparator[] getComparatorsApplicable() {
		return comparatorsApplicable;
	}

	public void setComparatorsApplicable(Comparator[] comparatorsApplicable) {
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

	public Class<?> getEntityClazz() {
		return entityClazz;
	}

	public void setEntityClazz(Class<?> entityClazz) {
		this.entityClazz = entityClazz;
	}

}
