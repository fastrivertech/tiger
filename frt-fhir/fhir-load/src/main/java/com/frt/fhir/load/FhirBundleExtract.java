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
package com.frt.fhir.load;

import java.util.Arrays;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class FhirBundleExtract {
	private JsonParser jsonParser;
	
	public void execute() 
		throws FhirLoadException {
		
		FhirContext context = FhirContext.forDstu3();
		jsonParser=(JsonParser)context.newJsonParser();
		
		String sourceDir = FhirLoadConfig.getInstance().get(FhirLoadConfig.FHIRLOAD_SOURCE_DIR);				
		File[] sourceFiles = new File(sourceDir).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		if (sourceFiles == null || sourceFiles.length < 1) {
			System.out.println(sourceDir + " no json files");
		}
		String targetDir = FhirLoadConfig.getInstance().get(FhirLoadConfig.FHIRLOAD_TARGET_DIR);
		
		File targetDirExits = new File(targetDir);
		if (!targetDirExits.exists()) {
			targetDirExits.mkdirs();
		}
		
		Arrays.stream(sourceFiles).forEach(sourceFile-> {			
			try (FileReader fr = new FileReader(sourceFile)){				
				Bundle bundle = this.jsonParser.parseResource(Bundle.class, fr);
				for (BundleEntryComponent entry : bundle.getEntry()) {
					if (entry.getResource().getResourceType() == ResourceType.Patient) {
						String jsonEncoded = jsonParser.encodeResourceToString((Patient)entry.getResource());
						String targetFilePath = targetDir + "/" + "patient_" + System.currentTimeMillis() + ".json"; 																				
						try (PrintWriter pr = new PrintWriter(targetFilePath)) {
							pr.println(jsonEncoded);
							pr.flush();
							System.out.println("succeeded to process patient '" + 
									   		   ((Patient)entry.getResource()).getId() + 
									   		   "' in " + targetFilePath);														
						} catch (IOException ex) {
							System.err.println("failed to process patient '" + 
											   ((Patient)entry.getResource()).getId() + 
											   "' in " + sourceFile + ": " + ex.getMessage());							
						}
					}
				}				
			} catch (IOException ex) {	
				System.err.println("failed to process " + sourceFile);
			}			
		});		
	}
	
	public static void main(String[] args) {
		try {
			new FhirBundleExtract().execute();
			System.out.println("FhirBundleExtract succeeded.");
			System.exit(0);
		} catch (FhirLoadException ex) {
			System.out.println("FhirBundleExtract failed: " + ex.getMessage());
			System.exit(1);
		}
	}
	
}
