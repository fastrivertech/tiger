package com.frt.fhir.model.base;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.frt.dr.model.ResourceComponent;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientAddress;
import com.frt.dr.model.base.PatientCodeableConcept;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientIdentifier;
import com.frt.fhir.model.MapperException;
import com.frt.fhir.model.ResourceMapper;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;

public abstract class BaseMapper implements ResourceMapper {
	private static Logger logger = Logger.getLog(PatientAddressMapper.class.getName());
	private static Localization localizer = Localization.getInstance();
	protected static String PAT_RS_BEGIN = "{\"resourceType\": \"Patient\",";
	protected static String PAT_RS_END = "}";
	protected static String IDENTIFIER_TAG = "\"identifier\"";
	protected static String ADDRESS_TAG = "\"address\"";
	protected static String HUMANNAME_TAG = "\"name\"";
	protected static String PHOTO_TAG = "\"photo\"";
	protected static String TELECOM_TAG = "\"telecom\"";
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
	}

	@Override
	public abstract ResourceMapper from(Class source);

	@Override
	public abstract ResourceMapper to(Class target);

	@Override
	public abstract Object map(Object source) throws MapperException;

	public static void toCodingArray(StringBuilder sb, PatientCodeableConcept c) {
		String[] codes = gconverter.fromJson(c.getCoding_code(), String[].class);
		String[] systems = gconverter.fromJson(c.getCoding_system(), String[].class);
		String[] versions = gconverter.fromJson(c.getCoding_version(), String[].class);
		String[] displays = gconverter.fromJson(c.getCoding_display(), String[].class);
		String[] selects = gconverter.fromJson(c.getCoding_userselected(), String[].class);
		if (codes.length > 0) {
			// per the encoding algorithm, codes.length == systems.length == version.length
			// == displays.length = selects.length
			JsonArray codings = new JsonArray();
			for (int i = 0; i < codes.length; i++) {
				JsonObject obj = new JsonObject();
				if (codes[i] != null && !codes[i].isEmpty()) {
					obj.addProperty("code", codes[i]);
				}
				if (systems[i] != null && !systems[i].isEmpty()) {
					obj.addProperty("system", systems[i]);
				}
				if (versions[i] != null && !versions[i].isEmpty()) {
					obj.addProperty("version", versions[i]);
				}
				if (displays[i] != null && !displays[i].isEmpty()) {
					obj.addProperty("display", displays[i]);
				}
				if (selects[i] != null && !selects[i].isEmpty()) {
					obj.addProperty("userSelected", selects[i]);
				}
				codings.add(obj);
			}
			sb.append("\"coding\":").append(gconverter.toJson(codings));
		}
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

			if (p.getManagingOrganization() != null) {
				addNVpairObject(sb, "managingOrganization", componentToJson(p.getManagingOrganization()));
			}

			sb.append(PAT_RS_END);
		}
		return sb.toString();
	}

	public static String componentToJson(ResourceComponent frtComponent) {
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
			// by the way 0..* Coding object is encoded,
			// here we need to decode PATIENT_CODEABLECONCEPT columns
			// CODING_CODE, CODING_SYSTEM, CODING_VERSION, CODING_DISPLAY,
			// CODING_USERSELECTED
			// into json array of Coding object
			if (component.getCoding_code() != null) {
				BaseMapper.toCodingArray(sb, component);
			}
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
			addNVpair(sb, "creation", component.getCreation());
			addNVpair(sb, "data", component.getData());
			addNVpair(sb, "hash", component.getHash());
		} else if (frtComponent instanceof com.frt.dr.model.base.PatientContactPoint) {
			com.frt.dr.model.base.PatientContactPoint component = (com.frt.dr.model.base.PatientContactPoint) frtComponent;
			addNVpair(sb, "use", component.getUse());
			addNVpair(sb, "system", component.getSystem());
			addNVpair(sb, "value", component.getValue());
			addNVpair(sb, "rank", component.getRank());
			addNVpairObject(sb, "period", component.getPeriod());
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
				sb.append(componentToJson((ResourceComponent)e));
			});
			sb.append(ARRAY_END);
		}
	}

}
