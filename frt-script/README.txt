Example of using fhir stream writer/reader
==========================================
On linux:
unzip frt-service-package-1.0.0-SNAPSHOT.zip into a folder e.g. ~/frt-services
the folder structure is as below:
/home/ec2-user/
setEnv.sh
/home/ec2-user/frt-services/
-----------------------------+ app
                             |
                             + bin --- datalake-ingestion.bat
                                       datalake-ingestion.sh
                                       datalake-read-write.bat
                                       datalake-read-write.sh
                                       fhir-stream-reader.bat
                                       fhir-stream-reader.sh
                                       fhir-stream-writer.bat
                                       fhir-stream-writer.sh
                                       ....
-----------------------------+ stream --- create-topic.bat
                                          create-topic.sh
                                          read-topic.bat
                                          read-topic.sh
                                          list-topic.bat
                                          list-topic.sh
                                          start.bat
                                          start-zookeeper.sh
                                          start-kafka.sh
                                          stop-zookeeper.sh
                                          stop-kafka.sh
                                          ... ... ...
-----------------------------+ data --- patient_01.json
                                        patient_02.json
										.... .... 
										patient_108.json
										
chmod 777 to all the *.sh if necessary.

AFTER started kafka (see README under ../stream folder for details):

* go to /home/ec2-user/frt-services/bin
* execute ./fhir-stream-writer.sh ../data where ../data is the folder where patient json files are stored, e.g. as shown 
in above folder structure.

after patient json files are published, they will be suffixed to prevent being published again.

Example of using fhir stream feeder
==========================================
Follow same process as using stream writer and reader for deploying services pack;
chmod 777 to all the *.sh if necessary.

AFTER started kafka (see README under ../stream folder for details):

* go to /home/ec2-user/frt-services/bin
* execute ./fhir-stream-feeder.sh ../data_feeder 1500 California 5000 Arizona 2800 Virginia 2000
where:
../data_feeder -- is the feeder intermediate files stagging area (it is created if not exists)
15000 -- time interval in milli-sec the pause between message send for one paralell task
California 5000 Arizona 2800 Virginia 2000 -- list of state name and sample size (population) for message generation
when this list is not present, the feeder will scan the base dir e.g. ../data_feeder_EXT_MESSAGES for patient json files to send

where ../data_feeder is the folder where patient json files are stagged synthea generated patient bundle jsons and fhir extractor produced
fhir patient json files, e.g. as shown in above folder structure.

example intermediate folder structures created by feeder:
../data_feeder
-----------+ fhir/California/ -patient_01.json <== synthea generated fhir patient bundle json files (patient address is in California)
                               patient_02.json
                               .... .... 
							   patient_108.json
                               ... ... ...
-----------+ fhir/Arizona/ --- patient_....json <== synthea generated fhir patient bundle json files (patient address is in Arizona)
                               patient_.....json
                               .... .... 
							   patient_....json
                               ... ... ...
-----------+ fhir/Virginia/ --- patient_....json <== synthea generated fhir patient bundle json files (patient address is in Virginia)
                               patient_.....json
                               .... .... 
                               ... ... ...
../data_feeder_EXT_MESSAGES
-----------+ California/ -patient_01.json <== extracted fhir patient json files (patient address is in California)
                               patient_02.json
                               .... .... 
							   patient_108.json
                               ... ... ...
-----------+ Arizona/ --- patient_....json <== extracted fhir patient json files (patient address is in Arizona)
                               patient_.....json
                               .... .... 
							   patient_....json
                               ... ... ...
-----------+ Virginia/ --- patient_....json <== extracted fhir patient json files (patient address is in Virginia)
                               patient_.....json
                               .... .... 
                               ... ... ...
Note, feeder will spin one task for each folder (for a state), tasks run in paralell sending messages to kafka topic (FhirTopic),
json suffix will be renamed to json~ after sent.


