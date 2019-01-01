package com.frt.fhir.model.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.frt.dr.model.ResourceComplexType;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;
import com.frt.fhir.service.HAPIComplexTypesResource;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;

public abstract class BaseMapper implements ResourceMapper {
	private static Logger logger = Logger.getLog(BaseMapper.class.getName());
	private static Localization localizer = Localization.getInstance();
	protected static String CUST_RS_BEGIN = "{\"resourceType\": \"HAPIComplexTypesResource\",";
	protected static String CUST_RS_END = "}";
	protected static String PAT_RS_BEGIN = "{\"resourceType\": \"Patient\",";
	protected static String PAT_RS_END = "}";
	protected static String IDENTIFIER_TAG = "\"identifier\"";
	protected static String ADDRESS_TAG = "\"address\"";
	protected static String HUMANNAME_TAG = "\"name\"";
	protected static String PHOTO_TAG = "\"photo\"";
	protected static String CONTACT_TAG = "\"contact\"";
	protected static String TELECOM_TAG = "\"telecom\"";
	protected static String LINK_TAG = "\"link\"";
	protected static String COMMUNICATION_TAG = "\"communication\"";
	protected static String GENERALPRACTITIONER_TAG = "\"generalPractitioner\"";
	protected static String ANIMAL_TAG = "\"animal\"";
	private static String NV_PAIR_FORMAT = "\"{0}\":\"{1}\"";
	private static String NV_PAIR_FORMAT_ARRAY = "\"{0}\":{1}";
	private static String NV_PAIR_FORMAT_OBJ = "\"{0}\":{1}";
	private static String NV_SEP = ":";
	private static String VAL_DEL = ",";
	private static String ARRAY_BEGIN = "[";
	private static String ARRAY_END = "]";
	private static String OBJ_BEGIN = "{";
	private static String OBJ_END = "}";

	protected static Gson gconverter = new Gson();

	protected ca.uhn.fhir.parser.JsonParser parser; // per mapper HAPI parser for json to object of HAPI type convert
	protected JsonParser gparser = new JsonParser();

	public BaseMapper() {
		FhirContext context = FhirContext.forDstu3();
		parser = (ca.uhn.fhir.parser.JsonParser) context.newJsonParser();
		context.registerCustomType(HAPIComplexTypesResource.class);
	}
	
	@Override
	public abstract ResourceMapper from(Class source);

	@Override
	public abstract ResourceMapper to(Class target);

	@Override
	public abstract Object map(Object source) throws MapperException;

	/**
	 * 
	 * @param type - one of the HAPI FHIR complex types
	 * @param message - json string of the complex type as indicated by <code>type</code>
	 * @return the HAPI object of the type;
	 */
	public <T extends org.hl7.fhir.dstu3.model.Type> T parseComplexType(Class<T> type, String message) {
		return (T)parseComplexType(type.getSimpleName().toLowerCase(), message);
	}

	/**
	 * 
	 * @param type - one of the HAPI FHIR complex types
	 * @param message - json string of array of element of complex type as indicated by <code>type</code> 
	 * @return the array of elements of HAPI complex type
	 */
	public <T extends org.hl7.fhir.dstu3.model.Type> List<T> parseComplexTypeArray(Class<T> type, String message) {
		return (List)parseComplexType(type.getSimpleName().toLowerCase()+"Array", message);
	}

	private Object parseComplexType(String fieldName, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(CUST_RS_BEGIN).append("\"").append(fieldName).append("\":").append(message).append(CUST_RS_END);
		HAPIComplexTypesResource rs = this.parser.parseResource(HAPIComplexTypesResource.class, sb.toString());
		Field field=null;
		try {
			field = rs.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			throw new MapperException(e);
		}
		field.setAccessible(true);
		Object value = null;
		try {
			value = field.get(rs);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new MapperException(e);
		}
		return value;
	}

//	public <T extends org.hl7.fhir.dstu3.model.Type> Map<Class<T>, Object> parseComplexTypeArray(Class<T>[] types, String[] messages, Boolean[] isArray) {
//		StringBuilder sb = new StringBuilder();
//		Map<Class<T>, Object> ret = new HashMap<Class<T>, Object>();
//		sb.append(CUST_RS_BEGIN);
//		boolean first = true;
//		for (int i=0; i<types.length;i++) {
//			if (!first) {
//				sb.append(VAL_DEL);
//			}
//			if (isArray[i]) {
//				sb.append("\"").append(types[i].getSimpleName().toLowerCase()).append("Array").append("\":").append(messages[i]);
//			}
//			else {
//				sb.append("\"").append(types[i].getSimpleName().toLowerCase()).append("\":").append(messages[i]);
//			}
//		}
//		sb.append(CUST_RS_END);
//		HAPIComplexTypesResource rs = this.parser.parseResource(HAPIComplexTypesResource.class, sb.toString());
//		return ret;
//	}

