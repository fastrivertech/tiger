package com.frt.dr.service.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	// temp search parameter registry
	protected static Map<String, SearchParameter> SUPPORTED_PARAMETERS = new HashMap<String, SearchParameter>();

	static {
		SearchParameter pd = new GroupParameter("name", 
				Arrays.asList("given", "family", "prefix", "suffix"),
				"names", new String[] {"Patient"}, com.frt.dr.model.base.PatientHumanName.class);
		SUPPORTED_PARAMETERS.put("name", pd);
		SUPPORTED_PARAMETERS.put("given", pd);
		SUPPORTED_PARAMETERS.put("family", pd);
		SUPPORTED_PARAMETERS.put("prefix", pd);
		SUPPORTED_PARAMETERS.put("suffix", pd);
		pd = new GroupParameter("identifier", 
				Arrays.asList("use", "system", "value"), 
				"identifiers", new String[] {"Patient"}, com.frt.dr.model.base.PatientIdentifier.class);
		SUPPORTED_PARAMETERS.put("identifier", pd);
		pd = new GroupParameter("address", 
				Arrays.asList("address-city", "address-state", "address-country", "addresse-postalcode", "addresse-use"), 
				"addresses", new String[] {"Patient"}, com.frt.dr.model.base.PatientAddress.class);
		SUPPORTED_PARAMETERS.put("address", pd);
		SUPPORTED_PARAMETERS.put("address-city", pd);
		SUPPORTED_PARAMETERS.put("address-state", pd);
		SUPPORTED_PARAMETERS.put("address-country", pd);
		SUPPORTED_PARAMETERS.put("address-postalcode", pd);
		SUPPORTED_PARAMETERS.put("address-use", pd);
		SUPPORTED_PARAMETERS.put("_id", new FieldParameter("_id", "id", String.class, new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("active", new FieldParameter("active", "active", Boolean.class, new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("birthdate", new FieldParameter("birthdate", "birthdate", Date.class, new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
		SUPPORTED_PARAMETERS.put("gender", new FieldParameter("gender", "gender", String.class, new String[] {"Patient"}, com.frt.dr.model.base.Patient.class));
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

	public static com.frt.dr.service.query.SearchParameter getParameterDescriptor(String pname) {
		return SUPPORTED_PARAMETERS.get(pname);
	}
	
}
