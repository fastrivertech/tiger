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
 * FieldParameter class
 * @author jfu
 */
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
