/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.model;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

public class RelationMappingCustomizer implements DescriptorCustomizer {
	
	@Override
	public void customize(ClassDescriptor descriptor) throws Exception {
		OneToManyMapping mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("generalPractitioners");
		ExpressionBuilder eb = new ExpressionBuilder(mapping.getReferenceClass());
		Expression fkExp = eb.getField("RESOURCE_ID").equal(eb.getParameter("RESOURCE_ID"));
		Expression pathExp = eb.getField("PATH").equal("Patient.generalPractitioner");
		mapping.setSelectionCriteria(fkExp.and(pathExp));
		
		OneToOneMapping mapping2 = (OneToOneMapping)descriptor.getMappingForAttributeName("managingOrganization");
		ExpressionBuilder eb2 = new ExpressionBuilder(mapping2.getReferenceClass());
		Expression fkExp2 = eb2.getField("RESOURCE_ID").equal(eb2.getParameter("RESOURCE_ID"));
		Expression pathExp2 = eb2.getField("PATH").equal("Patient.managingOrganization");
		mapping2.setSelectionCriteria(fkExp2.and(pathExp2));
	}
}
