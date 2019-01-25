package com.frt.dr.service.query;

import java.util.List;

public class FieldParameter extends AbstractParameter {
	
	public FieldParameter(String name, String fldName, Class<?> type, String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.fldName = fldName;
		this.base = base;
		this.type = type;
		this.entityClazz = entityClazz;
	}

	public FieldParameter(String name, String fldName, Class<?> type, 
			List<Modifier> modifiersApplicable,
			List<Comparator> comparatorsApplicable,
			String[] base, Class<?> entityClazz) {
		super();
		this.name = name;
		this.fldName = fldName;
		this.modifiersApplicable = modifiersApplicable;
		this.comparatorsApplicable = comparatorsApplicable;
		this.base = base;
		this.type = type;
		this.entityClazz = entityClazz;
	}

}
