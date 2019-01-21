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

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockTimeoutException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import com.frt.dr.model.Resource;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientIdentifier;

/**
 * ResourceDao class
 * 
 * @author jfu
 */
public class ResourceDao extends BaseDao<Resource, String> {

	@Override
	public Optional<Resource> save(Resource entry) throws DaoException {
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			em.persist(entry);
			transaction.commit();
			return Optional.of(entry);
		} catch (IllegalStateException | RollbackException ex) {
			try {
				if (transaction != null) {
					transaction.rollback();
				}
			} catch (IllegalStateException | RollbackException ignore) {
			}
			throw new DaoException(ex);
		}
	}

	@Override
	public Optional<Resource> findById(String id) throws DaoException {
		try {
			Query query = em.createNamedQuery("getResourceById");
			query.setParameter("id", id);
			List<Resource> resources = (List<Resource>) query.getResultList();
			Optional<Resource> resource = null;
			if (resources.size() > 0) {

				resource = Optional.ofNullable(resources.get(0));
			} else {
				resource = Optional.empty();
			}
			return resource;
		} catch (IllegalArgumentException | QueryTimeoutException | TransactionRequiredException
				| PessimisticLockException | LockTimeoutException ex) {
			throw new DaoException(ex);
		} catch (PersistenceException ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	public Optional<List<Resource>> query(Map params) throws DaoException {
		try {
			Map<String, Object> whereParams = new HashMap<String, Object>();
			CriteriaQuery cq = getWhereClause(em, com.frt.dr.model.base.Patient.class, params, whereParams);
			Query query = em.createQuery(cq);
			Set<Parameter<?>> qparams = query.getParameters();
			Iterator<Parameter<?>> pit = qparams.iterator();
			while (pit.hasNext()) {
				Parameter qparam = pit.next();
				System.out.println("param: class:" + qparam.getClass() + ", name:" + qparam.getName() + ", position:"
						+ qparam.getPosition() + ", type:" + qparam.getParameterType());
				String value = (String) whereParams.get(qparam.getName());
				if (value != null) {
					if (qparam.getParameterType().equals(String.class)) {
						query.setParameter(qparam.getName(), value);
					} else if (qparam.getParameterType().equals(Date.class)) {
						// date param
						Date d = parseDate(value);
						if (d != null) {
							query.setParameter(qparam.getName(), d);
						} else {
							throw new IllegalArgumentException("Query parameter:" + qparam.getName()
									+ " expect date value in the format of: " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd
									+ " or " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or "
									+ BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or "
									+ BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
						}
					} else if (qparam.getParameterType().equals(Boolean.class)) {
						Boolean b = false;
						if (value.equalsIgnoreCase("true")) {
							b = true;
						} else if (value.equalsIgnoreCase("false")) {
							b = false;
						} else {
							throw new IllegalArgumentException(
									"Query parameter:" + qparam.getName() + " expect true/false, value=" + value);
						}
						query.setParameter(qparam.getName(), b);
					} else if (Number.class.isAssignableFrom(qparam.getParameterType())) {
						// number parameter
						// BigInteger, Byte, Double, Float, Integer, Long, Short
						if (qparam.getParameterType().equals(Integer.class)) {
							query.setParameter(qparam.getName(), Integer.valueOf(value));
						} else if (qparam.getParameterType().equals(Long.class)) {
							query.setParameter(qparam.getName(), Long.valueOf(value));
						} else if (qparam.getParameterType().equals(Short.class)) {
							query.setParameter(qparam.getName(), Short.valueOf(value));
						} else if (qparam.getParameterType().equals(Float.class)) {
							query.setParameter(qparam.getName(), Float.valueOf(value));
						} else if (qparam.getParameterType().equals(Double.class)) {
							query.setParameter(qparam.getName(), Double.valueOf(value));
						} else {
							throw new IllegalArgumentException("Numeric parameter of type :"
									+ qparam.getParameterType().getClass().getCanonicalName()
									+ " not supported yet, value=" + value);
						}
					}
				} else {
					throw new IllegalStateException("Encountered query parameter: " + qparam.getName()
							+ ", which is not among the request query parameters.");
				}
			}
			List<Resource> resources = (List<Resource>) query.getResultList();
			Optional<List<Resource>> result = null;
			if (resources.size() > 0) {
				result = Optional.ofNullable(resources);
			} else {
				result = Optional.empty();
			}
			return result;
		} catch (IllegalArgumentException | QueryTimeoutException | TransactionRequiredException
				| PessimisticLockException | LockTimeoutException ex) {
			throw new DaoException(ex);
		} catch (PersistenceException ex) {
			throw new DaoException(ex);
		}
	}

	private Date parseDate(String value) {
		Date d = null;
		for (int i = 0; i < BaseDao.DF_FMT_SUPPORTED.length; i++) {
			try {
				d = BaseDao.DF_FMT_SUPPORTED[i].parse(value);
				break;
			} catch (ParseException e) {
				continue;
			}
		}
		return d;
	}

	/*********************************
	 * eq the value for the parameter in the resource is equal to the provided value
	 * the range of the search value fully contains the range of the target value ne
	 * the value for the parameter in the resource is not equal to the provided
	 * value the range of the search value does not fully contain the range of the
	 * target value gt the value for the parameter in the resource is greater than
	 * the provided value the range above the search value intersects (i.e.
	 * overlaps) with the range of the target value lt the value for the parameter
	 * in the resource is less than the provided value the range below the search
	 * value intersects (i.e. overlaps) with the range of the target value ge the
	 * value for the parameter in the resource is greater or equal to the provided
	 * value the range above the search value intersects (i.e. overlaps) with the
	 * range of the target value, or the range of the search value fully contains
	 * the range of the target value le the value for the parameter in the resource
	 * is less or equal to the provided value the range below the search value
	 * intersects (i.e. overlaps) with the range of the target value or the range of
	 * the search value fully contains the range of the target value sa the value
	 * for the parameter in the resource starts after the provided value the range
	 * of the search value does not overlap with the range of the target value, and
	 * the range above the search value contains the range of the target value eb
	 * the value for the parameter in the resource ends before the provided value
	 * the range of the search value does overlap not with the range of the target
	 * value, and the range below the search value contains the range of the target
	 * value ap the value for the parameter in the resource is approximately the
	 * same to the provided value. Note that the recommended value for the
	 * approximation is 10% of the stated value (or for a date, 10% of the gap
	 * between now and the date), but systems may choose other values where
	 * appropriate the range of the search value overlaps with the range of the
	 * target value;
	 * 
	 * 
	 * [parameter]=eq2013-01-14 2013-01-14T00:00 matches (obviously)
	 * 2013-01-14T10:00 matches 2013-01-15T00:00 does not match - it's not in the
	 * range [parameter]=ne2013-01-14 2013-01-15T00:00 matches - it's not in the
	 * range 2013-01-14T00:00 does not match - it's in the range 2013-01-14T10:00
	 * does not match - it's in the range [parameter]=lt2013-01-14T10:00 2013-01-14
	 * matches, because it includes the part of 14-Jan 2013 before 10am
	 * [parameter]=gt2013-01-14T10:00 2013-01-14 matches, because it includes the
	 * part of 14-Jan 2013 after 10am [parameter]=ge2013-03-14 "from 21-Jan 2013
	 * onwards" is included because that period may include times after 14-Mar 2013
	 * [parameter]=le2013-03-14 "from 21-Jan 2013 onwards" is included because that
	 * period may include times before 14-Mar 2013 [parameter]=sa2013-03-14 "from
	 * 15-Mar 2013 onwards" is included because that period starts after 14-Mar 2013
	 * "from 21-Jan 2013 onwards" is not included because that period starts before
	 * 14-Mar 2013 "before and including 21-Jan 2013" is not included because that
	 * period starts (and ends) before 14-Mar 2013 [parameter]=eb2013-03-14 "from
	 * 15-Mar 2013 onwards" is not included because that period starts after 14-Mar
	 * 2013 "from 21-Jan 2013 onwards" is not included because that period starts
	 * before 14-Mar 2013, but does not end before it "before and including 21-Jan
	 * 2013" is included because that period ends before 14-Mar 2013
	 * [parameter]=ap2013-03-14 14-Mar 2013 is included - as it exactly matches
	 * 21-Jan 2013 is not included because that is near 14-Mar 2013 15-Jun 2015 is
	 * not included - as it is not near 14-Mar 2013. Note that the exact value here
	 * is at the discretion of the system *
	 * 
	 *********************************************************/
	/**
	 * support below patient parameters: Patient.id Patient.identifier
	 * Patient.active Patient.address Patient.address.city Patient.address.country
	 * Patient.address.postalCode Patient.address.state Patient.birthDate
	 * Patient.name Patient.name.family Patient.name.given Patient.gender
	 * Patient.telecom.value ********************************* active address
	 * address-city address-country address-postalcode address-state address-use
	 * birthdate death-date deceased email family gender general-practitioner
	 * (Practitioner, Organization, PractitionerRole) given identifier language link
	 * (Patient, RelatedPerson) name - string A server defined search that may match
	 * any of the string fields in the HumanName, including family, give, prefix,
	 * suffix, suffix, and/or text Patient.name organization - The organization that
	 * is the custodian of the patient record Patient.managingOrganization
	 * (Organization) phone - A value in a phone contact
	 * Patient.telecom.where(system='phone') 4 Resources phonetic - string A portion
	 * of either family or given name using some kind of phonetic matching algorithm
	 * Patient.name 3 Resources telecom - The value in
	 *
	 * *********************************
	 * 
	 * @param em
	 *            EntityManager
	 * @param clazz
	 * @param params
	 * @return CriteriaQuery cq;
	 */
	private CriteriaQuery getWhereClause(EntityManager em, Class ResourceClazz, Map<String, String> params, Map<String, Object> whereParams) {
		if (ResourceClazz.equals(com.frt.dr.model.base.Patient.class)) {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<com.frt.dr.model.base.Patient> cq = cb.createQuery(com.frt.dr.model.base.Patient.class);
			Root<com.frt.dr.model.base.Patient> rootPatient = cq.from(com.frt.dr.model.base.Patient.class);
			Predicate where = cb.conjunction();
			// attribute of Entity: _id, Resource ID (logical) 64 VARCHAR
			Map<Class, Boolean> processed = new HashMap<Class, Boolean>();
			for (Map.Entry<String, String> e : params.entrySet()) {
				String key = e.getKey();
				String value = e.getValue();
				
				if (key.equals("_id")) {
					where = cb.and(where, cb.equal(rootPatient.get("id"), cb.parameter(String.class, "_id")));
					whereParams.put(key, value);
				}
				
				if (key.equals("active")) {
					where = cb.and(where, cb.equal(rootPatient.get("active"), cb.parameter(Boolean.class, "active")));
					whereParams.put(key, Boolean.valueOf(value));
				}
				
				if (key.equals("birthdate")) {
					where = cb.and(where,
							cb.equal(rootPatient.get("birthDate"), cb.parameter(Date.class, "birthdate")));
					Date d = parseDate(value);
					if (d != null) {
						whereParams.put(key, d);
					} else {
						throw new IllegalArgumentException("Query parameter:" + key
								+ " expect date value in the format of: " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd
								+ " or " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or "
								+ BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or "
								+ BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
					}

				}
				
				if (key.equals("gender")) {
					where = cb.and(where, cb.equal(rootPatient.get("gender"), cb.parameter(String.class, "gender")));
					whereParams.put(key, value);
				}

				if (key.startsWith("name") || key.startsWith("given") || key.startsWith("family")
						|| key.startsWith("prefix") || key.startsWith("suffix")) {
					Boolean b = processed.get(com.frt.dr.model.base.PatientHumanName.class);
					if (b==null||!b) {
						Map<String, Boolean> attributes = new HashMap<String, Boolean>(); // <'param-name',
																							// 'exact-flag'>
						boolean isConjunction = true; // all predicates AND'd
						if (key.startsWith("name")) {
							// do not support name:exact
							// any of 'given', 'family', 'prefix', 'suffix' contains param value
							// all take 'contains' semantics
							attributes.put("given", false);
							whereParams.put("given", key);
							attributes.put("family", false);
							whereParams.put("family", key);
							attributes.put("prefix", false);
							whereParams.put("prefix", key);
							attributes.put("suffix", false);
							whereParams.put("suffix", key);
							// all predicates OR'd
							isConjunction = false;
						} else {

							String given = params.get("given");
							if (given != null) {
								attributes.put("given", false);
								whereParams.put("given", params.get("given"));
							}
							String family = params.get("family");
							if (family != null) {
								attributes.put("family", false);
								whereParams.put("family", params.get("family"));
							}
							String prefix = params.get("prefix");
							if (prefix != null) {
								attributes.put("prefix", false);
								whereParams.put("prefix", params.get("prefix"));
							}
							String suffix = params.get("suffix");
							if (suffix != null) {
								attributes.put("suffix", false);
								whereParams.put("suffix", params.get("suffix"));
							}

							String givenE = params.get("given:exact");
							if (givenE != null) {
								attributes.put("given", true);
								whereParams.put("given", params.get("given:exact"));
							}
							String familyE = params.get("family:exact");
							if (familyE != null) {
								attributes.put("family", true);
								whereParams.put("family", params.get("family:exact"));
							}
							String prefixE = params.get("prefix:exact");
							if (prefixE != null) {
								attributes.put("prefix", true);
								whereParams.put("prefix", params.get("prefix:exact"));
							}
							String suffixE = params.get("suffix:exact");
							if (suffixE != null) {
								attributes.put("suffix", true);
								whereParams.put("suffix", params.get("suffix:exact"));
							}

							String givenC = params.get("given:contain");
							if (givenC != null) {
								attributes.put("given", false);
								whereParams.put("given", params.get("given:contain"));
							}
							String familyC = params.get("family:contain");
							if (familyC != null) {
								attributes.put("family", false);
								whereParams.put("family", params.get("family:contain"));
							}
							String prefixC = params.get("prefix:contain");
							if (prefixC != null) {
								attributes.put("prefix", false);
								whereParams.put("prefix", params.get("prefix:contain"));
							}
							String suffixC = params.get("suffix:contain");
							if (suffixC != null) {
								attributes.put("suffix", false);
								whereParams.put("suffix", params.get("suffix:contain"));
							}
						}
						// mark name as processed
						processed.put(com.frt.dr.model.base.PatientHumanName.class, true);
						// extract all human name params
						// humanName - PatientHumanName table
						where = appendSubquery(em, cb, cq, rootPatient, where, com.frt.dr.model.base.Patient.class, com.frt.dr.model.base.PatientHumanName.class,
								attributes, isConjunction);
					}
				}
				if (key.startsWith("identifier")) {
					Boolean b = processed.get(com.frt.dr.model.base.PatientIdentifier.class);
					if (b==null||!b) {
						// extract all identifier :
						// further match use, system, value
						Map<String, Boolean> attributes = new HashMap<String, Boolean>(); // <'param-name',
																							// 'exact-flag'>
						boolean isConjunction = false; // all predicates OR'd
						// do not support identifier:exact
						// any of 'use', 'system', 'value' contains param value
						// all take 'contains' semantics
						attributes.put("use", false);
						attributes.put("system", false);
						attributes.put("value", false);
						// attribute in associated Entity: e.g. PatientIdentifier
						// identifier - PatientIdentifier table
						// mark identifier as processed
						processed.put(com.frt.dr.model.base.PatientIdentifier.class, true);
						where = appendSubquery(em, cb, cq, rootPatient, where, com.frt.dr.model.base.Patient.class,
								com.frt.dr.model.base.PatientIdentifier.class, attributes, isConjunction);
					}
				}
				if (key.startsWith("address")) {
					Boolean b = processed.get(com.frt.dr.model.base.PatientAddress.class);
					if (b==null||!b) {
						// extract all addressXXX parameters
						Map<String, Boolean> attributes = new HashMap<String, Boolean>(); // <'param-name',
																							// 'exact-flag'>
						boolean isConjunction = true; // all predicates AND'd
						if (key.equals("address")) {
							// do not support address:exact
							// any of 'city', 'state', 'country', 'postalcode', 'use' contains param value
							// all take 'contains' semantics
							attributes.put("city", false);
							attributes.put("state", false);
							attributes.put("country", false);
							attributes.put("postalcode", false);
							attributes.put("use", false);
							// all predicates OR'd
							isConjunction = false;
						} else {
							
							String city = params.get("address-city");
							if (city != null)
								attributes.put("city", false);
							String state = params.get("address-state");
							if (state != null)
								attributes.put("state", false);
							String country = params.get("address-country");
							if (country != null)
								attributes.put("country", false);
							String postalcode = params.get("address-postalcode");
							if (postalcode != null)
								attributes.put("postalcode", false);
							String use = params.get("address-use");
							if (use != null)
								attributes.put("use", false);

							// any exact match params?
							String cityE = params.get("address-city:exact");
							if (cityE != null)
								attributes.put("city", true);
							String stateE = params.get("address-state:exact");
							if (stateE != null)
								attributes.put("state", true);
							String countryE = params.get("address-country:exact");
							if (countryE != null)
								attributes.put("country", true);
							String postalcodeE = params.get("address-postalcode:exact");
							if (postalcodeE != null)
								attributes.put("postalcode", true);
							String useE = params.get("address-use:exact");
							if (useE != null)
								attributes.put("use", true);
							
							// any contains search params?
							String cityC = params.get("address-city:contains");
							if (cityC != null)
								attributes.put("city", false);
							String stateC = params.get("address-state:contains");
							if (stateC != null)
								attributes.put("state", false);
							String countryC = params.get("address-country:contains");
							if (countryC != null)
								attributes.put("country", false);
							String postalcodeC = params.get("address-postalcode:contains");
							if (postalcodeC != null)
								attributes.put("postalcode", false);
							String useC = params.get("address-use:contains");
							if (useC != null)
								attributes.put("use", false);
						}
						// mark name as processed
						processed.put(com.frt.dr.model.base.PatientHumanName.class, true);
						// address - PatientAddress table
						where = appendSubquery(em, cb, cq, rootPatient, where, com.frt.dr.model.base.Patient.class, 
								com.frt.dr.model.base.PatientAddress.class,
								attributes, isConjunction);
					}
				}

			}
			cq.where(where);
			return cq;
		} else {
			throw new UnsupportedOperationException("Query with parameters on resource: "
					+ ResourceClazz.getClass().getCanonicalName() + " not implemented yet.");
		}
	}

	private Predicate appendSubquery(EntityManager em, CriteriaBuilder cb, CriteriaQuery<Patient> cq, Root mainRoot, Predicate where,
			Class mainClazz, Class refClazz, Map<String, Boolean> attributes, boolean isConjunction) {
		//Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
		Root rootName = cq.from(refClazz);
		Metamodel m = em.getMetamodel();
		EntityType<Patient> Patient_ = m.entity(Patient.class);
		EntityType<PatientHumanName> HumanName_ = m.entity(PatientHumanName.class);
		
		Predicate criteria = null;
		Iterator it = null;
		Boolean exactFlag = true;
		String paramName = null;
		if (isConjunction) {
			// AND'd
			criteria = cb.conjunction();
			it = attributes.keySet().iterator();
			while (it.hasNext()) {
				paramName = (String)it.next();
				exactFlag = attributes.get(paramName);
				criteria = cb.and(criteria, 
						exactFlag ? 
							cb.equal(rootName.get(paramName), cb.parameter(String.class, paramName))
							: cb.like(rootName.get(paramName), cb.parameter(String.class, paramName)));
			}
		} else {
			// OR'd
			criteria = cb.disjunction();
			it = attributes.keySet().iterator();
			while (it.hasNext()) {
				paramName = (String)it.next();
				exactFlag = attributes.get(paramName);
				criteria = cb.or(criteria, 
						exactFlag?
							cb.equal(rootName.get(paramName), cb.parameter(String.class, paramName))
							: cb.like(rootName.get(paramName), cb.parameter(String.class, paramName)));
			}
		}
		Join<Patient, PatientHumanName> y=mainRoot.join(Patient_.getList("names", PatientHumanName.class));
//		subquery.select(rootName.get("resource_id")).where(criteria);
//		return cb.and(where, cb.in(mainRoot.<BigInteger>get("patientId")).value(subquery));
		return cb.and(where, criteria);
	}

	private String getParamValue(Map params, String paramName) {
		LinkedList<?> valueList = (LinkedList<?>) params.get(paramName);
		String value = null;
		if (valueList != null) {
			value = (String) valueList.get(0);
		}
		return value;
	}

}
