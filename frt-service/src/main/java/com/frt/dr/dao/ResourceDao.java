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
		
			List<ActualParameter> actualParams = new ArrayList<ActualParameter>();
			CriteriaQuery<Resource> cq = getQueryCriteria(em, resourceClazz, params, actualParams);
			Query query = em.createQuery(cq);
			
			Set<Parameter<?>> qparams = query.getParameters();
			
			if (qparams.size()>actualParams.size()) {
				System.out.println("Query parameters count: " + qparams.size() + " > actual parameters count: " + actualParams.size());
			}
			else if (qparams.size()<actualParams.size()) {
				System.out.println("Query parameters count: " + qparams.size() + " < actual parameters count: " + actualParams.size());
			}
			
			for (ActualParameter ap: actualParams) {
				if (ap.getType().equals(String.class)&&ap.getEnumModifier()==SearchParameter.Modifier.CONTAINS) {
					query.setParameter(ap.getBaseName(), this.convertToLikePattern(ap.getValueObject().toString()));
				}
				else {
					query.setParameter(ap.getBaseName(), ap.getValueObject());
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
			Class<T> resourceClazz, Map<String, String> params, List<ActualParameter> actualParamValues) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(resourceClazz);
		Root<T> rootResource = cq.from(resourceClazz);
		Predicate where = cb.conjunction();

		for (Map.Entry<Class<?>, List<String>> entityParamNames : SearchParameterRegistry.ENTITY_SEARCH_PARAMETERS
				.entrySet()) {
			Class<?> entity = entityParamNames.getKey();
			ActualParameter[] groupParam = new ActualParameter[1]; // used to bring out the only group param if present
			List<ActualParameter> actualParams = extractParameter(params, entityParamNames.getValue(), groupParam);
			if (actualParams!=null||groupParam[0]!=null) {
				if (entity.equals(resourceClazz)) {
					// parameter that is the resource's attribute
					if (actualParams!=null) {
						// there are parameters on resource (main table)
						where = addCriteria(where, cb, rootResource, null, actualParams, true);
						actualParamValues.addAll(actualParams);
					}
					else {
						// no parameter on resource
					}
				} else {
					// the predicate is on columns of refClass (secondary table)
					// a join is needed plus other expression
					String[] joinAttrs = SearchParameterRegistry.getJoinAttributes(resourceClazz, (Class<U>)entity);
					Metamodel m = em.getMetamodel();
					EntityType<T> resourceEntity_ = m.entity(resourceClazz);
					// handle one foreign key for now
					// think about chained join later if there are use cases
					Join<T, U> join = rootResource.join(resourceEntity_.getList(joinAttrs[0], (Class<U>)entity));
					// group parameter will be handled also if presents 
					if (groupParam[0]!=null) {
						where = addCriteriaForGroup(where, cb, join, actualParamValues, groupParam[0]); 
					}
					if (actualParams!=null) {
						where = addCriteria(where, cb, null, join, actualParams, true);
						actualParamValues.addAll(actualParams);
					}
				}
			}
		}
		cq.where(where);
		return cq;
	}

	private <T extends Resource, U extends ResourceComplexType> Predicate addCriteriaForGroup(
			Predicate where, CriteriaBuilder cb, Join<T, U> join,
			List<ActualParameter> actualParams, ActualParameter actualParameter) {
		SearchParameter sp = SearchParameterRegistry.getParameterDescriptor(actualParameter.getBaseName());
		String[] grpParams = ((GroupParameter)sp).getParameters();
		List<ActualParameter> grpActualParams = new ArrayList<ActualParameter>();
		ActualParameter ap = null;
		for (int i=0; i<grpParams.length; i++) {
			ap = new ActualParameter(grpParams[i], actualParameter.getValue());
			// mangle the param names coming from grp param expansion
			// to avoid query param placeholder conflict
			ap.setBaseName(actualParameter.getBaseName()+"_"+grpParams[i]);
			ap.setRawName(grpParams[i]);
			ap.setGroupName(actualParameter.getBaseName());
			ap.setComparator(actualParameter.getComparator());
			ap.setModifier(actualParameter.getModifier());
			ap.setEnumComparator(actualParameter.getEnumComparator());
			ap.setEnumModifier(actualParameter.getEnumModifier());
			ap.setType(actualParameter.getType());
			ap.setValueObject(actualParameter.getValueObject());
			if (sp.accept(SearchParameter.Modifier.CONTAINS)) {
				ap.setEnumModifier(SearchParameter.Modifier.CONTAINS); // for now all group search is string based, and use contains semantics
			}
			else {
				throw new IllegalArgumentException("Group search parameter : " + sp.getName() + " does not accept string match CONTAINS semantics.");
			}
			grpActualParams.add(ap);
		}
		// append newly expanded params
		actualParams.addAll(grpActualParams);
		// generate expression for the group param (OR'd predicates)
		Predicate expression = cb.disjunction();
		expression = addCriteria(expression, cb, null, join, grpActualParams, false);
		// AND'd to the main where expression
		return cb.and(where, expression);
	}

	/**
	 * 
	 * @param where
	 * @param cb
	 * @param join
	 * @param actualParams
	 * @param isConjunction
	 * @return root predicate - root node of AND/OR expression 
	 */
	private <T extends Resource, U extends ResourceComplexType> Predicate addCriteria(
			Predicate where, CriteriaBuilder cb, 
			Root<T> rootResource, Join<T, U> join,
			List<ActualParameter> actualParams, boolean isConjunction) {
		SearchParameter sp = null;
		Predicate term = null;
		for (ActualParameter ap : actualParams) {
			String baseName = ap.isPartOfGroup()?ap.getRawName():ap.getBaseName();
			String paramPlaceHolder = ap.getBaseName();
			sp = SearchParameterRegistry.getParameterDescriptor(baseName);
			Path<Object> path = null;
			if (rootResource!=null) {
				path = rootResource.get(sp.getFieldName());
			} else if (join!=null) {
				path = join.get(sp.getFieldName());
			}
			else {
				throw new IllegalArgumentException("Missing required parameters when generating query expression.");
			}
			if (ap.getType().equals(String.class)) {
				ParameterExpression<String> p = cb.parameter(String.class, paramPlaceHolder);
				if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
					term = cb.equal(path, p);
				} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
					term = cb.like(path.as(String.class), p);
				} else {
					throw new IllegalArgumentException(
							"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + baseName);
				}
			} else if (ap.getType().equals(Boolean.class)) {
				ParameterExpression<Boolean> p = cb.parameter(Boolean.class, paramPlaceHolder);
				if (ap.getEnumModifier() == null) {
					term = cb.equal(path, p);
				} else if (ap.getEnumModifier() == SearchParameter.Modifier.NOT) {
					term = cb.not(cb.equal(path.as(String.class), p));
				} else {
					throw new IllegalArgumentException(
							"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + baseName);
				}
			} else if (ap.getType().equals(Date.class)) {
				ParameterExpression<Date> p = cb.parameter(Date.class, paramPlaceHolder);
				if (ap.getEnumComparator() == null) {
					term = cb.equal(path, p);
				} else {
					switch (ap.getEnumComparator()) {
					case EQ:
						term = cb.equal(path, p);
						break;
					case NE:
						term = cb.notEqual(path, p);
						break;
					case GT:
						term = cb.greaterThan(path.as(Date.class), p);
						break;
					case GE:
						term = cb.greaterThanOrEqualTo(path.as(Date.class), p);
						break;
					case LT:
						term = cb.lessThan(path.as(Date.class), p);
						break;
					case LE:
						term = cb.lessThanOrEqualTo(path.as(Date.class), p);
						break;
					case SA:
						term = cb.greaterThan(path.as(Date.class), p);
						break;
					case EB:
						term = cb.lessThan(path.as(Date.class), p);
						break;
					case AP:
					default:
						throw new IllegalArgumentException(
								"Unsupported comparator : " + ap.getComparator() + ", for parameter: " + baseName);
					}
				}
			} else if (Number.class.isAssignableFrom(ap.getType())) {
				ParameterExpression<Number> p = cb.parameter(Number.class, paramPlaceHolder);
				if (ap.getEnumComparator() == null) {
					term = cb.equal(path, p);
				} else {
					switch (ap.getEnumComparator()) {
					case EQ:
						term = cb.equal(path, p);
						break;
					case NE:
						term = cb.notEqual(path, p);
						break;
					case GT:
						term = cb.gt(path.as(Number.class), p);
						break;
					case GE:
						term = cb.ge(path.as(Number.class), p);
						break;
					case LT:
						term = cb.lt(path.as(Number.class), p);
						break;
					case LE:
						term = cb.le(path.as(Number.class), p);
						break;
					case SA:
						term = cb.gt(path.as(Number.class), p);
						break;
					case EB:
						term = cb.lt(path.as(Number.class), p);
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
			where = isConjunction?cb.and(where, term):cb.or(where, term);
		}
		return where;
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

	/**
	 * parse query parameters into actual parameters where name, type, value, comparator, modifier etc are validated and 
	 * transformed into form that is easy to be consumed 
	 * @param queryParams
	 * @param entityParamNames
	 * @param groupParam
	 * @return
	 */
	private List<ActualParameter> extractParameter(Map<String, String> queryParams, List<String> entityParamNames, ActualParameter[] groupParam) {
		List<ActualParameter> actualParams = null;
		ActualParameter actualParam = null;
		for (String epname : entityParamNames) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				String key = e.getKey();
				String[] name_parts = parseParamName(key);
				String baseName = name_parts[0];
				String value = e.getValue();
				if (key.equals(epname)) {
					SearchParameter sp = SearchParameterRegistry.getParameterDescriptor(epname);
					if (sp == null) {
						throw new IllegalStateException("Parameter definition not found for: " + epname);
					}
					actualParam = new ActualParameter(key, value);
					actualParam = parse(sp, actualParam);
					if (actualParams == null) {
						actualParams = new ArrayList<ActualParameter>();
					}
					if (sp instanceof GroupParameter) {
						groupParam[0] = actualParam;
					}
					else {
						actualParams.add(actualParam);
					}
				}
			}
		}
		return actualParams;
	}

	private ActualParameter parse(SearchParameter sp, ActualParameter param) {
		String rawName = param.getRawName();
		String[] parts = parseParamName(rawName);
		String bn = parts[0];
		bn = bn.trim();
		String md = null;
		param.setType(sp.getType());
		
		if (parts.length != 1 && parts.length != 2) {
			throw new IllegalArgumentException(
					"Malformed query parameter: " + rawName + ", expects: <name> or <name>:<modifier>");
		}
		if (parts.length == 2) {
			md = parts[1];
			md = md.trim();
			SearchParameter.Modifier m = SearchParameterRegistry.getModifier(md);
			if (m == null) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName);
			}
			if (!sp.accept(m)) {
				throw new IllegalArgumentException("Invalid modifier[" + md + "] in query parameter: " + rawName
						+ ", parameter:" + bn + ", does not accept [" + md + "]");
			}
			param.setModifier(md);
			param.setEnumModifier(m);
		}
		param.setBaseName(bn.trim());
		// process comparator on value side
		Class<?> t = sp.getType();
		String value = param.getValue();
		String[] value_parts = new String[2];
		Object valObj = value;
		if (Number.class.isAssignableFrom(t)) {
			// for now only number and date can have comparator
			SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
			String realValStr = value;
			if (c != null) {
				String comparatorStr = value_parts[0];
				if (sp.accept(c)) {
					param.setComparator(comparatorStr);
					param.setEnumComparator(c);
					realValStr = value_parts[1];
				}
				else {
					throw new IllegalArgumentException("Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
							+ ", parameter:" + bn + ", does not accept [" + comparatorStr + "]");
				}
			}
			param.setValueObject(parseNumeric(t, realValStr));
		} else if (Date.class.isAssignableFrom(t)) {
			SearchParameter.Comparator c = SearchParameterRegistry.checkComparator(value, value_parts);
			String realValStr = value;
			if (c != null) {
				String comparatorStr = value_parts[0];
				if (sp.accept(c)) {
					param.setComparator(comparatorStr);
					param.setEnumComparator(c);
					realValStr = value_parts[1];
				}
				else {
					throw new IllegalArgumentException("Invalid comparator[" + comparatorStr + "] in query parameter: " + rawName
							+ ", parameter:" + bn + ", does not accept [" + comparatorStr + "]");
				}
			}
			Date d = parseDate(realValStr);
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
		private String rawName; // name from web query, name can be with modifier etc
		// if ActualParameter is created in group param expansion, the raw name is the bare param name 
		private String baseName; // name with modifiers etc stripped off, this can be prefixed with grp param
		private String grpName;
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

		public String getGroupName() {
			return grpName;
		}

		public void setGroupName(String grpName) {
			this.grpName = grpName;
		}

		public boolean isPartOfGroup() {
			boolean ret = false;
			if (this.grpName!=null) {
				ret = true;
			}
			return ret;
		}
	}
}
