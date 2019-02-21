
1) Refer to FHIR Validation
https://www.hl7.org/fhir/validation.html

2) Official FHIR Validation Tool
FHIR Validator
http://wiki.hl7.org/index.php?title=Using_the_FHIR_Validator

Download the latest official FHIR validator toool 
org.hl7.fhir.validation.cli-3.8.0-20190211.175101-5.jar
https://oss.sonatype.org/content/repositories/snapshots/ca/uhn/hapi/fhir/org.hl7.fhir.validation.cli/3.8.0-SNAPSHOT/

3) Execute validation
java -jar org.hl7.fhir.validator.jar resource.json -version 4.0 -defn hl7.fhir.core#4.0.0

or use default option
java -jar org.hl7.fhir.validator.jar resource.json





