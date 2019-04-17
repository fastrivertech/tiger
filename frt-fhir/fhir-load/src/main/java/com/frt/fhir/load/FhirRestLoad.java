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
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import com.frt.fhir.service.FhirService;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.JsonParser;

public class FhirRestLoad {
	private static final String REST_URL = "http://localhost:8080/frt-fhir-rest/1.0/Patient";
	private File sourceDir; // where patient json files located
	private Integer limit; // load number of files from source location
	private JsonParser jsonParser; // HAPI json parser
	private Client client; // rest client
	private String target; // rest target url
	public FhirRestLoad(String target, File sourceDir) {
		this(target, sourceDir, -1);
	}

	public FhirRestLoad(String target, File sourceDir, int limit) {
		this.sourceDir = sourceDir;
		this.limit = limit;
		FhirContext context = FhirContext.forR4();
		this.jsonParser=(ca.uhn.fhir.parser.JsonParser)context.newJsonParser();
		this.target = target;
	}

	public static void main(String[] args) {
		// command line args: sourceDir, number of json files to be loaded
		File sourceDir = null;
		String restURL = null;
		int limit = -1;
		if (args.length<=3 && args.length>=2) {
			restURL = args[0];
			// source dir
			sourceDir = new File(args[1]);
			if (args.length==3) {
				limit = Integer.parseInt(args[2]);
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
		FhirRestLoad loader = new FhirRestLoad(restURL, sourceDir, limit);
		loader.load();
	}

	private static void printUsage() {
		System.out.println("FHIR Patient WebLoader Usage:");
		System.out.println(
				"java -cp <classpath> com.frt.fhir.load.Loader <fhir-patient-rest-url> <http://localhost:8080/frt-fhir-rest/1.0/Patient> [<load-patient-json-limit>]");
		System.out.println(
				"Parameter <fhir-patient-rest-url> (required): Restful Service API endpoint, e.g. http://localhost:8080/frt-fhir-rest/1.0/Patient");
		System.out.println(
				"Parameter <patient-json-source-dir> (required): folder where synthea generated patient json files are located");
		System.out.println(
				"Parameter <load-patient-json-limit> (optional): max number of files to be loaded, default: load all files");
	}
	
	public void load() {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.target);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

		File[] jsonFiles = sourceDir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		
		FileReader fr=null;
		
		int count = 0;
		
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
				org.hl7.fhir.r4.model.Bundle bundle = this.jsonParser.doParseResource(org.hl7.fhir.r4.model.Bundle.class, fr);
				List<Patient> pts = new ArrayList<Patient>();
				for (org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry: bundle.getEntry()) {
					if (entry.getResource().getResourceType()==ResourceType.Patient) {
						pts.add((Patient)entry.getResource());
						Response response = invocationBuilder.post(Entity.json(this.jsonParser.encodeResourceToString((Patient)entry.getResource())));
						if (response != null && (response.getStatus() == 200 || response.getStatus() == 201)) {
							// OK
						} else {
							System.out.println("Patient create error, return code: " + response.getStatus());
						}
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
