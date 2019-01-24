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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import com.frt.dr.model.Resource;
import com.frt.dr.model.ResourceComplexType;
import com.frt.dr.service.query.FieldParameter;
import com.frt.dr.service.query.GroupParameter;
import com.frt.dr.service.query.SearchParameter;
import com.frt.dr.service.query.SearchParameterRegistry;

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
	public Optional<List<Resource>> query(Class<Resource> resourceClazz, Map<String, String> params)
			throws DaoException {
		try {
			Map<String, Object> whereParams = new HashMap<String, Object>();
			CriteriaQuery cq = getQueryCriteria(em, resourceClazz, params, whereParams);
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
									+ " expect date value in the format of: "
									+ SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or "
									+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
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
		for (int i = 0; i < SearchParameterRegistry.DF_FMT_SUPPORTED.length; i++) {
			try {
				d = SearchParameterRegistry.DF_FMT_SUPPORTED[i].parse(value);
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
	private <T extends Resource, U extends ResourceComplexType> CriteriaQuery<T> getQueryCriteria(EntityManager em,
			Class<T> resourceClazz, Map<String, String> params, Map<String, Object> whereParams) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(resourceClazz);
		Root<T> rootResource = cq.from(resourceClazz);
		Predicate where = cb.conjunction();
		Class<? extends ResourceComplexType> associatedClazz = null;
		Map<String, Boolean> processed = new HashMap<String, Boolean>();
		SearchParameter sp = null;
		Map<String, Boolean> attributes = null;

		for (Map.Entry<Class<?>, List<String>> entityParamNames : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS
				.entrySet()) {
			Class<?> entity = entityParamNames.getKey();
			List<String> epnames = entityParamNames.getValue();
			List<ActualParameter> actualParams = extractParameter(params, epnames);
			if (actualParams != null) {
				attributes = new HashMap<String, Boolean>();
				if (entity.equals(resourceClazz)) {
					// parameter that is the resource's attribute
					where = addCriteria(where, cb, rootResource, actualParams);
					processEntityParameters((GroupParameter) sp, attributes, whereParams, params);
				} else {
					boolean isConjunction = true; // all predicates AND'd
					where = cb.and(where, cb.equal(rootResource.get(sp.getFieldName()),
							cb.parameter(((FieldParameter) sp).getType(), sp.getName())));
					processEntityParameters((GroupParameter) sp, attributes, whereParams, params);
					// parameter that is in a ref class - join needed
					where = addEntityCriteria(em, cb, cq, rootResource, where, resourceClazz, associatedClazz,
							attributes, sp.getFieldName(), isConjunction);
				}
			}
		}
		cq.where(where);
		return cq;
	}

	private <T extends Resource> Predicate addCriteria(Predicate where, CriteriaBuilder cb, Root<T> rootResource,
			List<ActualParameter> actualParams) {
		SearchParameter sp = null;
		for (ActualParameter ap : actualParams) {
			String baseName = ap.getBaseName();
			sp = SearchParameterRegistry.getParameterDescriptor(baseName);
			if (ap.getType().equals(String.class)) {
				if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
					where = cb.and(where,
							cb.equal(rootResource.get(sp.getFieldName()), cb.parameter(String.class, baseName)));
				} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
					where = cb.and(where,
							cb.like(rootResource.get(sp.getFieldName()), cb.parameter(String.class, baseName)));
				} else {
					throw new IllegalArgumentException(
							"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + baseName);
				}
			} else if (ap.getType().equals(Date.class)) {
				Path<Object> path = rootResource.get(sp.getFieldName());
				ParameterExpression<Date> p = cb.parameter(Date.class, baseName);
				if (ap.getEnumComparator() == null) {
					where = cb.and(where, cb.equal(path, p));
				} else {
					switch (ap.getEnumComparator()) {
					case EQ:
						where = cb.and(where, cb.equal(path, p));
						break;
					case NE:
						where = cb.and(where, cb.notEqual(path, p));
						break;
					case GT:
						where = cb.and(where, cb.greaterThan(path.as(Date.class), p));
						break;
					case GE:
						where = cb.and(where, cb.greaterThanOrEqualTo(path.as(Date.class), p));
						break;
					case LT:
						where = cb.and(where, cb.lessThan(path.as(Date.class), p));
						break;
					case LE:
						where = cb.and(where, cb.lessThanOrEqualTo(path.as(Date.class), p));
						break;
					case SA:
						where = cb.and(where, cb.greaterThan(path.as(Date.class), p));
						break;
					case EB:
						where = cb.and(where, cb.lessThan(path.as(Date.class), p));
						break;
					case AP:
					default:
						throw new IllegalArgumentException(
								"Unsupported comparator : " + ap.getComparator() + ", for parameter: " + baseName);
					}
				}
			} else if (Number.class.isAssignableFrom(ap.getType())) {
				Path<Object> path = rootResource.get(sp.getFieldName());
				ParameterExpression<Number> p = cb.parameter(Number.class, baseName);
				if (ap.getEnumComparator() == null) {
					where = cb.and(where, cb.equal(path, p));
				} else {
					switch (ap.getEnumComparator()) {
					case EQ:
						where = cb.and(where, cb.equal(path, p));
						break;
					case NE:
						where = cb.and(where, cb.notEqual(path, p));
						break;
					case GT:
						where = cb.and(where, cb.gt(path.as(Number.class), p));
						break;
					case GE:
						where = cb.and(where, cb.ge(path.as(Number.class), p));
						break;
					case LT:
						where = cb.and(where, cb.lt(path.as(Number.class), p));
						break;
					case LE:
						where = cb.and(where, cb.le(path.as(Number.class), p));
						break;
					case SA:
						where = cb.and(where, cb.gt(path.as(Number.class), p));
						break;
					case EB:
						where = cb.and(where, cb.lt(path.as(Number.class), p));
						break;
					case AP:
					default:
						throw new IllegalArgumentException(
								"Unsupported comparator : " + ap.getComparator() + ", for parameter: " + baseName);
					}
				}
			} else {
				throw new IllegalArgumentException("Unsupported parameter type: " + ap.getType().getCanonicalName()
						+ " for [" + baseName + "], supported types: string, date, numeric.");
			}

			where = cb.and(where, cb.equal(rootResource.get(sp.getFieldName()),
					cb.parameter(((FieldParameter) sp).getType(), sp.getName())));
		}
		return where;
	}

	private void processEntityParameters(GroupParameter gp, Map<String, Boolean> attributes,
			Map<String, Object> whereParams, Map<String, String> params) {
		// for entity fields AND'd match that could be : exact or contains etc. other
		// modifiers
		// for entity group parameter : need to generate OR'd predicates with string
		// like match (if the fields are string)
		// or any other modifier specified and applicable to the field
		String value = "";
		for (String f : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS.get(gp.getEntityClass())) {
			attributes.put(f, false);
			whereParams.put(f, convertToLikePattern(value));
		}
		String pv = null;
		for (String f : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS.get(gp.getEntityClass())) {
			if ((pv = params.get(f)) != null) {
				attributes.put(f, false);
				whereParams.put(f, convertToLikePattern(pv));
			}
			if ((pv = params.get(f + ":exact")) != null) {
				attributes.put(f, true);
				whereParams.put(f, pv);
			}
			if ((pv = params.get(f + ":contains")) != null) {
				attributes.put(f, false);
				whereParams.put(f, convertToLikePattern(pv));
			}
		}
	}

	private <T extends Resource, U extends ResourceComplexType> Predicate addEntityCriteria(EntityManager em,
			CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> mainRoot, Predicate where, Class<T> resourceClazz,
			Class<U> refClazz, Map<String, Boolean> attributes, String joinAttr, boolean isConjunction) {
		Metamodel m = em.getMetamodel();
		EntityType<T> resourceEntity_ = m.entity(resourceClazz);
		Join<T, U> join = mainRoot.join(resourceEntity_.getList(joinAttr, refClazz));

		Predicate criteria = null;
		Iterator<String> it = null;
		Boolean exactFlag = true;
		String paramName = null;
		if (isConjunction) {
			// AND'd
			criteria = cb.conjunction();
			it = attributes.keySet().iterator();
			while (it.hasNext()) {
				paramName = (String) it.next();
				exactFlag = attributes.get(paramName);
				criteria = cb.and(criteria,
						exactFlag ? cb.equal(join.get(paramName), cb.parameter(String.class, paramName))
								: cb.like(join.get(paramName), cb.parameter(String.class, paramName)));
			}
		} else {
			// OR'd
			criteria = cb.disjunction();
			it = attributes.keySet().iterator();
			while (it.hasNext()) {
				paramName = (String) it.next();
				exactFlag = attributes.get(paramName);
				criteria = cb.or(criteria,
						exactFlag ? cb.equal(join.get(paramName), cb.parameter(String.class, paramName))
								: cb.like(join.get(paramName), cb.parameter(String.class, paramName)));
			}
		}
		return cb.and(where, criteria);
	}

	/**
	 * helper - gen SQL String column LIKE pattern
	 * 
	 * @param value
	 * @return
	 */
	private String convertToLikePattern(String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("%");
		sb.append(value);
		sb.append("%");
		return sb.toString();
	}

	private String[] parseParamName(String pn) {
		String[] parts = pn.split(SearchParameterRegistry.PARAM_MODIFIER_DELIMETER);
		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed parameter name: " + pn + ", parameter name format: <name> or <name>:<modifier>.");
		}
		return parts;
	}

	// private boolean isProcessed(SearchParameter sp, Map<String, Boolean>
	// processed) {
	// boolean isProcessed = true;
	// Boolean b = (processed.get(sp.getEntityClass().getCanonicalName())!=null);
	// if (b==null||!b) {
	// isProcessed=false;
	// // mark as processed
	// processed.put(sp.getEntityClass().getCanonicalName(), true);
	// }
	// return isProcessed;
	// }

	private List<ActualParameter> extractParameter(Map<String, String> queryParams, List<String> entityParamNames) {
		List<ActualParameter> actualParams = null;
		ActualParameter actualParam = null;
		for (String epname : entityParamNames) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				String key = e.getKey();
				String value = e.getValue();
				if (key.startsWith(epname)) {
					SearchParameter sp = SearchParameterRegistry.getParameterDescriptor(epname);
					if (sp == null) {
						throw new IllegalStateException("Parameter definition not found for: " + epname);
					}
					actualParam = new ActualParameter(key, value);
					actualParam = parse(sp, actualParam);
					if (actualParams == null) {
						actualParams = new ArrayList<ActualParameter>();
					}
					actualParams.add(actualParam);
				}
			}
		}
		return actualParams;
	}

	// private ActualParameter extractParameter(Map<String, String> params, String
	// pbase) {
	// ActualParameter param = null;
	// for (Map.Entry<String, String> e: params.entrySet()) {
	// String key = e.getKey();
	// String value = e.getValue();
	// if (key.startsWith(pbase)) {
	// SearchParameter sp = SearchParameterRegistry.getParameterDescriptor(pbase);
	// if (sp==null) {
	// throw new IllegalStateException("Parameter definition not found for: " +
	// pbase);
	// }
	// param = new ActualParameter(key, value);
	// param = parse(sp, param);
	// }
	// }
	// return param;
	// }

	private ActualParameter parse(SearchParameter sp, ActualParameter param) {
		String rawName = param.getRawName();
		String[] parts = parseParamName(rawName);
		String bn = parts[0];
		bn = bn.trim();
		String md = null;
		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed query parameter: " + rawName + ", expects: <name> or <name>:<modifier>");
		}
		if (parts.length == 2) {
			md = parts[1];
			md = md.trim();
			SearchParameter.Modifier m = sp.getModifier(md);
			if (m == null) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName);
			}
			param.setModifier(md);
			param.setEnumModifier(m);
			if (!sp.accept(m)) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName
						+ ", parameter:" + bn + ", does not accept [" + md + "]");
			}
		}
		param.setBaseName(bn.trim());
		// process comparator on value side
		Class<?> t = sp.getType();
		String value = param.getValue();
		String[] value_parts = new String[2];
		Object valObj = value;
		if (Number.class.isAssignableFrom(t)) {
			// for now only number and date can have comparator
			SearchParameter.Comparator c = sp.checkComparator(value, value_parts);
			if (c != null) {
				param.setEnumComparator(c);
			}
			param.setValueObject(parseNumeric(t, c != null ? value_parts[1] : value));
		} else if (Date.class.isAssignableFrom(t)) {
			SearchParameter.Comparator c = sp.checkComparator(value, value_parts);
			if (c != null) {
				param.setEnumComparator(c);
			}
			Date d = parseDate(c != null ? value_parts[1] : value);
			if (d == null) {
				throw new IllegalArgumentException("Query parameter:" + param.getBaseName()
						+ " expect date value in the format of: " + SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd
						+ " or " + SearchParameterRegistry.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or "
						+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or "
						+ SearchParameterRegistry.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
			}
			param.setValueObject(d);
		} else {
			// for now assume it is a string - no comparator
			param.setValueObject(valObj);
		}
		return param;
	}

	private Object parseNumeric(Class<?> type, String value) {
		Object parsedValue = null;
		// number parameter
		// BigInteger, Byte, Double, Float, Integer, Long, Short
		if (type.equals(Integer.class)) {
			parsedValue = Integer.valueOf(value);
		} else if (type.equals(Long.class)) {
			parsedValue = Long.valueOf(value);
		} else if (type.equals(Short.class)) {
			parsedValue = Short.valueOf(value);
		} else if (type.equals(Float.class)) {
			parsedValue = Float.valueOf(value);
		} else if (type.equals(Double.class)) {
			parsedValue = Double.valueOf(value);
		} else {
			throw new IllegalArgumentException(
					"Numeric parameter of type :" + type.getCanonicalName() + " not supported yet, value=" + value);
		}
		return parsedValue;
	}

	class ActualParameter {
		private String rawName;
		private String baseName;
		private String modifier;
		private SearchParameter.Modifier enumModifier;
		private String comparator;
		private SearchParameter.Comparator enumComparator;
		private String value;
		private Class<?> type;
		private Object valObj;

		public ActualParameter(String rawName, String value) {
			super();
			this.rawName = rawName;
			this.value = value;
		}

		public String getBaseName() {
			return baseName;
		}

		public void setBaseName(String baseName) {
			this.baseName = baseName;
		}

		public String getModifier() {
			return modifier;
		}

		public void setModifier(String modifier) {
			this.modifier = modifier;
		}

		public SearchParameter.Modifier getEnumModifier() {
			return enumModifier;
		}

		public void setEnumModifier(SearchParameter.Modifier enumModifier) {
			this.enumModifier = enumModifier;
		}

		public String getComparator() {
			return comparator;
		}

		public void setComparator(String comparator) {
			this.comparator = comparator;
		}

		public SearchParameter.Comparator getEnumComparator() {
			return enumComparator;
		}

		public void setEnumComparator(SearchParameter.Comparator enumComparator) {
			this.enumComparator = enumComparator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Object getValueObject() {
			return this.valObj;
		}

		public void setValueObject(Object value) {
			this.valObj = value;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public String getRawName() {
			return rawName;
		}

		public void setRawName(String rawName) {
			this.rawName = rawName;
		}
	}
}
