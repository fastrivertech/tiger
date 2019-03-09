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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import com.frt.fhir.service.FhirService;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;

public class FhirLoad {
	private File sourceDir; // where patient json files located
	private Integer limit; // load number of files from source location
	private FhirService fhirService; // data base persist store
	private JsonParser jsonParser; // HAPI json parser
	
	public FhirLoad(File sourceDir) {
		this(sourceDir, -1);
	}

	public FhirLoad(File sourceDir, int limit) {
		this.sourceDir = sourceDir;
		this.limit = limit;
		this.fhirService = new FhirService();
		FhirContext context = FhirContext.forDstu3();
		this.jsonParser=(ca.uhn.fhir.parser.JsonParser)context.newJsonParser();
		
	}

	public static void main(String[] args) {
		// command line args: sourceDir, number of json files to be loaded
		File sourceDir = null;
		int limit = -1;
		if (args.length<=2&&args.length>=1) {
			// source dir
			sourceDir = new File(args[0]);
			if (args.length==2) {
				limit = Integer.parseInt(args[1]);
			}
		} else {
			printUsage();
			System.exit(0);
		}
		if (!(sourceDir.exists() && sourceDir.isDirectory())) {
			System.err.println("<patient-json-source-dir> given does not exist or is not a directory, path=[" + sourceDir.getPath() + "]");
			printUsage();
			System.exit(-1);
		}
		FhirLoad loader = new FhirLoad(sourceDir, limit);
		loader.load();
	}

	private static void printUsage() {
		System.out.println("FHIR Patient DBLoader Usage:");
		System.out.println(
				"java -cp <classpath> com.frt.fhir.store.Loader <patient-json-source-dir> [<load-patient-json-limit>]");
		System.out.println(
				"Parameter <patient-json-source-dir> (required): folder where synthea generated patient json files are located");
		System.out.println(
				"Parameter <load-patient-json-limit> (optional): max number of files to be loaded, default: load all files");
	}
	
	public void load() {
		File[] jsonFiles = sourceDir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		FileReader fr=null;
		int count = 0;

		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("META-INF/persistence.xml").getFile());
		System.out.println("JPA persistence.xml:::" + file.getAbsolutePath());
		Map<String, List<Patient>> bundle2Pt = new HashMap<String, List<Patient>>();
		List<Bundle> bundle0Pt = new ArrayList<Bundle>();
		long start = System.currentTimeMillis();
		for (File f: jsonFiles) {
			if (count>limit&&limit>0) {
				System.out.print("bundle processed reached limit:" + limit);
				break;
			}
			try {
				fr = new FileReader(f);
				org.hl7.fhir.dstu3.model.Bundle bundle = this.jsonParser.doParseResource(org.hl7.fhir.dstu3.model.Bundle.class, fr);
				List<Patient> pts = new ArrayList<Patient>();
				for (org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent entry: bundle.getEntry()) {
					if (entry.getResource().getResourceType()==ResourceType.Patient) {
						pts.add((Patient)entry.getResource());
						this.fhirService.create("Patient", (DomainResource)entry.getResource());
						count++;
						System.out.print(".");
					}
				}
				if (pts.size()>1) {
					// report bundle with >1 patient
					bundle2Pt.put(bundle.getId(), pts);
				}
				if (pts.size()==0) {
					// report bundle with no patient
					bundle0Pt.add(bundle);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long elapsed = System.currentTimeMillis() - start;
		System.out.println("time elapsed: " + elapsed/1000 + " sec., records loaded: " + count);
		System.out.println("below bundle has more than one patient entry>>>>>>>>>>>>>>>>");
		bundle2Pt.forEach((k, v) ->	{
			System.out.println("bundle:"+k);
			v.forEach((p) -> System.out.println("patient: id="+p.getId()+", name="+p.getName()));
			}
		);
			
		System.out.println("below bundle has 0 patient entry>>>>>>>>>>>>>>>>");
		bundle0Pt.forEach(b-> System.out.println("b-id:"+b.getId()));
	}
}
