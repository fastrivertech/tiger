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
package com.frt.dr.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.frt.dr.service.FieldParameter;
import com.frt.dr.service.GroupParameter;
import com.frt.dr.service.SearchParameter;

import java.util.logging.*;
import org.springframework.data.repository.Repository;

/**
 * BaseDao class
 * @author chaye
 */
public abstract class BaseDao<T,ID> implements Repository {
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

	protected static final String PARAM_MODIFIER_DELIMETER = ":";
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
	
	protected JdbcTemplate jdbcTemplate;
	
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    	this.jdbcTemplate = jdbcTemplate;
    }

	@PersistenceContext
	protected EntityManager em;
    
    public void setEntityManager(EntityManager em) {
    	this.em = em;
    }
	
    public abstract Optional<T> save(T entry) throws DaoException;

    public abstract Optional<T> findById(ID id) throws DaoException;
    
    public abstract Optional<List<T>> query(Map<String, String> params) throws DaoException;
    
    public SearchParameter getParamDesc(String name) {
    	return SUPPORTED_PARAMETERS.get(name);
    }
}
