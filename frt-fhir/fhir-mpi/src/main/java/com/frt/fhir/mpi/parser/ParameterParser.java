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
package com.frt.fhir.mpi.parser;

import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonException;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParsingException;
import org.hl7.fhir.r4.model.Identifier;
import com.frt.fhir.mpi.resource.Parameter;
import com.frt.fhir.mpi.resource.Parameters;

public class ParameterParser {

	public Parameters deserialize(String message) 
		throws ParameterParserException {		
		try (InputStream in = new ByteArrayInputStream(message.getBytes())) {
			Parameters parameters = null;
			JsonReader jsonReader = Json.createReader(in);
			JsonObject jsonObject = jsonReader.readObject();
			JsonValue resourceType = jsonObject.get("resourceType");
			if (resourceType != null &&
				"parameters".equals(((JsonString) resourceType).getString())) {
				 Iterator<String> names = jsonObject.keySet().iterator();
				 //ToDo
				 return parameters;
			} else {
				throw new ParameterParserException("invalid resource type: " + resourceType);
			}
		} catch(Exception ex) {
			throw new ParameterParserException(ex);
		}
	}
	
	public static Identifier decodeIdentifier(String id) 
		throws ParameterParserException {
		
			Identifier identifier = new Identifier();
			String[] splitted = id.split("|");		
			if ( splitted.length == 2 &&
				!splitted[0].isEmpty() &&
				!splitted[1].isEmpty()) {
				identifier.setSystem(splitted[1]).setValue(splitted[0]);
				return identifier;
			} else {	
				throw new ParameterParserException("invalid identifier '" + id + "'");
			}
	}
	
}