	private Object invokeGetter(HAPIComplexTypesResource rs, String attr) {
		Object ret = null;
		Method getter = null;
		try {
			getter = rs.getClass().getMethod("get"+attr);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (getter!=null) {
			try {
				ret = getter.invoke(rs);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static String resourceToJson(com.frt.dr.model.DomainResource frtResource) {
		final StringBuilder sb = new StringBuilder();
		if (frtResource instanceof com.frt.dr.model.base.Patient) {
			com.frt.dr.model.base.Patient p = (com.frt.dr.model.base.Patient) frtResource;
			sb.append(PAT_RS_BEGIN);

			addNVpair(sb, "id", p.getPatientId());
			addNVpair(sb, "active", p.getActive());
			addNVpair(sb, "gender", p.getGender());
			
			if (p.getBirthDate()!=null) {
				addNVpair(sb, "birthDate", (new SimpleDateFormat("yyyy-MM-dd")).format(p.getBirthDate()));
			}
			
			addNVpair(sb, "deceasedBoolean", p.getDeceasedBoolean());
			
			if (p.getDeceasedDateTime() != null) {
				addNVpair(sb, "deceasedDateTime", (new SimpleDateFormat("yyyy-MM-dd"))
						.format(new java.util.Date(p.getDeceasedDateTime().getTime())));
			}

			appendArray(sb, p.getIdentifiers(), IDENTIFIER_TAG);
			appendArray(sb, p.getNames(), HUMANNAME_TAG);
			appendArray(sb, p.getAddresses(), ADDRESS_TAG);
			appendArray(sb, p.getTelecoms(), TELECOM_TAG);

			if (p.getMaritalStatus() != null) {
				addNVpairObject(sb, "maritalStatus", componentToJson(p.getMaritalStatus()));
			}

			addNVpair(sb, "multipleBirthBoolean", p.getMultipleBirthBoolean());
			addNVpair(sb, "multipleBirthInteger", p.getMultipleBirthInteger());

			appendArray(sb, p.getPhotos(), PHOTO_TAG);
			appendArray(sb, p.getContacts(), CONTACT_TAG);

			if (p.getAnimal()!=null) {
				addNVpairObject(sb, "animal", componentToJson(p.getAnimal()));
			}

			appendArray(sb, p.getCommunications(), COMMUNICATION_TAG);
			appendArray(sb, p.getGeneralPractitioners(), GENERALPRACTITIONER_TAG);

			if (p.getManagingOrganization() != null) {
				addNVpairObject(sb, "managingOrganization", componentToJson(p.getManagingOrganization()));
			}

			appendArray(sb, p.getLinks(), LINK_TAG);

			sb.append(PAT_RS_END);
		}
		return sb.toString();
	}

	public static String componentToJson(ResourceComplexType frtComponent) {
		StringBuilder sb = new StringBuilder();
		sb.append(OBJ_BEGIN);
		if (frtComponent instanceof com.frt.dr.model.base.PatientAddress) {
			com.frt.dr.model.base.PatientAddress component = (com.frt.dr.model.base.PatientAddress) frtComponent;
			addNVpair(sb, "use", component.getUse());
			addNVpair(sb, "type", component.getType());
			addNVpair(sb, "text", component.getTxt());
			addNVpairArray(sb, "line", component.getLine());
			addNVpair(sb, "city", component.getCity());
			addNVpair(sb, "district", component.getDistrict());
			addNVpair(sb, "state", component.getState());
			addNVpair(sb, "country", component.getCountry());
			addNVpair(sb, "postalCode", component.getPostalcode());
			addNVpairObject(sb, "period", component.getPeriod());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientIdentifier) {
			com.frt.dr.model.base.PatientIdentifier component = (com.frt.dr.model.base.PatientIdentifier) frtComponent;
			addNVpair(sb, "use", component.getUse());
			addNVpair(sb, "system", component.getSystem());
			addNVpair(sb, "value", component.getValue());
			addNVpairObject(sb, "type", component.getType());
			addNVpairObject(sb, "period", component.getPeriod());
			addNVpairObject(sb, "assigner", component.getAssigner());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientHumanName) {
			com.frt.dr.model.base.PatientHumanName component = (com.frt.dr.model.base.PatientHumanName) frtComponent;
			addNVpair(sb, "use", component.getUse());
			addNVpair(sb, "text", component.getTxt());
			addNVpair(sb, "family", component.getFamily());
			addNVpairArray(sb, "given", component.getGiven());
			addNVpairArray(sb, "prefix", component.getPrefix());
			addNVpairArray(sb, "suffix", component.getSuffix());
			addNVpairObject(sb, "period", component.getPeriod());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientCodeableConcept) {
			com.frt.dr.model.base.PatientCodeableConcept component = (com.frt.dr.model.base.PatientCodeableConcept) frtComponent;
			addNVpairArray(sb, "coding", component.getCoding());
			addNVpair(sb, "text", component.getTxt());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientReference) {
			com.frt.dr.model.base.PatientReference component = (com.frt.dr.model.base.PatientReference) frtComponent;
			addNVpair(sb, "reference", component.getReference());
			addNVpair(sb, "display", component.getDisplay());
			addNVpairObject(sb, "identifier", component.getIdentifier());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientAttachment) {
			com.frt.dr.model.base.PatientAttachment component = (com.frt.dr.model.base.PatientAttachment) frtComponent;
			addNVpair(sb, "contentType", component.getContenttype());
			addNVpair(sb, "language", component.getLanguage());
			addNVpair(sb, "title", component.getTitle());
			addNVpair(sb, "url", component.getUrl());
			addNVpair(sb, "size", component.getSize());
			if (component.getCreation()!=null) {
				addNVpair(sb, "creation", (new SimpleDateFormat("yyyy-MM-dd"))
				.format(new java.util.Date(component.getCreation().getTime())));
			}
			addNVpair(sb, "data", component.getData());
			addNVpair(sb, "hash", component.getHash());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientContactPoint) {
			com.frt.dr.model.base.PatientContactPoint component = (com.frt.dr.model.base.PatientContactPoint) frtComponent;
			addNVpair(sb, "use", component.getUse());
			addNVpair(sb, "system", component.getSystem());
			addNVpair(sb, "value", component.getValue());
			addNVpair(sb, "rank", component.getRank());
			addNVpairObject(sb, "period", component.getPeriod());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientContact) {
			com.frt.dr.model.base.PatientContact component = (com.frt.dr.model.base.PatientContact) frtComponent;
			addNVpairArray(sb, "relationship", component.getRelationship());
			addNVpairObject(sb, "name", component.getName());
			addNVpairArray(sb, "telecom", component.getTelecom());
			addNVpairObject(sb, "address", component.getAddress());
			addNVpair(sb, "gender", component.getGender());
			addNVpairObject(sb, "organization", component.getOrganization());
			addNVpairObject(sb, "period", component.getPeriod());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientAnimal) {
			com.frt.dr.model.base.PatientAnimal component = (com.frt.dr.model.base.PatientAnimal) frtComponent;
			addNVpairObject(sb, "bread", component.getBreed());
			addNVpairObject(sb, "species", component.getSpecies());
			addNVpairObject(sb, "genderstatus", component.getGenderStatus());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientCommunication) {
			com.frt.dr.model.base.PatientCommunication component = (com.frt.dr.model.base.PatientCommunication) frtComponent;
			addNVpairObject(sb, "language", component.getLanguage());
			addNVpair(sb, "preferred", component.getPreferred());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientLink) {
			com.frt.dr.model.base.PatientLink component = (com.frt.dr.model.base.PatientLink) frtComponent;
			addNVpairObject(sb, "other", component.getOther());
			addNVpair(sb, "type", component.getType());
		} else {
			throw new UnsupportedOperationException("Convert instance of composite type: "
					+ frtComponent.getClass().getCanonicalName() + " not supported.");
		}
		sb.append(OBJ_END);
		return sb.toString();
	}

	public static void addNVpair(StringBuilder sb, String n, Object v) {
		if (v != null) {
			if (!endingInComma(sb) && !firstInArray(sb) && !firstInObject(sb)) {
				sb.append(VAL_DEL);
			}
			sb.append(MessageFormat.format(NV_PAIR_FORMAT, n, v));
		}
	}

	public static void addNVpairArray(StringBuilder sb, String n, Object v) {
		if (v != null) {
			if (!endingInComma(sb) && !firstInArray(sb) && !firstInObject(sb)) {
				sb.append(VAL_DEL);
			}
			sb.append(MessageFormat.format(NV_PAIR_FORMAT_ARRAY, n, v));
		}
	}

	public static void addNVpairObject(StringBuilder sb, String n, Object v) {
		if (v != null) {
			if (!endingInComma(sb) && !firstInArray(sb) && !firstInObject(sb)) {
				sb.append(",");
			}
			sb.append(MessageFormat.format(NV_PAIR_FORMAT_OBJ, n, v));
		}
	}

	public static boolean endingInComma(StringBuilder sb) {
		boolean endInComma = false;
		if (sb.length() > 0) {
			char ch = sb.charAt(sb.length() - 1);
			if (ch == ',') {
				endInComma = true;
			}
		}
		return endInComma;
	}

	public static boolean firstInArray(StringBuilder sb) {
		boolean firstInArray = false;
		if (sb.length() > 0) {
			char ch = sb.charAt(sb.length() - 1);
			if (ch == '[') {
				firstInArray = true;
			}
		}
		return firstInArray;
	}

	public static boolean firstInObject(StringBuilder sb) {
		boolean firstInObject = false;
		if (sb.length() > 0) {
			char ch = sb.charAt(sb.length() - 1);
			if (ch == '{') {
				firstInObject = true;
			}
		}
		return firstInObject;
	}

	public static <T> void appendArray(StringBuilder sb, List<T> components, String tag) {
		if (components != null && components.size() > 0) {
			if (!endingInComma(sb)) {
				sb.append(VAL_DEL);
			}
			sb.append(tag).append(NV_SEP).append(ARRAY_BEGIN);
			components.forEach(e -> {
				if (!firstInArray(sb)) {
					sb.append(VAL_DEL);
				}
				sb.append(componentToJson((ResourceComplexType)e));
			});
			sb.append(ARRAY_END);
		}
	}

}
