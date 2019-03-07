Open Source Synthea Synthetic Patient Generation
------------------------------------------------

Refer to https://synthetichealth.github.io/synthea
		 https://github.com/synthetichealth/synthea

1. Install Git
Download and install Git-2.21.0-64-bit.exe from  https://git-scm.com/download/win

2. Set Git Home
set GIT_HOME=C:\Bin\Git
set PATH=%GIT_HOME%\bin;%PATH%

3. Clone the source code
git clone https://github.com/synthetichealth/synthea.git
cd synthea

Note: you can skip this step if you donwload and unzip a snapshot

4. Build and test
gradlew build check test

5. Run to generate synthetic patients
run_synthea [-s seed] [-p populationSize] [state [city]]

For example:
run_synthea Massachusetts
run_synthea Alaska Juneau
run_synthea -s 12345
run_synthea -p 1000
run_synthea -s 987 Washington Seattle
run_synthea -s 21 -p 100 Utah "Salt Lake City"

Some settings can be changed in ./src/main/resources/synthea.properties.

SyntheaTM will output patient records in C-CDA and FHIR formats in ./output.

FRT FHIR Load
-------------
1. Build Tiger Project

2. Unzip \frt-package\target\frt-service-package-1.0.0-SNAPSHOT to any working folder
   load folder contains load tool
   load\data folder contains sample data
   
3. Execute env.bat to setup environment

4. Run load
   .\load\load.bat [data folder] [maximum number of data files to load]
   E.g.,    
   .\load\load.bat .\data 5
