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
package com.frt.dr.transaction.model;

import java.sql.Timestamp;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Size;
import com.frt.dr.model.DomainResource;
import com.frt.dr.model.base.Patient;

/**
 * PatientTransaction class
 * @author cqye
 */
@Entity
@Table(name = "PATIENT_TRANSACTION")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(name = "PATIENT_TRANSACTION_SEQ", sequenceName = "PATIENT_TRANSACTION_SEQ", allocationSize = 100)
@NamedQueries({
	@NamedQuery(name = "PatientTransaction.getPatientTransactionById", query = "SELECT T FROM PatientTransaction T WHERE T.transactionId = :id"),	
    @NamedQuery(name = "PatientTransaction.getPatientTransactionByResourceId", query = "SELECT T FROM PatientTransaction T WHERE T.transactionId = :id")
})
public class PatientTransaction implements Transaction {

	@Id
    @Column(name = "transaction_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PATIENT_TRANSACTION_SEQ") 
	@Basic(optional = false)
    private BigInteger transactionId;    
        		  
   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL) 
   @JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
    private Patient patient;
  //@Column(name = "resource_id", insertable = true, updatable = false)                        
  //private BigInteger resourceId;

    @Lob
    @Column(name = "meta", insertable = true, updatable = false)                        
    private String meta;

    @Size(max = 32)    
    @Column(name = "action", nullable = false, insertable = true, updatable = false)                                            
    private String action;

    @Lob
    @Column(name = "delta", insertable = true, updatable = false)                        
    private String delta;

    @Size(max = 128)    
    @Column(name = "actor", nullable = false, insertable = true, updatable = false)                                            
    private String actor;
 
	@Column(name = "transaction_timestamp")                        
    private Timestamp timestamp;

    public PatientTransaction() {    	
    }

    public void setTransactionId(BigInteger transactionId) {
    	this.transactionId = transactionId;
    }
    
    public BigInteger getTransactionId() {
    	return this.transactionId;
    }
    
    @Override        
    public <R extends DomainResource> void setResource(R patient) {
    	this.patient = (Patient)patient;
    }

    @Override
    public <R extends DomainResource> R getResource() {
    	return (R)this.patient;
    }
        
    public void setMeta(String meta) {
    	this.meta = meta;
    }
    
    public String getMeta() {
    	return this.meta;
    }
    
    public void setDelta(String delta) {
    	this.delta = delta;
    }
    
    public String getDelta() {
    	return this.delta;
    }
    
    public void setActor(String actor) {
    	this.actor = actor;
    }
    
    public String getActor() {
    	return this.actor;
    }
    
    public void setAction(String action) {
    	this.action = action;
    }
    
    public String getAction() {
    	return this.action;
    }

    public void setTimestamp(Timestamp timestamp) {
    	this.timestamp = timestamp;
    }
    
    public Timestamp getTimestamp() {
    	return this.timestamp;
    }
    
}
