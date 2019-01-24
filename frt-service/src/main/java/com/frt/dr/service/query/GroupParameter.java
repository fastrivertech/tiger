package com.frt.dr.service.query;

public class GroupParameter extends AbstractParameter {
	private String name;
	private Class<?> type; // java type of the parameter
	private String fldName; // corresponding attribute name in the resource JPA POJO
	private String[] base; 
	private Class<?> entityClazz; // the JPA entity POJO class
	private String expression;
	private Modifier[] modifiersApplicable;
	private Comparator[] comparatorsApplicable;
	private Boolean multipleAnd;
	private Boolean multipleOr;
	
	public GroupParameter(String name, String fldName, Class<?> type, String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.type = type;
		this.fldName = fldName;
		this.base = base;
		this.entityClazz = entityClazz;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getFldName() {
		return fldName;
	}

	public void setFldName(String fldName) {
		this.fldName = fldName;
	}

	public Class<?> getEntityClazz() {
		return entityClazz;
	}

	public void setEntityClazz(Class<?> entityClazz) {
		this.entityClazz = entityClazz;
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

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String[] getBase() {
		return base;
	}

	public void setBase(String[] base) {
		this.base = base;
	}

	public String getFieldName() {
		return fldName;
	}
	
	public void setFieldName(String fldName) {
		this.fldName = fldName;
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
}
