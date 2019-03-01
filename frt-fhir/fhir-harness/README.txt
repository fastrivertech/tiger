To run the SOAPUI projects from maven command line:
cd <fhir-tiger-project-base-dir>
.\env.bat
mvn -Dgroovy.json.faststringutils.disable=true test -pl frt-fhir\fhir-harness

### -Dgroovy.json.faststringutils.disable=true is needed when JDK is > 8
### to avoid error at test runtime: java.lang.ClassCastException: [B cannot be cast to [C....