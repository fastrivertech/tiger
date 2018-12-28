Apache Derby Database

Download Apache Derby 10.14.2.0 from https://db.apache.org/derby/
Install C:\apache\derby\db-derby-10.14.2.0

1. Embedded Mode
1.1 SQL command
	env.bat
	derby-ij.bat
1.2 Enable Derby embedded mode in persistence.xml
	<property name="javax.persistence.jdbc.driver" value="org.apache.derby.jdbc.EmbeddedDriver"/>
    <property name="javax.persistence.jdbc.url" value="jdbc:derby:C:\apache\derby\tiger_db"/>
	
2. Network Mode
2.1 Commands
	env.bat
	start-derby.bat
	shutdown-derby.bat
2.2 URL & USER
URL: jdbc:derby://localhost:1527/tiger_db;create=true;
USERID/PASSWORD: APP/null

2.3 SQL command
	derby-client.bat
ij> run 'create_patient_tables.sql';

3. Persistence Configuration
Enable Derby network mode in persistence.xml
	<property name="javax.persistence.jdbc.driver" value="org.apache.derby.jdbc.ClientDriver"/>
	<property name="javax.persistence.jdbc.url" value="jdbc:derby://localhost:1527/tiger_db"/>	
	<property name="javax.persistence.jdbc.user" value="APP"/>
	<property name="javax.persistence.jdbc.password" value="null"/>










