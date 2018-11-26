package com.frt.fhir.springboot.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder {

	public static ResponseEntity<String> build(String body, HttpStatus status, String tag, MediaType type) {
		ResponseEntity<String> re = new ResponseEntity<String>(body, status);
		
		return re.status(status).lastModified(System.currentTimeMillis()).eTag("W/" + tag).build();
	}

}
