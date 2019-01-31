Apache Derby Database

Download Apache Derby 10.14.2.0 from https://db.apache.org/derby/
Install to C:\apache\derby\db-derby-10.14.2.0

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

2.3 change app to system user
	run derby-ij.bat
	ij>CALL SYSCS_UTIL.SYSCS_CREATE_USER('app','derby');
	ij>SYSCS_UTIL.SYSCS_RESET_PASSWORD('app','derby');	
	ij>disconnect;
	ij>exit;

2.4 add credential to derby-client.properties
	ij.user=app
	ij.password=derby

2.5 create schema
	derby-client.bat
	ij> run 'create_schema.sql';

2.6 drop schema
	derby-client.bat
	ij> run 'drop_schema.sql';

2.7 create patient tables
	derby-client.bat
	ij> connect 'jdbc:derby://localhost:1527/tiger_db;create=true' user 'frt' password 'frt';
	ij> run 'create_patient_tables.sql';

2.8 drop patient tables
	derby-client.bat
	ij> connect 'jdbc:derby://localhost:1527/tiger_db;create=true' user 'frt' password 'frt';	
	ij> run 'drop_patient_tables.sql';
	
3. Persistence Configuration
	Enable Derby network mode in persistence.xml
	<property name="javax.persistence.jdbc.driver" value="org.apache.derby.jdbc.ClientDriver"/>
	<property name="javax.persistence.jdbc.url" value="jdbc:derby://localhost:1527/tiger_db"/>	
	<property name="javax.persistence.jdbc.user" value="APP"/>
	<property name="javax.persistence.jdbc.password" value="null"/>










