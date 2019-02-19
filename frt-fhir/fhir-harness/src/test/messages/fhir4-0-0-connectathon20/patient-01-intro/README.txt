
FHIR4-0-0-Connectathon20-Patient-01-Intro-Client
------------------------------------------------
1) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${createdResourceId}  
2) Search patient
   GET /frt-fhir-rest/1.0/Patient?given=Peter${C8}=Chalmers${C8}
   
FHIR4-0-0-Connectathon20/Patient-01-Intro/Base Tests/patient-intro-c20-base-client-id-json
   
Step1 - RegisterNewPatient 
	PUT	/frt-fhir-rest/1.0/Patient/${createdResourceId}
	patient-read-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}
	response: patient-create-PeterChalmers-min.json
	
Step2 - UpdatePatient
    PUT /frt-fhir-rest/1.0/Patient/${createdResourceId}  	 
    patient-update-PeterChalmers.json
   
    GET /frt-fhir-rest/1.0/Patient/${createdResourceId}
    response: patient-update-PeterChalmers-min.json
   
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}/_history
	
Step4 - PatientSearch
	GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}	
	response: patient-update-PeterChalmers-min.json

Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${createdResourceId}
	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}
	
	
FHIR4-0-0-Connectathon20-Patient-01-Intro-Server
-----------------------------------------------
1) Delete patient (not supported)
   DELETE /frt-fhir-rest/1.0/Patient?given=${searchGivenName}&amp;family=${searchFamilyName} 
2) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${createdResourceId}    
	
Step1 - RegisterNewPatient
	POST /frt-fhir-rest/1.0/Patient
	patient-create-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/?identifier=${searchIdentifier}	
	response: patient-create-PeterChalmers-min.json
	
Step2 - UpdatePatient
	PUT	/frt-fhir-rest/1.0/Patient/${createdResourceId}
	patient-update-PeterChalmers.json

	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}
	
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}/_history
	
Step4 - PatientSearch	
    GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}
	response: patient-update-PeterChalmers-min.json
	
Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${createdResourceId}
	GET /frt-fhir-rest/1.0/Patient/${createdResourceId}
		