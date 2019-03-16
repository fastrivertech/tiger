
step to build rest api docs

1) Enable swagger in frt-fhir/fhir-rest/pom.xml
2) build
3) deploy frt-fhir/fhir-api/target/frt-fhir-api.war
4) http://<server>:<port>/<context-root>
   e.g. http://localhost:8080/frt-fhir-api
5) Authorization HTTP BASIC, user name, password
   
   If frt-fhir-rest.war has authorization enabled (see fhir-rest/README.txt for details),
   then some kind of security challenge has to be answered by the user, e.g. a user name / password 
   dialog box will popup if the security scheme is HTTP BASIC;
   
   The challenge can occur when first time execute an operation is attempted, or when
   user clicks the button "Authorize" on the page;
   
   After first time the challenge is answered, it is memorized (cookie is saved), no challenge for sub-sequent operations.
   
   To test the authorization in action, make sure to clean the browser's cookies (browser specific, check browser doc for details)
   
   
 
