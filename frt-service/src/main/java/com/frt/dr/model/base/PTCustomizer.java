package com.frt.dr.model.base;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

public class PTCustomizer implements DescriptorCustomizer {
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
