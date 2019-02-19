
FHIR4-0-0-Connectathon20-Patient-01-Intro-Client
------------------------------------------------
1) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}  
2) Search patient
   GET /frt-fhir-rest/1.0/Patient?given=Peter${C8}=Chalmers${C8}
   
FHIR4-0-0-Connectathon20/Patient-01-Intro/Base Tests/patient-intro-c20-base-client-id-json
   
Step1 - RegisterNewPatient 
	PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-read-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	response: patient-create-PeterChalmers-min.json
	
Step2 - UpdatePatient
    PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}  	 
    patient-update-PeterChalmers.json
   
    GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
    response: patient-update-PeterChalmers-min.json
   
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}/_history
	
Step4 - PatientSearch
	GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}	
	response: patient-update-PeterChalmers-min.json

Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	
	
FHIR4-0-0-Connectathon20-Patient-01-Intro-Server
-----------------------------------------------
1) Delete patient (not supported)
   DELETE /frt-fhir-rest/1.0/Patient?given=${searchGivenName}&amp;family=${searchFamilyName} 
2) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}    
	
Step1 - RegisterNewPatient
	POST /frt-fhir-rest/1.0/Patient
	patient-create-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/?identifier=${searchIdentifier}	
	response: patient-create-PeterChalmers-min.json
	
Step2 - UpdatePatient
	PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-PeterChalmers.json

	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}/_history
	
Step4 - PatientSearch	
    GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}
	response: patient-update-PeterChalmers-min.json
	
Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
		