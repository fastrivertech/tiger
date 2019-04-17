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
package com.frt.fhir.model;

import java.util.List;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Attachment;
//import org.hl7.fhir.r4.model.BaseExtension;
//import org.hl7.fhir.r4.model.BaseNarrative;
//import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Contributor;
import org.hl7.fhir.r4.model.DataRequirement;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.ParameterDefinition;
import org.hl7.fhir.r4.model.Period;
//import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.SampledData;
import org.hl7.fhir.r4.model.Signature;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.TriggerDefinition;
import org.hl7.fhir.r4.model.UsageContext;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**
 * This is an example of a custom resource that also uses a custom datatype.
 * 
 * Note that we are extensing DomainResource for an STU3 resource. For DSTU2 it
 * would be BaseResource.
 */
/**
 * FHIR Datatypes: Complex Types:
 * 
 * (X)Address, (X)Annotation, (X)Attachment, 
 * ( )BaseExtension, ( )BaseNarrative, ( )BaseReference, 
 * (X)CodeableConcept, (X)Coding, (X)ContactDetail, 
 * (X)ContactPoint, (X)Contributor, (X)DataRequirement, 
 * (X)Dosage, (X)ElementDefinition, (X)HumanName, 
 * (X)Identifier, (X)Meta, (X)ParameterDefinition, 
 * (X)Period, (X)PrimitiveType, (X)Quantity, 
 * (X)Range, (X)Ratio, (X)RelatedArtifact, 
 * (X)SampledData, (X)Signature, (X)Timing, 
 * (X)TriggerDefinition, (X)UsageContext
 * 
 * @author JIMFUQIAN
 *
 */
@ResourceDef(name = "ComplexTypesResource", profile = "http://hl7.org/fhir/profiles/custom-resource")
public class ComplexTypesResource extends DomainResource {
	private static final long serialVersionUID = 1L;
	
	/**
	 * the resource has all the fields of FHIR complex types as its attributes. These fields each has 
	 * a corresponding List<T> where T is the FHIR complex type.
	 * 
	 * the purpose of this resource is to parse json string of FHIR complex type instance
	 * and stored in the resource as a child.
	 */

//	@Child(name = "baseextension", min = 0, max = 1)
//	private BaseExtension baseextension;
//	
//	@Child(name = "basereference", min = 0, max = 1)
//	private BaseReference basereference;
//	
//	@Child(name = "basenarrative", min = 0, max = 1)
//	private BaseNarrative basenarrative;

	@Child(name = "identifier", min = 0, max = 1)
	private Identifier identifier;
	
	@Child(name = "address", min = 0, max = 1)
	private Address address;
	
	@Child(name = "annotation", min = 0, max = 1)
	private Annotation annotation;

	@Child(name = "attachment", min = 0, max = 1)
	private Attachment attachment;
	
	@Child(name = "codeableconcept", min = 0, max = 1)
	private CodeableConcept codeableconcept;
	
	@Child(name = "coding", min = 0, max = 1)
	private Coding coding;

	@Child(name = "contactdetail", min = 0, max = 1)
	private ContactDetail contactdetail;

	@Child(name = "contactpoint", min = 0, max = 1)
	private ContactPoint contactpoint;
	
	@Child(name = "contributor", min = 0, max = 1)
	private Contributor contributor;
	
	@Child(name = "datarequirement", min = 0, max = 1)
	private DataRequirement datarequirement;
	
	@Child(name = "dosage", min = 0, max = 1)
	private Dosage dosage;
	
	@Child(name = "elementdefinition", min = 0, max = 1)
	private ElementDefinition elementdefinition;
	
	@Child(name = "parameterdefinition", min = 0, max = 1)
	private ParameterDefinition parameterdefinition;
	
	@Child(name = "name", min = 0, max = 1)
	private HumanName humanname;

	@Child(name = "period", min = 0, max = 1)
	private Period period;
	
	@Child(name = "quantity", min = 0, max = 1)
	private Quantity quantity;

	@Child(name = "range", min = 0, max = 1)
	private Range range;

	@Child(name = "ratio", min = 0, max = 1)
	private Ratio ratio;
	
	@Child(name = "relatedartifact", min = 0, max = 1)
	private RelatedArtifact relatedartifact;
	
	@Child(name = "sampleddata", min = 0, max = 1)
	private SampledData sampleddata;
	
	@Child(name = "signature", min = 0, max = 1)
	private Signature signature;
	
	@Child(name = "timing", min = 0, max = 1)
	private Timing timing;
	
	@Child(name = "tiggerdefinition", min = 0, max = 1)
	private TriggerDefinition tiggerdefinition;
	
	@Child(name = "usagecontext", min = 0, max = 1)
	private UsageContext usagecontext;
	
	@Child(name = "reference", min = 0, max = 1)
	private Reference reference;
	
	/**
	 * arrays of complex type
	 */
	
//	@Child(name = "baseextensionArray", min = 0, max = Child.MAX_UNLIMITED)
//	private List<BaseExtension> baseextensionArray;
//	
//	@Child(name = "basereferenceArray", min = 0, max = Child.MAX_UNLIMITED)
//	private List<BaseReference> basereferenceArray;
//
//	@Child(name = "basenarrativeArray", min = 0, max = Child.MAX_UNLIMITED)
//	private List<BaseNarrative> basenarrativeArray;

	@Child(name = "identifierArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Identifier> identifierArray;

	@Child(name = "addressArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Address> addressArray;

	@Child(name = "annotationArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Annotation> annotationArray;

	@Child(name = "attachmentArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Attachment> attachmentArray;
	
	@Child(name = "codeableconceptArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<CodeableConcept> codeableconceptArray;
	
	@Child(name = "codingArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Coding> codingArray;

	@Child(name = "contactpointArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<ContactPoint> contactpointArray;
	
	@Child(name = "contactdetailArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<ContactDetail> contactdetailArray;

	@Child(name = "contributorArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Contributor> contributorArray;
	
	@Child(name = "datarequirementArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<DataRequirement> datarequirementArray;
	
	@Child(name = "dosageArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Dosage> dosageArray;
	
	@Child(name = "elementdefinitionArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<ElementDefinition> elementdefinitionArray;

	@Child(name = "parameterdefinitionArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<ParameterDefinition> parameterdefinitionArray;

	@Child(name = "metaArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Meta> metaArray;

	@Child(name = "nameArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<HumanName> nameArray;
	
	@Child(name = "periodArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Period> periodArray;
	
	@Child(name = "quantityArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Quantity> quantityArray;

	@Child(name = "rangeArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Range> rangeArray;

	@Child(name = "ratioArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Ratio> ratioArray;
	
	@Child(name = "relatedartifactArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<RelatedArtifact> relatedartifactArray;
	
	@Child(name = "sampleddataArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<SampledData> sampleddataArray;
	
	@Child(name = "signatureArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Signature> signatureArray;
	
	@Child(name = "timingArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Timing> timingArray;
	
	@Child(name = "tiggerdefinitionArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<TriggerDefinition> tiggerdefinitionArray;
	
	@Child(name = "usagecontextArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<UsageContext> usagecontextArray;
	
	@Child(name = "referenceArray", min = 0, max = Child.MAX_UNLIMITED)
	private List<Reference> referenceArray;

	@Override
	public ComplexTypesResource copy() {
		ComplexTypesResource retVal = new ComplexTypesResource();
		super.copyValues(retVal);
		return retVal;
	}

	@Override
	public ResourceType getResourceType() {
		return null;
	}

}
