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
package com.frt.fhir.mpi;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;

/**
 * FHIR Mpi Service
 * @author cqye
 */
public interface MpiService<T> {

   /**
	 * Match domain resource with golden domain resources in the MPI database based on the MPI provider 
	 * @param parameters match parameters containing domain resources and match options
	 * resource: patient which includes match fields
	 * onlyCertainMatch: if the flag sets true, only matched record with highest score return
	 * 				 	 if the flag sets false, all matches records with match score return 
	 * 					 onlyCertainMatch is similar with oneExactMacth  
	 * count: maximum number of records that is above match thresholds 
	 * @return Bundle contains a set of matched golden domain resources or potentially matched records 
	 * resource: a list of golden patients matching above threshold 
	 * search:	match-grade extension with mode and score, mode specifies 'match' or 'potential match'
	 * operation outcome: operation result code
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Bundle match(Parameters parameters) 
		throws MpiServiceException;

	/**
	 * Probabilistic search golden domain resources based on search criteria and options
	 * @param parameters search parameters containing domain resource and search options
	 * resource: patient which include search fields (can be extended to support range search)
	 * option: vendor-specific such as different search types: BLOCKER-SEARCH/ALPHA-SEARCH/PHONETIC-SEARCH  
	 * threshold: match threshold   			
	 * @return Bundle contains a set of matched golden domain resources with score above the match threshold
	 * resource:  a list of patients matching above threshold
	 * search:	match-grade extension with mode and score, mode specifies 'match'
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Bundle search(Parameters parameters)
		throws MpiServiceException;
	
	Optional<T> update(T object, List<Parameter> options) 
		throws MpiServiceException;
	
	/**
	 * Merge a source domain resource with the sourceId to a target domain resource with the targetId.
	 * FHIR patient merge operation maps to HL7 ADT A40, identifier maps to PID-3 (Patient Identifier List) 
	 * FHIR Identifier	=> HL7 PID-3 CX
	 * 		use
	 * 		type	    =>	   Identifier Type code
	 * 		system 	    =>	   Assigning Authority 
	 * 		value	    =>	   id
	 * 		period
	 *  	assigner    =>	   Assigning Authority
	 * FHIR Merge operation is global merge or enterprise merge in MPI perspective, 
	 * plus local merge or domain resource merge. After operation, the subsumed domain resource is not visible. 
	 * @param sourceId the surviving unique identifier of the source domain resource
	 * @param targetId the subsumed unique identifier of the target domain resource
	 * @param options list of vendor specific options such as CalculateOnly = true / false
	 * @return Bundle a set of merged domain resource and survived domain resource
	 * operation outcome: operation result code
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Optional<T> merge(org.hl7.fhir.r4.model.Parameters parameters) 
		throws MpiServiceException;
	
	/**
	 * Un-merge a survived domain resource
	 * @param resourceId the global unique identifier of the survived domain resource
	 * @param options list of options vendor specific options such as CalculateOnly = true / false
	 * @return Bundle a set of source domain resource and target domain resource
	 * operation outcome: operation result code
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Bundle unmerge(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException;
	
	/**
	 * Link two domain resources,  FHIR link operation is global merge or enterprise merge 
	 * in MPI perspective, but does not perform local merge or domain resource merge.
	 * @param sourceId the unique identifier of the source domain resource from the domain
	 * @param targetId the unique identifier of the target domain resource from the domain
	 * @param list of options vendor specific options such as CalculateOnly = true / false
	 * @return Bundle a set of linked domain resources
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Bundle link(Identifier sourceId, Identifier targetId, List<Parameter> options) 
		throws MpiServiceException;
	
	/**
	 * Un-merge a survived domain resource 
	 * @param resourceId the unique identifier of the survived domain resource from the domain
	 * @param options list of options vendor specific options such as CalculateOnly = true / false
	 * @return Bundle a set of merged domain resource and survived domain resource
	 * @throws MpiServiceException throws exception if any error occurs during process
	 */
	Bundle unlink(Identifier resourceId, List<Parameter> options) 
		throws MpiServiceException;
	
}
