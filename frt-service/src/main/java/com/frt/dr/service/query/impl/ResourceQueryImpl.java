/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.service.query.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
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
import com.frt.dr.service.query.CompositeParameter;
import com.frt.dr.service.query.GroupParameter;
import com.frt.dr.service.query.ResourceQuery;
import com.frt.dr.service.query.SearchParameter;
import com.frt.dr.service.query.SearchParameterRegistry;
import com.frt.dr.service.query.ResourceQueryUtils;

/**
 * ResourceQueryImpl class
 * @author jfu
 * @param <T>
 */
public class ResourceQueryImpl<T extends Resource> implements ResourceQuery<Resource> {
	private Class<T> resourceClazz;
	private Map<Class<?>, List<CompositeParameter>> parameters;
	private EntityManager em;
	private QUERY_STATES state;
	private Query query; // JPA query
	
	public ResourceQueryImpl(EntityManager em, Class<T> resourceClazz, Map<Class<?>, List<CompositeParameter>> parameters) {
		this.em = em;
		this.resourceClazz = resourceClazz;
		this.parameters = parameters;
		this.state = QUERY_STATES.CREATED;
	}
	
	@Override
	public void prepareQuery() {
		checkState(QUERY_STATES.CREATED);
		
		CriteriaQuery<T> cq = getQueryCriteria(resourceClazz, parameters);
		
		this.query = em.createQuery(cq);
		
		if (parameters==null&&parameters.size()==0) {
			this.query.setMaxResults(3);
		}
		
		Set<Parameter<?>> qparams = query.getParameters();

		Iterator<Class<?>> it = parameters.keySet().iterator();
		
		while (it.hasNext()) {
			Class<?> clazz = (Class<?>)it.next();
			List<CompositeParameter> paramsPerClazz = parameters.get(clazz);
			for (CompositeParameter ap : paramsPerClazz) {
				List<Object> valObjs = ap.getValueObject();
				Map<String, String> mvMapping = null;
				if (ap.getType().equals(String.class) && ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
					for (int i=0; i<valObjs.size(); i++) {
						Object value = valObjs.get(i);
						if (value instanceof Map) {
							mvMapping = (Map<String, String>)value;
							for (Map.Entry<String, String> e: mvMapping.entrySet()) {
								query.setParameter(e.getKey(), ResourceQueryUtils.convertToLikePattern(e.getValue()));
							}
						}
						else {
							query.setParameter(ResourceQueryUtils.getPlaceHolder(i, ap), ResourceQueryUtils.convertToLikePattern(value.toString()));
						}
					}
				} else {
					for (int i=0; i<valObjs.size(); i++) {
						Object value = valObjs.get(i);
						if (value instanceof Map) {
							mvMapping = (Map<String, String>)value;
							for (Map.Entry<String, String> e: mvMapping.entrySet()) {
								query.setParameter(e.getKey(), e.getValue());
							}
						}
						else {
							query.setParameter(ResourceQueryUtils.getPlaceHolder(i, ap), value);
						}
					}
				}
			}
		}
		this.state = QUERY_STATES.PREPARED;
	}
	
	@Override
	public Optional<List<Resource>> doQuery() {
		checkState(QUERY_STATES.PREPARED);
		List<Resource> resources = (List<Resource>) query.getResultList();
		
		Optional<List<Resource>> result = null;

		if (resources.size() > 0) {
			result = Optional.ofNullable(resources);
		} else {
			result = Optional.empty();
		}

		return result;
	}

