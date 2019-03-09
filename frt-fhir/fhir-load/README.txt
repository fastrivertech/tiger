Open Source Synthea Synthetic Patient Generation
------------------------------------------------

Refer to https://synthetichealth.github.io/synthea
		 https://github.com/synthetichealth/synthea

1. Execute env.bat to setup environment
2. Goto Synthea Directory 
   cd ./synthea/bin
3. Generate synthetic patients
   ./synthea.bat  -p <generatedPatientPopulationSize> <State> [<City>]

synthea command line usage:

Usage: run_synthea [options] [state [city]]
Options: [-s seed] [-p populationSize]
         [-g gender] [-a minAge-maxAge]");
         [-o overflowPopulation]");
         [--config* value]");
          * any setting from src/main/resources/synthea.properties");
Examples:");
run_synthea Massachusetts
run_synthea Alaska Juneau
run_synthea -s 12345
run_synthea -p 1000
run_synthea -s 987 Washington Seattle
run_synthea -s 21 -p 100 Utah \"Salt Lake City\"
run_synthea -g M -a 60-65
run_synthea -p 10 --exporter.fhir.export true
run_synthea --exporter.baseDirectory \"./output_tx/\" Texas

e.g. 

./synthea.bat  -p 100 Utah "Salt Lake City"

this will generate 100 patient fhir resource in the form of fhir bundle in json format,

Sample stdout:

Loaded 4 modules.
Running with options:
Population: 100
Seed: 1551993661210
Location: Salt Lake City, Utah
Min Age: 0
Max Age: 140
4 -- Noel608 Reynolds644 (14 y/o F) Salt Lake City, Utah
6 -- Florencia449 Yost751 (24 y/o F) Salt Lake City, Utah
3 -- Lesley194 Roob72 (64 y/o M) Salt Lake City, Utah
2 -- Lucia634 Carranza218 (15 y/o F) Salt Lake City, Utah
8 -- Rhett759 Rogahn59 (16 y/o M) Salt Lake City, Utah
7 -- Sheldon401 Wolff180 (40 y/o M) Salt Lake City, Utah
5 -- Jessie665 Balistreri607 (67 y/o F) Salt Lake City, Utah
1 -- Sabrina514 Breitenberg711 (9 y/o F) Salt Lake City, Utah
9 -- Jaleesa813 Zemlak964 (36 y/o F) Salt Lake City, Utah
10 -- Cordie578 Wolf938 (65 y/o F) Salt Lake City, Utah
11 -- Louetta798 Sporer811 (48 y/o F) Salt Lake City, Utah
14 -- Werner409 Streich926 (9 y/o M) Salt Lake City, Utah
15 -- Amber507 Ernser583 (31 y/o F) Salt Lake City, Utah
....
91 -- Roxanne257 Considine820 (55 y/o F) Salt Lake City, Utah
90 -- Carmen818 Leyva523 (27 y/o F) Salt Lake City, Utah
89 -- Valentine262 Weimann465 (6 y/o F) Salt Lake City, Utah
{alive=100, dead=2}

FRT FHIR Load
-------------
1. Build Tiger Project

2. Unzip \frt-package\target\frt-service-package-1.0.0-SNAPSHOT to any working folder
   load folder contains load tool
   load\data folder contains sample data
   
3. Execute env.bat to setup environment

4. Run load - there are 2 loaders, one that loads patients directly into persistence storage (e.g. Splice Machine - db loader),
   another that loads patients to persistence store through FHIR Patient RESTful service API (web loader)

   db loader usage:

   .\load\load.bat [data folder] [maximum number of data files to load]
   E.g.,    
   .\load\load.bat .\data 5
   E.g.,
   .\load\load.bat .\synthea\bin\output 500

   web loader usage:

   .\load\webload.bat [fhir patient rest service URL] [data folder] [maximum number of data files to load]
   E.g.,    
   .\load\webload.bat "http://localhost:8080/frt-fhir-rest/1.0/Patient" ".\data"
   E.g.,
   .\load\webload.bat "http://localhost:8080/frt-fhir-rest/1.0/Patient" ".\synthea\bin\output" 500
   
