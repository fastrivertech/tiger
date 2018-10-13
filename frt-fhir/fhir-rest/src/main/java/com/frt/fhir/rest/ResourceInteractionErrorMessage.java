package com.frt.fhir.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceInteractionErrorMessage {

    private int httpStatusCode;
    private String httpMessage;
    private String errorCode;
    private String errorMessage;
    private String relatedLink;
    private String additionalInfoLink;
    
    public ResourceInteractionErrorMessage() {        
    }
    
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
    
    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
    }

    public String getHttpMessage() {
        return this.httpMessage;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public void setRelatedLink(String relatedLink) {
        this.relatedLink  = relatedLink;
    }
    
    public String getRelatedLink() {
        return this.relatedLink;
    }    

    public void setAdditionalInfoLink(String additionalInfoLink) {
        this.additionalInfoLink  = additionalInfoLink;
    }
    
    public String getAdditionalInfoLink() {
        return this.additionalInfoLink;
    }    
    	
}
