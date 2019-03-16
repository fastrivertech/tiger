
Enabling HTTP BASIC authentication & authorization for frt-fhir-rest.war:

Added following to web.xml to enable authorization of all API operations:

<!-- enable basic authentication for frt-fhir-rest -->
	<security-constraint>
    	<web-resource-collection>
      		<web-resource-name>*</web-resource-name>
      		<url-pattern>/API/*</url-pattern>
		</web-resource-collection>
    	<auth-constraint>
		<role-name>apiuser</role-name>
    	</auth-constraint>
	</security-constraint>
   	<security-role>
    	<description>The role that is required to access FRT FHIR Services</description>
    	<role-name>apiuser</role-name>
  	</security-role>
   	<login-config>
    	<auth-method>BASIC</auth-method>
    	<realm-name>ProtectedResources</realm-name>
  	</login-config>

This is tomcat specific (AFAIK):

since a role "apiuser" is introduced and referenced in above security configuration,
the target container (Tomcat) has to add below to its user DB - tomcat-users.xml:
    <!-- added for basic auth of frt-fhir-rest.war -->
	<user username="guest" password="fast123" roles="apiuser"/>
    <role rolename="apiuser"/>
	...
	<role rolename="manager-gui"/>
	<role rolename="tomcat"/>
	<role rolename="role1"/>
	<user username="tomcat" password="s3cret" roles="manager-gui"/>
	<user username="both" password="password" roles="tomcat,manager-gui"/>
	<user username="role1" password="password" roles="role1"/>
  	  	
 Authorization HTTP BASIC:
 user name: guest
 password: fast123
   
Note, enable authorization will impact clients (examples see below) that access the FHIR services:

1) end user using client tools
2) auto tests
3) SOAPUI testSuites
4) Swagger UI API Doc apps

The behavior is briefly described below:
   
   If frt-fhir-rest.war has authorization enabled (see fhir-rest/README.txt for details),
   then some kind of security challenge has to be answered by the user, e.g. a user name / password 
   dialog box will popup if the security scheme is HTTP BASIC;
   
   The challenge can occur when first time execute an operation is attempted, or when
   user clicks the button "Authorize" on the page;
   
   After first time the challenge is answered, it is memorized (cookie is saved), no challenge for sub-sequent operations.
   
   To test the authorization in action, make sure to clean the browser's cookies (browser specific, check browser doc for details)
   for each new session, or go incognito mode, e.g. for Chrome CTRL+SHFT+N
   
To Disable HTTP BASIC Auth: 

Comment out relevant section from web.xml of frt-fhir-rest.war
   
 
