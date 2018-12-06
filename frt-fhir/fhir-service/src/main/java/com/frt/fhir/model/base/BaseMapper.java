package com.frt.fhir.model.base;

import java.util.List;

import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;

import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;

import ca.uhn.fhir.context.FhirContext;

public abstract class BaseMapper implements ResourceMapper {
	protected static String PAT_RS_HEAD = "{\"resourceType\": \"Patient\", \"id\": \"PAT1\",";
	protected static String PAT_RS_TAIL = "}";
	protected static String IDENTIFIER_BEGIN = "\"identifier\":[";
	protected static String IDENTIFIER_END = "]";

	protected ca.uhn.fhir.parser.JsonParser parser; // per mapper HAPI parser for json to object of HAPI type convert 

	public BaseMapper() {
		FhirContext context = FhirContext.forDstu3();
		parser = (ca.uhn.fhir.parser.JsonParser)context.newJsonParser();
	}
	
	@Override
	public abstract ResourceMapper from(Class source);

	@Override
	public abstract ResourceMapper to(Class target);

	@Override
	public abstract Object map(Object source) throws MapperException;

	protected org.hl7.fhir.dstu3.model.Reference getAssigner(String assigner) {
		// Patient.Identifier.assigner json string : e.g. "assigner": {"display": "Acme Healthcare"}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(assigner).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getAssigner();
	}

	protected org.hl7.fhir.dstu3.model.Period getPeriod(String period) {
		// Patient.Identifier.period json string : e.g. "period": {"start": "2001-05-06"}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(period).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getPeriod();
	}

	protected org.hl7.fhir.dstu3.model.CodeableConcept getType(String type) {
		// Patient.Identifier.type json string : "type": {"coding": [{"system": "http://hl7.org/fhir/v2/0203","code": "MR"}]}
		StringBuilder sb = new StringBuilder();
		sb.append(this.PAT_RS_HEAD).append(type).append(this.PAT_RS_TAIL);
		org.hl7.fhir.dstu3.model.Patient pt = this.parser.parseResource(org.hl7.fhir.dstu3.model.Patient.class, sb.toString());
		return pt.getIdentifier().get(0).getType();
	}

	protected IdentifierUse getIdentifierUse(String use) {
		if (use==null||use.isEmpty()) {
			return IdentifierUse.NULL;
		} else {
			return IdentifierUse.valueOf(use.trim());
		}
	}

	/**
	 * Serialize a list of HAPI type into json arry string
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	protected <T extends org.hl7.fhir.dstu3.model.Type> String serializeToJson(List<T> obj, String fieldName) {
		StringBuilder sb = new StringBuilder();
		return null;
	}

	/**
	 * 
	 * @param obj - the HAPI instance of a Composite Type e.g. Period, Reference, Type etc.
	 * @param fieldName - Patient.Identifier field name (json), e.g. "period", "assigner", "type", etc.
	 * @return the corresponding json string;
	 */
	protected <T extends org.hl7.fhir.dstu3.model.Type> String serializeToJson(T obj, String fieldName) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append(fieldName).append(":{");
		if (obj instanceof org.hl7.fhir.dstu3.model.Reference) {
			// Identifier.assigner
			org.hl7.fhir.dstu3.model.Reference r = ((org.hl7.fhir.dstu3.model.Reference)obj);
			if (r.hasDisplay()) {
				sb.append("display:").append(r.getDisplay());
				first = false;
			}
			if (r.hasReference()) {
				if (first) {
					sb.append(",");
				}
				sb.append("reference:").append(r.getReference());
			}
//			if (r.hasIdentifier()) {
//				if (first) {
//					sb.append(",");
//				}
//				sb.append("identifier:").append(serializeToJson(r.getIdentifier(), "identifier"));
//			}
		}
		else if (obj instanceof org.hl7.fhir.dstu3.model.Period) {
			// Identifier.period
			org.hl7.fhir.dstu3.model.Period p = ((org.hl7.fhir.dstu3.model.Period)obj); 
			if (p.hasStart()) {
				sb.append("start:").append(p.getStart().toString());
			};
			if (p.hasEnd()) {
				if (p.hasStart()) {
					sb.append(",");
				}
				sb.append(p.getEnd().toString());
			}
		}
		else if (obj instanceof org.hl7.fhir.dstu3.model.CodeableConcept) {
			// Identifier.type
			org.hl7.fhir.dstu3.model.CodeableConcept c = ((org.hl7.fhir.dstu3.model.CodeableConcept)obj); 
			if (c.hasCoding()) {
				
			}
			if (c.hasText()) {
				
			}
		}
		else {
			throw new UnsupportedOperationException("Serialization of instance of type: " + obj.getClass().getCanonicalName() + " not supported yet.");
		}
		sb.append("}");
		return sb.toString();
	}

}
