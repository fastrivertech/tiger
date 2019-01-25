package com.frt.dr.service.query;

import java.util.List;

public class GroupParameter extends AbstractParameter {
	private String[] parameters; // search parameters (corresponding to fields in the entity) that are part of the group param search 
	
	public GroupParameter(String name, String fldName, Class<?> type, String[] params, String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.type = type;
		this.fldName = fldName;
		this.parameters = params;
		this.base = base;
		
		this.entityClazz = entityClazz;
	}

	public GroupParameter(String name, String fldName, Class<?> type, String[] params, 
			List<Modifier> modifiersApplicable,
			List<Comparator> comparatorsApplicable,
			String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.type = type;
		this.fldName = fldName;
		this.parameters = params;
		this.base = base;
		this.modifiersApplicable = modifiersApplicable;
		this.comparatorsApplicable = comparatorsApplicable;
		this.entityClazz = entityClazz;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

}
