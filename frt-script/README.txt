example of using fhir stream writer/reader.

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

