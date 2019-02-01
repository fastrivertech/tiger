package com.frt.dr.service.query.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.frt.dr.service.query.ActualParameter;
import com.frt.dr.service.query.GroupParameter;
import com.frt.dr.service.query.ResourceQuery;
import com.frt.dr.service.query.SearchParameter;
import com.frt.dr.service.query.SearchParameterRegistry;
import com.frt.dr.service.query.SearchParameterUtils;

public class ResourceQueryImpl<T extends Resource> implements ResourceQuery<Resource> {
	private Class<T> resourceClazz;
	private Map<Class<?>, List<ActualParameter>> parameters;
	private EntityManager em;
	private QUERY_STATES state;
	private Query query; // JPA query
	
	public ResourceQueryImpl(EntityManager em, Class<T> resourceClazz, Map<Class<?>, List<ActualParameter>> parameters) {
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

		Set<Parameter<?>> qparams = query.getParameters();

		if (qparams.size() > parameters.size()) {
			System.out.println("Query parameters count: " + qparams.size() + " > actual parameters count: "
					+ parameters.size());
		} else if (qparams.size() < parameters.size()) {
			System.out.println("Query parameters count: " + qparams.size() + " < actual parameters count: "
					+ parameters.size());
		}

		Iterator<Class<?>> it = parameters.keySet().iterator();
		
		while (it.hasNext()) {
			Class<?> clazz = (Class<?>)it.next();
			List<ActualParameter> paramsPerClazz = parameters.get(clazz);
			for (ActualParameter ap : paramsPerClazz) {
				List<Object> valObjs = ap.getValueObject();
				if (ap.getType().equals(String.class) && ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
					for (int i=0; i<valObjs.size(); i++) {
						query.setParameter(SearchParameterUtils.getPlaceHolder(i, ap), SearchParameterUtils.convertToLikePattern(ap.getValueObject().get(i).toString()));
					}
				} else {
					for (int i=0; i<valObjs.size(); i++) {
						query.setParameter(SearchParameterUtils.getPlaceHolder(i, ap), ap.getValueObject().get(i));
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
	private <T extends Resource, U extends ResourceComplexType> CriteriaQuery<T> getQueryCriteria(Class<T> resourceClazz, Map<Class<?>, List<ActualParameter>> parameters) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(resourceClazz);
		Root<T> rootResource = cq.from(resourceClazz);
		Predicate where = cb.conjunction();

		Iterator<Class<?>> it = parameters.keySet().iterator();
		
		while (it.hasNext()) {
			Class<?> clazz = (Class<?>)it.next();
			List<ActualParameter> paramsPerClazz = parameters.get(clazz);
			if (clazz.equals(resourceClazz)) {
				// process search parameters on main class (the FHIR resource)
				where = addCriteria(where, cb, rootResource, null, paramsPerClazz, true);
			} else {
				// process search parameters on associated class (the FHIR complex types)
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
			CriteriaBuilder cb, Root<T> rootResource, Join<T, U> join, List<ActualParameter> actualParams,
			boolean isConjunction) {
		SearchParameter sp = null;
		Predicate term = null;
		List<ActualParameter> expandedParams = null;
		for (ActualParameter ap : actualParams) {
			sp = SearchParameterRegistry.getParameterDescriptor(ap.getCannonicalName());
			// process group parameter
			if (sp instanceof GroupParameter) {
				expandedParams = expandGroupParameter(ap);
				// predicates from expanded parameters are OR'd
				// per FHIR collect parameter (string) matching semantics 
				Predicate expression = cb.disjunction();
				expression = addCriteria(expression, cb, null, join, expandedParams, false);
				// AND'd to the main expression
				cb.and(where, expression);
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

			List valObjs = ap.getValueObject();
			term = cb.conjunction();

			// value can be multiple - if multiple values present for a param, the semantics is AND'd
			if (ap.getType().equals(String.class)) {
				for (int i = 0; i < valObjs.size(); i++) {
					ParameterExpression<String> p = cb.parameter(String.class, SearchParameterUtils.getPlaceHolder(i, ap));
					if (ap.getEnumModifier() == null || ap.getEnumModifier() == SearchParameter.Modifier.EXACT) {
						term = cb.and(term, cb.equal(path, p));
					} else if (ap.getEnumModifier() == SearchParameter.Modifier.CONTAINS) {
						term = cb.and(term, cb.like(path.as(String.class), p));
					} else {
						throw new IllegalArgumentException(
								"Unsupported modifier: " + ap.getModifier() + ", for parameter: " + ap.getCannonicalName());
					}
				}
			} else if (ap.getType().equals(Boolean.class)) {
				for (int i = 0; i < valObjs.size(); i++) {
					ParameterExpression<Boolean> p = cb.parameter(Boolean.class, SearchParameterUtils.getPlaceHolder(i, ap));
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
						p = cb.parameter(Date.class, SearchParameterUtils.getPlaceHolder(i, ap));
						term = cb.and(term, cb.equal(path, p));
					} else {
						p = cb.parameter(Date.class, SearchParameterUtils.getPlaceHolder(i, ap));
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
						p = cb.parameter(Number.class, SearchParameterUtils.getPlaceHolder(i, ap));
						term = cb.and(term, cb.equal(path, p));
					} else {
						p = cb.parameter(Number.class, SearchParameterUtils.getPlaceHolder(i, ap));
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

	/**
	 * Expand group parameter into its corresponding field parameter(s)
	 * @param actualParameter
	 * @return - the field parameter(s) from expansion
	 */
	private List<ActualParameter> expandGroupParameter(ActualParameter actualParameter) {
		SearchParameter paramDef = SearchParameterRegistry.getParameterDescriptor(actualParameter.getBaseName());
		String[] groupParams = ((GroupParameter)paramDef).getParameters();
		List<ActualParameter> expandedActualParams = new ArrayList<ActualParameter>();
		ActualParameter ap = null;
		for (int i = 0; i < groupParams.length; i++) {
			ap = new ActualParameter(groupParams[i], actualParameter.getValues());
			// mangle the param names coming from group param expansion
			// to avoid query parameter placeholder conflict
			ap.setBaseName(actualParameter.getBaseName() + "_" + groupParams[i]);
			ap.setRawName(groupParams[i]);
			ap.setGroupName(actualParameter.getBaseName());
			ap.setComparator(actualParameter.getComparator());
			ap.setModifier(actualParameter.getModifier());
			ap.setEnumComparator(actualParameter.getEnumComparator());
			ap.setEnumModifier(actualParameter.getEnumModifier());
			ap.setType(actualParameter.getType());
			ap.setValueObject(actualParameter.getValueObject());
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
