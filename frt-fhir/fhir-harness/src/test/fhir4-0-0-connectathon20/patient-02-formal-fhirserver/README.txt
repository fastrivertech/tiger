
FHIR4-0-0-Connectathon20-Patient-02-Formal-Server-Client-Id
-----------------------------------------------------------
01-RegisterPatient
	1) Delete patient
	   DLETE /frt-fhir-rest/1.0/Patient?given=${patientGivenName}&family=${patientFamilyName}
	2) Delete patient
       DELETE /frt-fhir-rest/1.0/Patient?identifier=${patientIdentifier} 

	01-UpdateCreate Patient
    PUT	/frt-fhir-rest/1.0/Patient/${#Project#CD16}
    patient-create-client-id.json

02-PatientUpdate
    1) Delete patient
       DLETE /frt-fhir-rest/1.0/Patient?given=${patientGivenName}&family=${patientFamilyName}
    2) Delete patient
       DELETE /frt-fhir-rest/1.0/Patient?identifier=${patientIdentifier}
    3) UpdateCreate Patient
       PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}
       patient-create-client-id.json
   
	01-UpdatePatient  
    PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	patient-update-client-id.json
  
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	
03-PatientRead
    1) Delete patient
       DLETE /frt-fhir-rest/1.0/Patient?given=${patientGivenName}&family=${patientFamilyName}
    2) Delete patient
       DELETE /frt-fhir-rest/1.0/Patient?identifier=${patientIdentifier}
    3) UpdateCreate Patient
       PUT /frt-fhir-rest/1.0/Patient/${#Project#CD16}
       patient-create-client-id.json

	01-ReadPatient
	GET /frt-fhir-rest/1.0/Patient/${#Project#CD16}
	
04-PatientHistory


05-PatientVRead

06-PatientSearching

07-PatientDeletion

98-PatientNoVersion

99-PatientAll
		
FHIR4-0-0-Connectathon20-Patient-02-Formal-FhirServer-Server-Id
------------------------------------------------------------
01-RegisterPatient

02-PatientUpdate

03-PatientRead

04-PatientHistory

05-PatientVRead

06-PatientSearching

07-PatientDeletion

98-PatientNoVersion

99-PatientAll

		