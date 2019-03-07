Command line scripts at tiger project level:

(1) set up environment
env.bat - set build, test, execute environment - need to run before evrything else
usage:
cd <tiger-project-base-dir>
.\env.bat

(2) build tiger project:
usage:
cd <tiger-project-base-dir>
mvn clean install -Dmaven.test.skip=true

(3) run soapui projects - end to end tests simulate TouchStone Connectathoin20 test suites
usage:
cd <tiger-project-base-dir>
mvn test -pl frt-fhir\fhir-harness
note: need to get frt server up and running with frt-fhir-rest.war deployed

(4) run synthea generated synthetic patients loader - extract patient json from Synthea generated patient bundle and persist into database
usage:
cd <tiger-project-base-dir>
.\load_synthea.bat <path-to-patient-json-file-folder> [<max-record-to-be-load>]

sample output:
D:\FHIR_TIGER\tiger-refactor>.\load_synthea.bat "D:\SYNTHEA\synthea\output\fhir" 300
D:\FHIR_TIGER\tiger-refactor>REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA
D:\FHIR_TIGER\tiger-refactor>REM All Rights Reserved.
D:\FHIR_TIGER\tiger-refactor>REM Product Build Environment
D:\FHIR_TIGER\tiger-refactor>REM @echo off
D:\FHIR_TIGER\tiger-refactor>java -cp "./frt-fhir/fhir-rest/target/frt-fhir-rest/WEB-INF/classes;./frt-fhir/fhir-rest/ta
rget/frt-fhir-rest/WEB-INF/lib/*" com.frt.fhir.store.Loader "D:\SYNTHEA\synthea\output\fhir" 300
log4j:WARN No appenders could be found for logger (org.springframework.context.annotation.AnnotationConfigApplicationCon
text).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
[EL Info]: 2019-03-06 21:30:07.478--ServerSession(810672306)--EclipseLink, version: Eclipse Persistence Services - 2.7.4
.v20190115-ad5b7c6b2a
[EL Warning]: metadata: 2019-03-06 21:30:07.861--ServerSession(810672306)--Reverting the lazy setting on the OneToOne or
 ManyToOne attribute [patient] for the entity class [class com.frt.dr.transaction.model.PatientTransaction] since weavin
g was not enabled or did not occur.
[EL Info]: connection: 2019-03-06 21:30:08.048--ServerSession(810672306)--/file:/D:/FHIR_TIGER/tiger-refactor/frt-fhir/f
hir-rest/target/frt-fhir-rest/WEB-INF/classes/_FRT_DR_LOCAL_PERSISTENCE login successful
........................................................................................................................
........................................................................................................................
.............................................................bundle processed reached limit:300time elapsed: 75 sec., re
cords loaded: 301
below bundle has more than one patient entry>>>>>>>>>>>>>>>>
below bundle has 0 patient entry>>>>>>>>>>>>>>>>
b-id:null

