
FHIR4-0-0-Connectathon20-Patient-01-Intro-Bonus-Client
------------------------------------------------------
1) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}  
2) Search patient
   GET /frt-fhir-rest/1.0/Patient?given=Peter${C8}=Chalmers${C8}
   
/FHIR4-0-0-Connectathon20/Patient-01-Intro/Bonus Tests/patient-intro-c20-bonus-client-id-json
   
Step1 - RegisterNewPatient 
	PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-create-bonus-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	response: patient-create-bonus-PeterChalmers-min.json
	
Step2a - UpdatePatient
    PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}  	 
    patient-update-bonus1-PeterChalmers.json
   
    GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
    response: patient-update-bonus1-PeterChalmers-min.json
   
Step2b - UpdatePatient
    PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}  	 
    patient-update-bonus2-PeterChalmers.json
   
    GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
    response: patient-update-bonus2-PeterChalmers-min.json
   
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}/_history
	
Step4 - PatientSearch
	GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}	
	response: patient-update-bonus2-PeterChalmers-min.json

Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	
	
FHIR4-0-0-Connectathon20-Patient-01-Intro-Bonus-Server
------------------------------------------------------
1) Delete patient (not supported)
   DELETE /frt-fhir-rest/1.0/Patient?given=${searchGivenName}&amp;family=${searchFamilyName} 
2) Delete patient
   DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}    
	
Step1 - RegisterNewPatient
	POST /frt-fhir-rest/1.0/Patient
	patient-create-bonus-PeterChalmers.json
	
	GET /frt-fhir-rest/1.0/Patient/?identifier=${searchIdentifier}	
	response: patient-create-bonus-PeterChalmers-min.json
	
Step2a - UpdatePatient
	PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-bonus1-PeterChalmers.json

	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-bonus1-PeterChalmers-min.json
	
Step2b - UpdatePatient
	PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-bonus2-PeterChalmers.json

	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-bonus2-PeterChalmers-min.json
	
Step3 - PatientHistory
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}/_history
	
Step4 - PatientSearch	
    GET /frt-fhir-rest/1.0/Patient?given=${searchGivenName}=${searchFamilyName}
	response: patient-update-bonus2-PeterChalmers-min.json
	
Step5 - PatientDelete
	DELETE /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
		