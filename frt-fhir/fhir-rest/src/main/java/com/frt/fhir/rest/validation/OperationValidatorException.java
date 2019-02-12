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
package com.frt.fhir.rest.validation;

/**
 * ValidationException class
 * 
 * @author cqye
 */
public class OperationValidatorException extends Exception {

    public enum ErrorCode {
        
    	INVALID_ID("invalid id"),
        INVALID_MIME_TYPE("invalid mime type"),
        INVALID_SUMMARY_TYPE("invalid summary type"),
        INVALID_QUERY_PARAMS("invalid query parameter(s)"),
        UNKNOWN("unknown");        
    	
        private String value;
        
        private ErrorCode(String value) {
            this.value = value;
        }
        
    }   
    
    private ErrorCode errorCode = ErrorCode.UNKNOWN;
    
    public OperationValidatorException() {
        super();
    }

    public OperationValidatorException(String m) {
        super(m);
    }

    public OperationValidatorException(Throwable t) {
        super(t);
    }
    
    public OperationValidatorException(String m, Throwable t) {
        super(m, t);
    }

    public OperationValidatorException(String m, ErrorCode e) {
        super(m);
        this.errorCode = e;
    }
    
    public OperationValidatorException(Throwable t, ErrorCode e) {
        super(t);
        this.errorCode = e;
    }
    
    public OperationValidatorException(String m, Throwable t, ErrorCode e) {
        super(m, t);
        this.errorCode = e;
    }
    
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}