	/**
	 * Generate JPA CriteriaQuery per REST query info
	 * 
	 * @param em EntityManager
	 * @param clazz
	 * @param params
	 * @return CriteriaQuery cq;
	 * 
	 */
	private <T extends Resource, U extends ResourceComplexType> CriteriaQuery<T> getQueryCriteria(Class<T> resourceClazz, Map<Class<?>, List<CompositeParameter>> parameters) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(resourceClazz);
		Root<T> rootResource = cq.from(resourceClazz);
		Predicate where = cb.conjunction();
		if (parameters!=null) {
			Iterator<Class<?>> it = parameters.keySet().iterator();
	
			while (it.hasNext()) {
				Class<?> clazz = (Class<?>)it.next();
				List<CompositeParameter> paramsPerClazz = parameters.get(clazz);
				if (clazz.equals(resourceClazz)) {
					// process search parameters on primary class (the FHIR resource)
					where = addCriteria(where, cb, rootResource, null, paramsPerClazz, true);
				} else {
					// process search parameters on child classes (the FHIR complex types)
					// a join is needed plus other expression
					String[] joinAttrs = SearchParameterRegistry.getJoinAttributes(resourceClazz, (Class<U>)clazz);
					Metamodel m = em.getMetamodel();
					EntityType<T> resourceEntity_ = m.entity(resourceClazz);
					// handle one foreign key for now
					// think about chained join later if there are use cases
					Join<T, U> join = rootResource.join(resourceEntity_.getList(joinAttrs[0], (Class<U>) clazz));
					where = addCriteria(where, cb, null, join, paramsPerClazz, true);
				}
			}
		}
		cq.where(where);
		return cq;
	}

	/**
	 * Generate JPA Query criteria for the given entity
	 * @param where
	 * @param cb
	 * @param join
	 * @param actualParams
	 * @param isConjunction
	 * @return root predicate - root node of AND/OR expression
	 */
	private <T extends Resource, U extends ResourceComplexType> Predicate addCriteria(Predicate where,
			CriteriaBuilder cb, Root<T> rootResource, Join<T, U> join, List<CompositeParameter> actualParams,
			boolean isConjunction) {
		SearchParameter sp = null;
		Predicate term = null;
		List<CompositeParameter> expandedParams = null;
		for (CompositeParameter ap : actualParams) {
			sp = SearchParameterRegistry.getParameterDescriptor(ap.getCannonicalName());
			// process group parameter
			if (sp instanceof GroupParameter) {
				expandedParams = expandGroupParameter(ap);
				// predicates from expanded parameters are OR'd
				// per FHIR collect parameter (string) matching semantics 
				Predicate expression = cb.disjunction();
				expression = addCriteria(expression, cb, null, join, expandedParams, false);
				// AND'd to the main expression
				where = cb.and(where, expression);
				continue;
			}
			
			// process field parameter
			Path<Object> path = null;
			if (rootResource != null) {
				path = rootResource.get(sp.getFieldName());
			} else if (join != null) {
				path = join.get(sp.getFieldName());
			} else {
				throw new IllegalArgumentException("Missing required parameters when generating query expression.");
			}

			List<Object> valObjs = ap.getValueObject();
			term = cb.conjunction();

			// value can be multiple - if multiple values present for a param, the semantics is AND'd
			if (ap.getType().equals(String.class)) {
				for (int i = 0; i < valObjs.size(); i++) {
					String value = (String)valObjs.get(i);
					String[] mv = value.split(",");
					if (mv.length==1) {
						term = addCompare(cb, term, path, i, -1, null, ap);
					} else {
						Predicate orExp = cb.disjunction();
						int index = 0;
						for (String v: mv) {
							orExp = addCompare(cb, orExp, path, i, index, v, ap);
							index++;
						}
						term = cb.and(term, orExp);
					}
				}
			} else if (ap.getType().equals(Boolean.class)) {
				for (int i = 0; i < valObjs.size(); i++) {
					ParameterExpression<Boolean> p = cb.parameter(Boolean.class, ResourceQueryUtils.getPlaceHolder(i, ap));
					if (ap.getEnumModifier() == null) {
						term = cb.equal(path, p);
					} else if (ap.getEnumModifier() == SearchParameter.Modifier.NOT) {
						term = cb.not(cb.equal(path.as(String.class), p));
					} else {
						throw new IllegalArgumentException(
								"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + ap.getCannonicalName());
					}
				}
			} else if (ap.getType().equals(Date.class)) {
				for (int i = 0; i < valObjs.size(); i++) {
					ParameterExpression<Date> p = null;
					if (ap.getEnumComparator() == null||ap.getEnumComparator().size()==0) {
						p = cb.parameter(Date.class, ResourceQueryUtils.getPlaceHolder(i, ap));
						term = cb.and(term, cb.equal(path, p));
					} else {
						p = cb.parameter(Date.class, ResourceQueryUtils.getPlaceHolder(i, ap));
						switch (ap.getEnumComparator().get(i)) {
						case EQ:
							term = cb.and(term, cb.equal(path, p));
							break;
						case NE:
							term = cb.and(term, cb.notEqual(path, p));
							break;
						case GT:
							term = cb.and(term, cb.greaterThan(path.as(Date.class), p));
							break;
						case GE:
							term = cb.and(term, cb.greaterThanOrEqualTo(path.as(Date.class), p));
							break;
						case LT:
							term = cb.and(term, cb.lessThan(path.as(Date.class), p));
							break;
						case LE:
							term = cb.and(term, cb.lessThanOrEqualTo(path.as(Date.class), p));
							break;
						case SA:
							term = cb.and(term, cb.greaterThan(path.as(Date.class), p));
							break;
						case EB:
							term = cb.and(term, cb.lessThan(path.as(Date.class), p));
							break;
						case AP:
						default:
							throw new IllegalArgumentException(
									"Unsupported comparator : " + ap.getComparator() + ", for parameter: " + ap.getCannonicalName());
						}
					}
				}
			} else if (Number.class.isAssignableFrom(ap.getType())) {
				for (int i = 0; i < valObjs.size(); i++) {
					ParameterExpression<Number> p = null;
					if (ap.getEnumComparator() == null||ap.getEnumComparator().size()==0) {
						p = cb.parameter(Number.class, ResourceQueryUtils.getPlaceHolder(i, ap));
						term = cb.and(term, cb.equal(path, p));
					} else {
						p = cb.parameter(Number.class, ResourceQueryUtils.getPlaceHolder(i, ap));
						switch (ap.getEnumComparator().get(i)) {
						case EQ:
							term = cb.and(term, cb.equal(path, p));
							break;
						case NE:
							term = cb.and(term, cb.notEqual(path, p));
							break;
						case GT:
							term = cb.and(term, cb.gt(path.as(Number.class), p));
							break;
						case GE:
							term = cb.and(term, cb.ge(path.as(Number.class), p));
							break;
						case LT:
							term = cb.and(term, cb.lt(path.as(Number.class), p));
							break;
						case LE:
							term = cb.and(term, cb.le(path.as(Number.class), p));
							break;
						case SA:
							term = cb.and(term, cb.gt(path.as(Number.class), p));
							break;
						case EB:
							term = cb.and(term, cb.lt(path.as(Number.class), p));
							break;
						case AP:
						default:
							throw new IllegalArgumentException(
									"Unsupported comparator : " + ap.getComparator() + ", for parameter: " + ap.getCannonicalName());
						}
					}
				}
			} else {
				throw new IllegalArgumentException("Unsupported parameter type: " + ap.getType().getCanonicalName()
						+ " for [" + ap.getCannonicalName() + "], supported types: string, date, numeric.");
			}
			// term contains the expression on current param
			// OR the terms - e.g. when params are expanded from a group param such as 'name' on entity HumanName
			where = isConjunction ? cb.and(where, term) : cb.or(where, term);
		}
		if (expandedParams!=null&&expandedParams.size()>0) {
			actualParams.addAll(expandedParams);
		}
		return where;
	}

	private Predicate addCompare(CriteriaBuilder cb, Predicate term, Path<Object> path, int i, int j, String v, CompositeParameter ap) {
		Predicate result = null;
		String mangledParamName = ResourceQueryUtils.getPlaceHolder(i, j, ap);
		ParameterExpression<String> p = cb.parameter(String.class, mangledParamName);
		if (j>=0) {
			// MV
			ap.setMV(i, mangledParamName, v);
			if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
				result = cb.or(term, cb.equal(path, p));
			} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
				result = cb.or(term, cb.like(path.as(String.class), p));
			} else {
				throw new IllegalArgumentException(
					"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + ap.getCannonicalName());
			}
		}
		else {
			if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
				result = cb.and(term, cb.equal(path, p));
			} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
				result = cb.and(term, cb.like(path.as(String.class), p));
			} else {
				throw new IllegalArgumentException(
					"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + ap.getCannonicalName());
			}
		}
		return result;
	}

	/**
	 * generate OR'd string comparison predicates
	 * @param term 
	 * @param cb 
	 * @param path 
	 * @param valObjs - actual param value
	 * @param i - position of value (may be a comma delimited multi-value);
	 * @param ap 
	 * @return 
	 */
	private List<CompositeParameter> processMultiValues(CriteriaBuilder cb, Predicate term, Path<Object> path, List<Object> valObjs, int i, CompositeParameter ap) {
		String value = (String)valObjs.get(i);
		String[] mv = value.split(",");
		List<CompositeParameter> pl = new ArrayList<CompositeParameter>();
		for (int j=0; j<mv.length; j++) {
			ParameterExpression<String> p = cb.parameter(String.class, ResourceQueryUtils.getPlaceHolder(i, ap));
			if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
				term = cb.and(term, cb.equal(path, p));
			} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
				term = cb.and(term, cb.like(path.as(String.class), p));
			} else {
				throw new IllegalArgumentException(
						"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + ap.getCannonicalName());
			}
		}
		return null;
	}

	/**
	 * Expand group parameter into its corresponding field parameter(s)
	 * @param CompositeParameter
	 * @return - the field parameter(s) from expansion
	 */
	private List<CompositeParameter> expandGroupParameter(CompositeParameter CompositeParameter) {
		SearchParameter paramDef = SearchParameterRegistry.getParameterDescriptor(CompositeParameter.getBaseName());
		String[] groupParams = ((GroupParameter)paramDef).getParameters();
		List<CompositeParameter> expandedActualParams = new ArrayList<CompositeParameter>();
		CompositeParameter ap = null;
		for (int i = 0; i < groupParams.length; i++) {
			ap = new CompositeParameter(groupParams[i], CompositeParameter.getValues());
			// mangle the param names coming from group param expansion
			// to avoid query parameter placeholder conflict
			ap.setBaseName(CompositeParameter.getBaseName() + "_" + groupParams[i]);
			ap.setRawName(groupParams[i]);
			ap.setGroupName(CompositeParameter.getBaseName());
			ap.setComparator(CompositeParameter.getComparator());
			ap.setModifier(CompositeParameter.getModifier());
			ap.setEnumComparator(CompositeParameter.getEnumComparator());
			ap.setEnumModifier(CompositeParameter.getEnumModifier());
			ap.setType(CompositeParameter.getType());
			ap.setValueObject(CompositeParameter.getValueObject());
			if (paramDef.accept(SearchParameter.Modifier.CONTAINS)) {
				// for now all group search is string based, and use contains semantics
				ap.setEnumModifier(SearchParameter.Modifier.CONTAINS);
			} else {
				throw new IllegalArgumentException("Group search parameter : " + paramDef.getName()
						+ " does not accept string match CONTAINS semantics.");
			}
			expandedActualParams.add(ap);
		}
		return expandedActualParams;
	}

	/**
	 * helper - check current state of ResourceQuery instance and throw invalid state exception
	 * @param expected - the expected state
	 */
	private void checkState(QUERY_STATES expected) {
		if (state!=expected) {
			throw new IllegalStateException("Expect query state: " + expected + ", current query state: " + state);
		}
	}

}
