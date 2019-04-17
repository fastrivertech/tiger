package com.frt.dr.service.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Properties;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientAddress;

public class ResourceQueryBuilder {

	public static void main(String[] args) {
		try {
			Properties pros = new Properties();
			pros.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, 
			                 "./persistence.xml");
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("FRT_DR_LOCAL_PERSISTENCE", pros);
			EntityManager em = emf.createEntityManager();	
			CriteriaBuilder builder = emf.getCriteriaBuilder();
			
			CriteriaQuery<Patient> criteria = builder.createQuery(Patient.class);
			Root<Patient> resourceRoot = criteria.from(Patient.class);
			
			Metamodel m = em.getMetamodel();
			EntityType<Patient> Patient_ = m.entity(Patient.class);
			
			// 1 select * from * where patient.gender = ?
			//Predicate where = builder.equal(resourceRoot.get("gender"), new String("male"));
			
			// 2.1 select * from * where patient.gender = ? and/or patient.active = ? 
			//Predicate where = builder.and(builder.equal(resourceRoot.get("gender"), new String("male")), 
			//							  builder.equal(resourceRoot.get("active"), Boolean.valueOf(true)));
			
			// 2.2 select * from * where patient.gender = ? and/or patient.active = ? 
			//Predicate where = builder.or(builder.equal(resourceRoot.get("gender"), new String("male")), 
			//  						   builder.equal(resourceRoot.get("active"), Boolean.valueOf(true)));
			
			// 3 select * from * where patient.name.family = ?
			//Join<Patient,PatientHumanName> name = resourceRoot.join(Patient_.getList("names", PatientHumanName.class));
			//Predicate where = builder.and(builder.equal(name.get("family"), new String("ChalmersNmjdjdejwbn89")),
			//							  builder.equal(name.get("path"), new String("Patient.name")));
			
			// 4 select * from * where patient.gender = ? and/or patient.name.family = ?
			//Join<Patient,PatientHumanName> name = resourceRoot.join(Patient_.getList("names", PatientHumanName.class));
			//Predicate where = builder.and(builder.equal(resourceRoot.get("gender"), new String("male")),
			//							  builder.equal(name.get("family"), new String("ChalmersNmjdjdejwbn89")));
			
			// 5 select * from * where patient.name.given = ?
			//Join<Patient,PatientHumanName> name = resourceRoot.join(Patient_.getList("names", PatientHumanName.class));
			//Predicate where = builder.and(builder.like(name.get("given"), new String("%Jim%")),
			//						      builder.equal(name.get("path"), new String("Patient.name")));
						
			// 6 select * from * where patient.gender = ? and/or patient.name.family = ?
			Join<Patient,PatientHumanName> name = resourceRoot.join(Patient_.getList("names", PatientHumanName.class), JoinType.LEFT);
			Predicate where = builder.and(builder.equal(resourceRoot.get("gender"), new String("male")),
										  builder.in(name.get("use")).value("official").value("usual"));
			
			// 7 select * from * where patient.gender = ? and/or patient.name.family = ?
			//Join<Patient,PatientHumanName> name = resourceRoot.join(Patient_.getList("names", PatientHumanName.class), JoinType.INNER);
			//Join<Patient,PatientAddress> address = resourceRoot.join(Patient_.getList("addresses", PatientAddress.class), JoinType.INNER);
			
			//Predicate where = builder.and(builder.equal(resourceRoot.get("gender"), new String("male")),
			//							  builder.in(name.get("use")).value("official").value("usual"),
			//							  builder.equal(address.get("state"), new String("Vic")));
			
			criteria.where(where);
			criteria.select(resourceRoot);
		  //criteria.distinct(true);
			TypedQuery<Patient> query = em.createQuery(criteria).setHint("org.eclipse.persistence.config", "EXISTS");
			List<Patient> patients = query.getResultList();
			patients.forEach(patient->{
				System.out.println("retrieved: " + patient.getId());				
			});			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
