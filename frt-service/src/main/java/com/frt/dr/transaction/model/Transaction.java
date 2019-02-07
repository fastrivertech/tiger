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

import java.io.Serializable;
import javax.persistence.MappedSuperclass;
import com.frt.dr.model.DomainResource;

/**
 * Transaction class
 * @author cqye
 */
@MappedSuperclass
public interface Transaction extends Serializable{	
	static final long serialVersionUID = -8321293485415819089L;	
	
	public enum ActionCode {
		C("Create"),
		R("Read/view/print"),
		U("Update"),
		D("Delete"),
		E("Excute");		
		
		private String code;
		
		ActionCode(String code){
			this.code = code;
		}
		
		public String getActionCode() {
			return this.code;
		}
	}
	
	<R extends DomainResource> void setResource(R resource);
	
	<R extends DomainResource> R getResource();
	
}
