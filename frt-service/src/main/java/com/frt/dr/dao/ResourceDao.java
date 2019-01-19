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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import com.frt.dr.model.Resource;

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
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery cq = cb.createQuery();
			Root root = cq.from(com.frt.dr.model.base.Patient.class);
			// Query query = em.createNamedQuery("getResourceById");
			// query.setParameter("id", id);
			Predicate where = genWhere(cq, cb, root, com.frt.dr.model.base.Patient.class, params);
			if (where != null) {
				cq.where(where);
			}
			Query query = em.createQuery(cq);
			Set<Parameter<?>> qparams = query.getParameters();
			Iterator<Parameter<?>> pit = qparams.iterator();
			while (pit.hasNext()) {
				Parameter qparam = pit.next();
				System.out.println("param: class:" + qparam.getClass() + ", name:" + qparam.getName() + ", position:"
						+ qparam.getPosition() + ", type:" + qparam.getParameterType());
				String value = (String) params.get(qparam.getName());
				if (value != null) {
					if (qparam.getParameterType().equals(String.class)) {
						query.setParameter(qparam.getName(), value);
					} else if (qparam.getParameterType().equals(Date.class)) {
						// date param
						Date d = null;
						for (int i=0; i<BaseDao.DF_FMT_SUPPORTED.length; i++) {
							try {
								d = BaseDao.DF_FMT_SUPPORTED[i].parse(value);
								break;
							} catch (ParseException e) {
								continue;
							}
						}
						if (d!=null) {
							query.setParameter(qparam.getName(), d);
						}
						else {
							throw new IllegalArgumentException("Query parameter:" + qparam.getName() + " expect date value in the format of: " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd + " or " + BaseDao.PARAM_DATE_FMT_yyyy_MM_dd_T_HH_mm_ss + " or " + BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + " or " + BaseDao.PARAM_DATE_FMT_dd_s_MM_s_yyyy + ", value=" + value);
						}
					} else if (qparam.getParameterType().equals(Boolean.class)) {
						Boolean b = false;
						if (value.equalsIgnoreCase("true")) {
							b = true;
						}
						else if (value.equalsIgnoreCase("false")) {
							b = false;
						}
						else {
							throw new IllegalArgumentException("Query parameter:" + qparam.getName() + " expect true/false, value=" + value);
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
							throw new IllegalArgumentException("Numeric parameter of type :" + qparam.getParameterType().getClass().getCanonicalName() + " not supported yet, value=" + value);
						}
					}
				}
				else {
					throw new IllegalStateException("Encountered query parameter: " + qparam.getName() + ", which is not among the request query parameters.");
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
	 * target value
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
	 * @param cq
	 * @param cb
	 * @param root
	 * @param clazz
	 * @param params
	 * @return
	 */
	private Predicate genWhere(CriteriaQuery cq, CriteriaBuilder cb, Root rootEntity, Class ResourceClazz,
			Map<String, String> params) {
		Predicate where = cb.conjunction();
		if (ResourceClazz.equals(com.frt.dr.model.base.Patient.class)) {
			// attribute of Entity: _id, Resource ID (logical) 64 VARCHAR
			for (Map.Entry<String, String> e : params.entrySet()) {
				String key = e.getKey();
				String value = e.getValue();
				if (key.equals("_id")) {
					where = cb.and(where, cb.equal(rootEntity.get("id"), cb.parameter(String.class, "_id")));
				}
				if (key.equals("active")) {
					where = cb.and(where, cb.equal(rootEntity.get("active"), cb.parameter(Boolean.class, "active")));
				}
				if (key.equals("birthdate")) {
					where = cb.and(where, cb.equal(rootEntity.get("birthDate"), cb.parameter(Date.class, "birthdate")));
				}
				if (key.equals("gender")) {
					where = cb.and(where, cb.equal(rootEntity.get("gender"), cb.parameter(String.class, "gender")));
				}

			}
			// attributes from associated entity : PatientAddress
			// captureParam(cq, cb, root, predicates, params, new String[] {"address",
			// "address-city", "address-country", "address-postalcode", "address-state"},
			// new Class[] {String.class, String.class, String.class, String.class,
			// String.class}, com.frt.dr.model.base.PatientAddress.class);
			/**
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
			 */

			// name - string A server defined search that may match any of the string fields
			// in the HumanName, including family, give, prefix, suffix, suffix, and/or text
			// Patient.name
			// attribute in associated Entity: e.g. PatientHumanName
			// captureParam(cq, cb, root, predicates, params, new String[] {"name", "given",
			// "family"}, new Class[] {String.class, String.class, String.class},
			// com.frt.dr.model.base.PatientHumanName.class);

			// attribute in associated Entity: e.g. PatientIdentifier
			// captureParam(cq, cb, root, predicates, params, new String[] {"identifier"},
			// new Class[] {String.class}, com.frt.dr.model.base.PatientIdentifier.class);

			// attribute in associated Entity: e.g. PatientContactPoint
			// captureParam(cq, cb, root, predicates, params, new String[] {"telecom"}, new
			// Class[] {String.class}, com.frt.dr.model.base.PatientContactPoint.class);
		} else {
			throw new UnsupportedOperationException("Query with parameters on resource: "
					+ ResourceClazz.getClass().getCanonicalName() + " not implemented yet.");
		}
		return where;
	}

	private <T extends com.frt.dr.model.ResourceComplexType> void captureParam(CriteriaQuery cq, CriteriaBuilder cb,
			Root root, List<Predicate> predicates, Map params, String[] paramNames, Class[] paramTypes,
			Class<T> refClazz) {
		Subquery<BigInteger> subQuery = cq.subquery(BigInteger.class);
		List<Predicate> subCond = new ArrayList<Predicate>();
		if (refClazz.equals(com.frt.dr.model.base.PatientAddress.class)) {
			Root<com.frt.dr.model.base.PatientAddress> subRoot = subQuery
					.from(com.frt.dr.model.base.PatientAddress.class);
			subQuery.select(subRoot.<BigInteger>get("patient"));
			for (int i = 0; i < paramNames.length; i++) {
				if (paramNames[i].equals("address")) {
					Predicate p1 = cb.like(subRoot.get("city"), cb.parameter(String.class, "city"));
					Predicate p2 = cb.like(subRoot.get("country"), cb.parameter(String.class, "country"));
					Predicate p3 = cb.like(subRoot.get("postalcode"), cb.parameter(String.class, "postalcode"));
					Predicate p4 = cb.like(subRoot.get("state"), cb.parameter(String.class, "state"));
					subCond.add((cb.or(cb.or(cb.or(p1, p2), p3), p4)));
				} else if (paramNames[i].endsWith("-city")) {
					subCond.add(cb.like(subRoot.get("city"), cb.parameter(String.class, "city")));
				} else if (paramNames[i].endsWith("-country")) {
					subCond.add(cb.like(subRoot.get("country"), cb.parameter(String.class, "country")));
				} else if (paramNames[i].endsWith("-postalcode")) {
					subCond.add(cb.like(subRoot.get("postalcode"), cb.parameter(String.class, "postalcode")));
				} else if (paramNames[i].endsWith("-state")) {
					subCond.add(cb.like(subRoot.get("state"), cb.parameter(String.class, "state")));
				} else {
					throw new IllegalArgumentException("Invalid parameter: " + paramNames[i] + ", for sub query on : "
							+ refClazz.getCanonicalName());
				}
			}
			Predicate subWhere = cb.conjunction();
			for (Predicate p : subCond) {
				subWhere = cb.and(subWhere, p);
			}
			subQuery.where(subWhere);
			predicates.add(root.get("resourceId").in(subQuery));
		} else if (refClazz.equals(com.frt.dr.model.base.PatientIdentifier.class)) {
			Root<com.frt.dr.model.base.PatientIdentifier> subRoot = subQuery
					.from(com.frt.dr.model.base.PatientIdentifier.class);
			subQuery.select(subRoot.<BigInteger>get("patient"));
			for (int i = 0; i < paramNames.length; i++) {
				if (paramNames[i].equals("identifier")) {
					Predicate p1 = cb.like(subRoot.get("use"), cb.parameter(String.class, "use"));
					Predicate p2 = cb.like(subRoot.get("system"), cb.parameter(String.class, "system"));
					Predicate p3 = cb.like(subRoot.get("value"), cb.parameter(String.class, "value"));
					subCond.add(cb.or(cb.or(cb.or(p1, p2), p3)));
				} else {
					throw new IllegalArgumentException("Invalid parameter: " + paramNames[i] + ", for sub query on : "
							+ refClazz.getCanonicalName());
				}
			}
			Predicate subWhere = cb.conjunction();
			for (Predicate p : subCond) {
				subWhere = cb.and(subWhere, p);
			}
			subQuery.where(subWhere);
			predicates.add(root.get("resourceId").in(subQuery));
		} else if (refClazz.equals(com.frt.dr.model.base.PatientHumanName.class)) {
			Root<com.frt.dr.model.base.PatientHumanName> subRoot = subQuery
					.from(com.frt.dr.model.base.PatientHumanName.class);
			subQuery.select(subRoot.<BigInteger>get("patient"));
			for (int i = 0; i < paramNames.length; i++) {
				if (paramNames[i].equals("name")) {
					Predicate p1 = cb.like(subRoot.get("family"), cb.parameter(String.class, "family"));
					Predicate p2 = cb.like(subRoot.get("given"), cb.parameter(String.class, "given"));
					Predicate p3 = cb.like(subRoot.get("prefix"), cb.parameter(String.class, "prefix"));
					Predicate p4 = cb.like(subRoot.get("suffix"), cb.parameter(String.class, "suffix"));
					subCond.add((cb.or(cb.or(cb.or(p1, p2), p3), p4)));
				} else if (paramNames[i].equals("family")) {
					subCond.add(cb.like(subRoot.get("family"), cb.parameter(String.class, "family")));
				} else if (paramNames[i].endsWith("given")) {
					subCond.add(cb.like(subRoot.get("given"), cb.parameter(String.class, "given")));
				} else if (paramNames[i].endsWith("prefix")) {
					subCond.add(cb.like(subRoot.get("prefix"), cb.parameter(String.class, "prefix")));
				} else if (paramNames[i].endsWith("suffix")) {
					subCond.add(cb.like(subRoot.get("suffix"), cb.parameter(String.class, "suffix")));
				} else {
					throw new IllegalArgumentException("Invalid parameter: " + paramNames[i] + ", for sub query on : "
							+ refClazz.getCanonicalName());
				}
			}
			Predicate subWhere = cb.conjunction();
			for (Predicate p : subCond) {
				subWhere = cb.and(subWhere, p);
			}
			subQuery.where(subWhere);
			predicates.add(root.get("resourceId").in(subQuery));
		} else if (refClazz.equals(com.frt.dr.model.base.PatientContactPoint.class)) {
			Root<com.frt.dr.model.base.PatientContactPoint> subRoot = subQuery
					.from(com.frt.dr.model.base.PatientContactPoint.class);
			subQuery.select(subRoot.<BigInteger>get("patient"));
			for (int i = 0; i < paramNames.length; i++) {
				if (paramNames[i].equals("telecom")) {
					Predicate p1 = cb.like(subRoot.get("system"), cb.parameter(String.class, "system"));
					Predicate p2 = cb.like(subRoot.get("value"), cb.parameter(String.class, "value"));
					subCond.add(cb.or(p1, p2));
				} else {
					throw new IllegalArgumentException("Invalid parameter: " + paramNames[i] + ", for sub query on : "
							+ refClazz.getCanonicalName());
				}
			}
			Predicate subWhere = cb.conjunction();
			for (Predicate p : subCond) {
				subWhere = cb.and(subWhere, p);
			}
			subQuery.where(subWhere);
			predicates.add(root.get("resourceId").in(subQuery));
		} else {
			throw new IllegalArgumentException("Invalid Complex type for the search : " + refClazz.getCanonicalName());
		}
	}

	/**
	 * add predicate of a field of scalar type
	 * 
	 * @param cb
	 * @param root
	 * @param predicates
	 * @param params
	 * @param paramName
	 * @param paramType
	 */
	private void captureParam(CriteriaBuilder cb, Root root, List<Predicate> predicates, Map params, String paramName,
			Class paramType) {
		String value = getParamValue(params, paramName);
		if (value != null && !value.isEmpty()) {

			if (paramType.equals(Boolean.class)) {
				if (value.startsWith(PARAM_PREFIX_NE)) {
					predicates.add(cb.notEqual(root.get(paramName), cb.parameter(paramType, paramName)));
				} else {
					predicates.add(cb.equal(root.get(paramName), cb.parameter(paramType, paramName)));
				}
			}

			if (paramType.equals(Integer.class) || paramType.equals(Long.class) || paramType.equals(Double.class)
					|| paramType.equals(Number.class) || paramType.equals(BigInteger.class)) {
				if (value.startsWith(PARAM_PREFIX_NE)) {
					predicates.add(cb.notEqual(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_EQ)) {
					predicates.add(cb.equal(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_GE)) {
					predicates.add(cb.greaterThanOrEqualTo(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_GT)) {
					predicates.add(cb.greaterThan(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_LE)) {
					predicates.add(cb.lessThanOrEqualTo(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_LT)) {
					predicates.add(cb.lessThan(root.get(paramName), cb.parameter(paramType, paramName)));
				} else {
					predicates.add(cb.equal(root.get(paramName), cb.parameter(paramType, paramName)));
				}
			}

			if (paramType.equals(Date.class)) {
				if (value.startsWith(PARAM_PREFIX_NE)) {
					predicates.add(cb.notEqual(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_EQ)) {
					predicates.add(cb.equal(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_GE)) {
					predicates.add(cb.greaterThanOrEqualTo(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_GT)) {
					predicates.add(cb.greaterThan(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_LE)) {
					predicates.add(cb.lessThanOrEqualTo(root.get(paramName), cb.parameter(paramType, paramName)));
				} else if (value.startsWith(PARAM_PREFIX_LT)) {
					predicates.add(cb.lessThan(root.get(paramName), cb.parameter(paramType, paramName)));
				} else {
					predicates.add(cb.like(root.get(paramName), cb.parameter(paramType, paramName)));
				}
			}

			if (paramType.equals(String.class)) {
				// name=eve - start with 'eve'
				// name:exact=steven
				// name:contains=eve
				String[] names = paramName.split(":");
				String realName = names[0];
				if (value.startsWith(PARAM_PREFIX_NE)) {
					predicates.add(cb.notLike(root.get(realName), cb.parameter(paramType, realName)));
				} else if (value.startsWith(PARAM_PREFIX_EQ)) {
					predicates.add(cb.like(root.get(realName), cb.parameter(paramType, realName)));
				} else if (value.startsWith(PARAM_PREFIX_GE)) {
					predicates.add(cb.greaterThanOrEqualTo(root.get(realName), cb.parameter(paramType, realName)));
				} else if (value.startsWith(PARAM_PREFIX_GT)) {
					predicates.add(cb.greaterThan(root.get(realName), cb.parameter(paramType, realName)));
				} else if (value.startsWith(PARAM_PREFIX_LE)) {
					predicates.add(cb.lessThanOrEqualTo(root.get(realName), cb.parameter(paramType, realName)));
				} else if (value.startsWith(PARAM_PREFIX_LT)) {
					predicates.add(cb.lessThan(root.get(realName), cb.parameter(paramType, realName)));
				} else {
					predicates.add(cb.like(root.get(realName), cb.parameter(paramType, realName)));
				}
			}
		}
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
