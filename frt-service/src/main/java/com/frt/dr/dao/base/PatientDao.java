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
package com.frt.dr.dao.base;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import java.sql.Types;
import java.sql.SQLException;
import java.lang.IllegalStateException;
import javax.persistence.Query;
import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.PessimisticLockException;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import com.frt.dr.model.base.Patient;
import com.frt.dr.dao.BaseDao;
import com.frt.dr.dao.DaoException;

/**
 * PatientDao class
 * @author chaye
 */
@Transactional
@Repository
public class PatientDao extends BaseDao<Patient,String> {
		
	public PatientDao() {	
	}
	
	@Override	
	public Optional<Patient> save(Patient patient) 
		throws DaoException {	
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			
			if (patient.getNames()!=null && 
				patient.getNames().size()>0) {
				patient.getNames().forEach(name->name.setHumannameId(null));
			}
			if (patient.getIdentifiers()!=null &&
				patient.getIdentifiers().size()>0) {
				patient.getIdentifiers().forEach(identifier->identifier.setIdentifierId(null));
			}
			if (patient.getAddresses()!=null &&
				patient.getAddresses().size()>0) {
				patient.getAddresses().forEach(address->address.setAddressId(null));
			}
			if (patient.getMaritalStatus()!=null) {
				patient.getMaritalStatus().setCodeableconceptId(null);
				patient.getMaritalStatus().setPath("patient.maritalStatus");
			}

			if (patient.getManagingOrganization()!=null) {
				patient.getManagingOrganization().setReferenceId(null);
				patient.getManagingOrganization().setPath("patient.managingOrganization");
			}
	
			em.persist(patient);
			transaction.commit();
			return Optional.of(patient);
		} catch (IllegalStateException |
				 RollbackException ex) {
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
	public Optional<Patient> findById(String id) 
		throws DaoException {
		try {
	   	    Query query = em.createNamedQuery("getPatientById");
            query.setParameter("id", id);
            List<Patient> patients = (List<Patient>) query.getResultList();          							
            Optional<Patient> patient = null;
            if (patients.size()>0) {
            	patient = Optional.ofNullable(patients.get(0));
            } else {
            	patient = Optional.empty();
            }
			return patient;
		} catch (IllegalArgumentException | 
				 QueryTimeoutException |
				 TransactionRequiredException |
				 PessimisticLockException |
				 LockTimeoutException ex) {
			throw new DaoException(ex);			
		} catch (PersistenceException ex) {
			throw new DaoException(ex);						
		}
	}
}
