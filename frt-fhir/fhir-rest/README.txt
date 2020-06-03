Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
-------------------------------------------------------------------

Installation and Deployment Guide
---------------------------------

Prerequisites:
--------------
1) Build and unzip frt-service-package
   frt-service-package-1.0.0-SNAPSHOT.zip 

Install and Deploy:
-------------------
1) Install JDK 11.0.2
2) Install Tomcat
   2.1) Download apache-tomcat-9.0.16.tar.gz
        from https://tomcat.appache.org 
   2.2)	Unzip 
   2.3) Set environment variables
        frt-service-package\env.bat/env.sh    
   2.4) Set roles and users
	    CATALINA_HOME\conf\tomcat-users.xml
   2.5) Enable remote access to Tomcat Manager
        Uncomment out 'Valve' and 'Manager' 
        CATALINA_HOME\webapps\manager\META-INF\context.xml
   2.6) Start/top tomcat
        CATALINA_HOME\bin\catlina start/stop
   2.7) References
		https://github.com/fastrivertech/tiger/wiki/Build-and-Deploy-Apps
		CATALINA_HOME\RUNNING.txt
		
3) Install Splice Machine or Derby Database
   3.1) Download and unzip Splice Machine standalone 
   3.3) References
        https://github.com/fastrivertech/tiger/wiki/Install-Splice-Machine
   
4) Create and Deploy FRT FHIR database schema
   4.1) For Splice Machine, 
        refer to frt-service-package\schema\sp\README.txt
   4.2) For Derby, 
		refere to frt-service-package\schema\derby\README.txt	  	  

5) Deploy FRT FHIR Applications
   5.1) Drop frt-service-package\app\frt-fhir-doc.war 
			 to CATALINA_HOME\webapps
   2.2) Drop frt-service-package\app\frt-fhir-rest.war 
			 to CATALINA_HOME\webapps
   
6) Smoke Test
   6.1) Fhir API docs
   http://[host]:8088/frt-fhir-doc/apidocs
   host = ec2-54-202-187-87.us-west-2.compute.amazonaws.com
          or localhost     
   6.2) Fhir Patient API
   http://:8088/frt-fhir-rest/1.0/Patient
   host = ec2-54-202-187-87.us-west-2.compute.amazonaws.com
          or localhost   
