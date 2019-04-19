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
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;

public class FhirBundleExtract {
	private static Random random = new Random();
	private static String[] organizations = new String[]{"6a777679","cb2c590e",
													     "23cab562","8ad84ff8",
													     "06066802","194f938f",
													     "76c72b22","5ddfe33b",
													     "2f3cce27","0ef66802"}; 
	private static String[] practitioners = new String[]{"0f5f9b3e","254d3102",
														 "e294f205","5826716e",
														 "8c66c002","e3440d3f",
														 "f0117735","a4c72900",
														 "c4a72900","1b0656e5"};
	
	private JsonParser jsonParser;
	private String srcDir;
	private String destDir;
	
	public FhirBundleExtract() {
	}
	
	public FhirBundleExtract(String srcDir, String destDir) {
		this.srcDir=srcDir;
		this.destDir=destDir;
	}
	
	public void execute() 
		throws FhirLoadException {
		
		FhirContext context = FhirContext.forR4();
		jsonParser=(JsonParser)context.newJsonParser();
		
		String sourceDir = this.getSrcDir() != null ?
				this.getSrcDir():FhirLoadConfig.getInstance().get(FhirLoadConfig.FHIRLOAD_SOURCE_DIR);		
				
		File[] sourceFiles = new File(sourceDir).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		if (sourceFiles == null || sourceFiles.length < 1) {
			System.out.println(sourceDir + " no json files");
			return;
		}
		
		String targetDir = this.getDestDir() != null ?
				this.getDestDir():FhirLoadConfig.getInstance().get(FhirLoadConfig.FHIRLOAD_TARGET_DIR);
		
		File targetDirExits = new File(targetDir);
		if (!targetDirExits.exists()) {
			targetDirExits.mkdirs();
		}
		
		Arrays.stream(sourceFiles).forEach(sourceFile-> {			
			try (FileReader fr = new FileReader(sourceFile)){				
				Bundle bundle = this.jsonParser.parseResource(Bundle.class, fr);
				for (BundleEntryComponent entry : bundle.getEntry()) {
					if (entry.getResource().getResourceType() == ResourceType.Patient) {						
						Patient patient = (Patient)entry.getResource();
						enrich(patient);
						String jsonEncoded = jsonParser.encodeResourceToString(patient);
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
	
	public void enrich(Patient patient) {
		if (!patient.hasManagingOrganization()) {
			Reference theReference = new Reference();			
			int i = random.nextInt(10);			
			theReference.setId(organizations[i]);
			patient.setManagingOrganization(theReference);			
		}
		if (!patient.hasGeneralPractitioner()) {
			Reference theReference = new Reference();
			int i = random.nextInt(10);			
			theReference.setId(practitioners[i]);
			patient.addGeneralPractitioner(theReference);
		}		
	}
	
	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public static void main(String[] args) {
		// used in data feeding requires source and destination dirs from command line
		FhirBundleExtract extractor = null;
		try {
			if (args.length==2) {
				extractor = new FhirBundleExtract(args[0], args[1]);
			}
			else {
				extractor = new FhirBundleExtract();
			}
			extractor.execute();
			System.out.println("FhirBundleExtract succeeded.");
			System.exit(0);
		} catch (FhirLoadException ex) {
			System.out.println("FhirBundleExtract failed: " + ex.getMessage());
			System.exit(1);
		}
	}
	
}
