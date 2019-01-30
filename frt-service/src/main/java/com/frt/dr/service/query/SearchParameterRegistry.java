package com.frt.dr.service.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.frt.dr.model.Resource;
import com.frt.dr.model.ResourceComplexType;
import com.frt.dr.service.query.SearchParameter.Comparator;
import com.frt.dr.service.query.SearchParameter.Modifier;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SearchParameterRegistry {
	private static final String FHIR_SEARCH_PARAMETERS_JSON="search-parameters.json";
	private static final JsonParser parser = new JsonParser();
	private static volatile SearchParameterRegistry instance = null;
	private static JsonElement bunndle = null;
	
	public static final String PARAM_PREFIX_EQ = "eq";
	//	 * eq	the value for the parameter in the resource is equal to the provided value	the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_NE = "ne";
	//	 * ne	the value for the parameter in the resource is not equal to the provided value	the range of the search value does not fully contain the range of the target value
	public static final String PARAM_PREFIX_GT = "gt";
	//	 * gt	the value for the parameter in the resource is greater than the provided value	the range above the search value intersects (i.e. overlaps) with the range of the target value
	public static final String PARAM_PREFIX_LT = "lt";
	//	 * lt	the value for the parameter in the resource is less than the provided value	the range below the search value intersects (i.e. overlaps) with the range of the target value
	public static final String PARAM_PREFIX_GE = "ge";
	//	 * ge	the value for the parameter in the resource is greater or equal to the provided value	the range above the search value intersects (i.e. overlaps) with the range of the target value, or the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_LE = "le";
	//	 * le	the value for the parameter in the resource is less or equal to the provided value	the range below the search value intersects (i.e. overlaps) with the range of the target value or the range of the search value fully contains the range of the target value
	public static final String PARAM_PREFIX_SA = "sa";
	//	 * sa	the value for the parameter in the resource starts after the provided value	the range of the search value does not overlap with the range of the target value, and the range above the search value contains the range of the target value
	public static final String PARAM_PREFIX_EB = "eb";
	//	 * eb	the value for the parameter in the resource ends before the provided value	the range of the search value does overlap not with the range of the target value, and the range below the search value contains the range of the target value
	public static final String PARAM_PREFIX_AP = "ap";
	//	 * ap	the value for the parameter in the resource is approximately the same to the provided value.
	public static final String PARAM_DATE_FMT_yyyy_MM_dd = "yyyy-MM-dd";
	public static final String PARAM_DATE_FMT_yyyy_s_MM_s_dd = "yyyy/MM/dd";
	public static final String PARAM_DATE_FMT_dd_s_MM_s_yyyy = "dd/MM/yyyy";
	public static final String PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
	public static final DateFormat DF_DATE_FMT_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DF_DATE_FMT_yyyy_s_MM_s_dd = new SimpleDateFormat("yyyy/MM/dd");
	public static final DateFormat DF_DATE_FMT_dd_s_MM_s_yyyy = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat DF_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final DateFormat[] DF_FMT_SUPPORTED = new DateFormat[] {DF_DATE_FMT_yyyy_MM_dd, DF_DATE_FMT_yyyy_s_MM_s_dd, DF_DATE_FMT_dd_s_MM_s_yyyy, DF_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss};
	public static final String PARAM_MODIFIER_DELIMETER = ":";

	// join column(s) between resource table and complex type tables
	public static final Map<Class<?>, Map<Class<?>, String[]>> ENTITY_ENTITY_JOINATTS = Map.ofEntries(
			Map.entry(com.frt.dr.model.base.Patient.class, 
				Map.ofEntries(
					Map.entry(com.frt.dr.model.base.PatientHumanName.class, new String[] {"names"}),
					Map.entry(com.frt.dr.model.base.PatientIdentifier.class, new String[] {"identifiers"}),
					Map.entry(com.frt.dr.model.base.PatientAddress.class, new String[] {"addresses"}),
					Map.entry(com.frt.dr.model.base.PatientContact.class, new String[] {"contacts"}),
					Map.entry(com.frt.dr.model.base.PatientCommunication.class, new String[] {"communications"}),
					Map.entry(com.frt.dr.model.base.PatientContactPoint.class, new String[] {"telecoms"})
				)
			)
		);
	// entity to parameters lookup
	public static final Map<Class<?>, List<String>> ENTITY_SEARCH_PARAMETERS = Map.ofEntries(
			// convention: the first name, if present, is the group parameter name, an empty string "" - indicate a missing of group search parameter
			// for the entity
			Map.entry(com.frt.dr.model.base.Patient.class, Arrays.asList("___", "id", "active", "birthdate", "gender", "_text")),
			Map.entry(com.frt.dr.model.base.PatientHumanName.class, Arrays.asList("name", "given", "family", "prefix", "suffix")),
			Map.entry(com.frt.dr.model.base.PatientIdentifier.class, Arrays.asList("identifier", "use", "system", "value")),
			Map.entry(com.frt.dr.model.base.PatientAddress.class, Arrays.asList("address", "address-city", "address-state", "address-country", "address-postalcode", "address-district", "address-use")),
			Map.entry(com.frt.dr.model.base.PatientCodeableConcept.class, Arrays.asList("codeable-text")), // now only 'txt' column is varchar other coulumns are clob
			Map.entry(com.frt.dr.model.base.PatientCommunication.class, Arrays.asList("communication-language", "communication-preferred")),
			Map.entry(com.frt.dr.model.base.PatientContact.class, Arrays.asList("contact-name", "contact-relationship", "contact-gender", "contact-address", "contact-telecom")),
			Map.entry(com.frt.dr.model.base.PatientContactPoint.class, Arrays.asList("contactpoint-system", "contactpoint-value", "contactpoint-use", "contactpoint-rank")),
			Map.entry(com.frt.dr.model.base.PatientReference.class, Arrays.asList("reference-reference", "reference-display"))
		);

	// search parameter look up
	public static Map<String, SearchParameter> SUPPORTED_PARAMETERS = new HashMap<String, SearchParameter>();

	static {
		// contactpoint (patient telecom)
		SUPPORTED_PARAMETERS.put("telecom", new GroupParameter("telecom", "telecoms", String.class, new String[] {"contactpoint-system", "contactpoint-value", "contactpoint-use"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContactPoint.class));
		SUPPORTED_PARAMETERS.put("contactpoint-system", new FieldParameter("contactpoint-system", "cp_system", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContactPoint.class));
		SUPPORTED_PARAMETERS.put("contactpoint-value", new FieldParameter("contactpoint-value", "value", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContactPoint.class));
		SUPPORTED_PARAMETERS.put("contactpoint-use", new FieldParameter("contactpoint-use", "use", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContactPoint.class));
		SUPPORTED_PARAMETERS.put("contactpoint-rank", new FieldParameter("contactpoint-rank", "cp_rank", Integer.class, 
				Arrays.asList(),
				Arrays.asList(
						SearchParameter.Comparator.AP, 
						SearchParameter.Comparator.EB, 
						SearchParameter.Comparator.SA, 
						SearchParameter.Comparator.EQ, 
						SearchParameter.Comparator.NE, 
						SearchParameter.Comparator.LE, 
						SearchParameter.Comparator.LT, 
						SearchParameter.Comparator.GE, 
						SearchParameter.Comparator.GT 
						),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContactPoint.class));
		// contact - patient contacts (guardian, friends, parteners etc)
		SUPPORTED_PARAMETERS.put("contact", new GroupParameter("contact", "contacts", String.class, 
				new String[] {"contact-name", "contact-relationship", "contact-gender", "contact-address", "contact-telecom"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		SUPPORTED_PARAMETERS.put("contact-name", new FieldParameter("contact-name", "name", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		SUPPORTED_PARAMETERS.put("contact-relationship", new FieldParameter("contact-relationship", "relationship", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		SUPPORTED_PARAMETERS.put("contact-gender", new FieldParameter("contact-gender", "gender", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		SUPPORTED_PARAMETERS.put("contact-address", new FieldParameter("contact-address", "address", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		SUPPORTED_PARAMETERS.put("contact-telecom", new FieldParameter("contact-telecom", "telecom", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientContact.class));
		// codeable concept
		SUPPORTED_PARAMETERS.put("codeable", new GroupParameter("codeable", "maritalStatus", String.class, new String[] {"codeable-text"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientCodeableConcept.class));
		SUPPORTED_PARAMETERS.put("codeable-text", new FieldParameter("codeable-text", "txt", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientCodeableConcept.class));
		// reference
		SUPPORTED_PARAMETERS.put("reference", new GroupParameter("reference", "maritalStatus", String.class,
				new String[] {"reference-reference", "reference-display"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientReference.class));
		SUPPORTED_PARAMETERS.put("reference-reference", new FieldParameter("reference", "reference", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientReference.class));
		SUPPORTED_PARAMETERS.put("reference-display", new FieldParameter("display", "display", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientReference.class));
		// communication
		SUPPORTED_PARAMETERS.put("communication", new GroupParameter("communication", "communications", String.class,
				new String[] {"communication-language", "communication-preferred"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientCommunication.class));
		SUPPORTED_PARAMETERS.put("communication-language", new FieldParameter("communication-language", "language", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientCommunication.class));
		SUPPORTED_PARAMETERS.put("communication-preferred", new FieldParameter("communication-preferred", "preferred", Boolean.class, 
				Arrays.asList(
						SearchParameter.Modifier.TEXT, 
						SearchParameter.Modifier.ABOVE, 
						SearchParameter.Modifier.BELOW, 
						SearchParameter.Modifier.NOT, 
						SearchParameter.Modifier.IN, 
						SearchParameter.Modifier.NOT_IN, 
						SearchParameter.Modifier.OFTYPE),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientCommunication.class));
		// human name
		SUPPORTED_PARAMETERS.put("name", new GroupParameter("name", "names", String.class, new String[] {"given", "family", "prefix", "suffix"},
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class));
		SUPPORTED_PARAMETERS.put("given", new FieldParameter("given", "given", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class));
		SUPPORTED_PARAMETERS.put("family", new FieldParameter("family", "family", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class));
		SUPPORTED_PARAMETERS.put("prefix", new FieldParameter("prefix", "prefix", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class));
		SUPPORTED_PARAMETERS.put("suffix", new FieldParameter("suffix", "suffix", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class));
		// identifier
		SUPPORTED_PARAMETERS.put("identifier", new GroupParameter("identifier", "identifiers", String.class, new String[] {"use", "system", "value"}, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientIdentifier.class));
		SUPPORTED_PARAMETERS.put("use", new FieldParameter("use", "use", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientIdentifier.class));
		SUPPORTED_PARAMETERS.put("system", new FieldParameter("system", "system", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientIdentifier.class));
		SUPPORTED_PARAMETERS.put("value", new FieldParameter("value", "value", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientIdentifier.class));
		// address
		SUPPORTED_PARAMETERS.put("address", new GroupParameter("address", "addresses", String.class, new String[] {"address-city", "address-state", "address-country", "address-postalcode", "address-district", "address-use"}, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-city", new FieldParameter("city", "city", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-state", new FieldParameter("state", "state", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-country", new FieldParameter("country", "country", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-postalcode", new FieldParameter("postalcode", "postalcode", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-district", new FieldParameter("district", "district", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		SUPPORTED_PARAMETERS.put("address-use", new FieldParameter("use", "use", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class));
		// resource level parameters
		SUPPORTED_PARAMETERS.put("id", new FieldParameter("id", "id", String.class, 
				Arrays.asList(SearchParameter.Modifier.EXACT, SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("_text", new FieldParameter("txt", "txt", String.class, 
				Arrays.asList(SearchParameter.Modifier.CONTAINS),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("active", new FieldParameter("active", "active", Boolean.class, 
				Arrays.asList(
						SearchParameter.Modifier.TEXT, 
						SearchParameter.Modifier.ABOVE, 
						SearchParameter.Modifier.BELOW, 
						SearchParameter.Modifier.NOT, 
						SearchParameter.Modifier.IN, 
						SearchParameter.Modifier.NOT_IN, 
						SearchParameter.Modifier.OFTYPE),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("birthdate", new FieldParameter("birthdate", "birthDate", Date.class, 
				Arrays.asList(),
				Arrays.asList(
						SearchParameter.Comparator.AP, 
						SearchParameter.Comparator.EB, 
						SearchParameter.Comparator.SA, 
						SearchParameter.Comparator.EQ, 
						SearchParameter.Comparator.NE, 
						SearchParameter.Comparator.LE, 
						SearchParameter.Comparator.LT, 
						SearchParameter.Comparator.GE, 
						SearchParameter.Comparator.GT 
						),
				new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("gender", new FieldParameter("gender", "gender", String.class, 
				Arrays.asList(
						SearchParameter.Modifier.TEXT, 
						SearchParameter.Modifier.ABOVE, 
						SearchParameter.Modifier.BELOW, 
						SearchParameter.Modifier.NOT, 
						SearchParameter.Modifier.IN, 
						SearchParameter.Modifier.NOT_IN, 
						SearchParameter.Modifier.OFTYPE),
				Arrays.asList(),
				new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
	}
	
	private SearchParameterRegistry() {
	}
	
	private void load() throws FileNotFoundException {
		//get json file from resources folder
		FileReader fr = new FileReader(new File(getClass().getClassLoader().getResource(FHIR_SEARCH_PARAMETERS_JSON).getFile()));
		this.bunndle = this.parser.parse(fr);
		// below code traverse bundle hierarchy and build parameter registry
		// to be added
	}

	public static SearchParameterRegistry getInstance() {
		if (instance == null) {
            synchronized (SearchParameterRegistry.class) {
                if (instance == null) {
                    instance = new SearchParameterRegistry();
                    try {
						instance.load();
					} catch (FileNotFoundException e) {
						throw new IllegalStateException("FHIR search parameters definition file: " + FHIR_SEARCH_PARAMETERS_JSON + " not found.");
					}
                }
            }
        }
        return instance;
    }

	public static <T extends Resource, U extends ResourceComplexType> String[] getJoinAttributes(Class<T> resourceClazz, Class<U> refClazz) {
		return ENTITY_ENTITY_JOINATTS.get(resourceClazz).get(refClazz);
	}

	public static SearchParameter getParameterDescriptor(String pname) {
		return SUPPORTED_PARAMETERS.get(pname);
	}
	
	public static Modifier getModifier(String sm) {
		return SearchParameter.MODIFIERMAP.get(sm);
	}
	
	public static Comparator getComparator(String sc) {
		return SearchParameter.COMPARATORMAP.get(sc);
	}
	
	public static Comparator checkComparator(String value, String[] comparator) {
		Comparator c = null;
		for (Map.Entry<String, Comparator> e: SearchParameter.COMPARATORMAP.entrySet()) {
			if (value.startsWith(e.getKey())) {
				comparator[0] = e.getKey();
				comparator[1] = value.substring(e.getKey().length());
				c = e.getValue();
				break;
			}
		}
		return c;
	}
}
