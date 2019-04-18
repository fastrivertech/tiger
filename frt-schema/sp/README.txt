Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
-------------------------------------------------------------------

Splice Machine Installation and Deployment Guide
------------------------------------------------

Splice Machine User Reference
https://doc.splicemachine.com/index.html

Default Standalone Splice Machine 2.7 Users/Passwords:
splice/admin
YourUserId/YourPassword

Splice Machine Console
http://localhost:4040

Installation
------------
1) Splice Machine Standalone 
   https://github.com/splicemachine/spliceengine/blob/branch-2.7/platforms/std/docs/STD-installation.md
2) More refer to
   https://github.com/fastrivertech/tiger/wiki/Install-Splice-Machine
 
Setup
-----
1) create frt user and schema
   bin/sqlshell.sh
   splice>connect 'jdbc:splice://localhost:1527/splicedb;user=splice;password=admin';
   splice>run '/home/ec2-user/frt/create_schema.sql';
   splice>exit;
   
   drop frt user and schema
   splice>run '/home/ec2-user/frt/drop_schema.sql';
   
2) create tables
   bin/sqlshell.sh
   splice>connect 'jdbc:splice://localhost:1527/splicedb;user=frt;password=frt';
   splice>run '/home/ec2-user/frt/create_patient_tables.sql';
   splice>run '/home/ec2-user/frt/create_patient_indexes.sql';

   drop tables
   splice>run '/home/ec2-user/frt/drop_patient_tables.sql';
   splice>run '/home/ec2-user/frt/drop_patient_indexes.sql';
      
	  
Splice Machine sqlshell Command Reference
-----------------------------------------
splice>describe [table name];
splice>values current_user;
splice>show tables in [user name]
splice>create schema
splice>call syscs_util.syscs_create_user('username', 'password');
splice>call syscs_util.syscs_update_schema_owner('username', 'password')
splice>call syscs_util.syscs_drop_user('username');
splice>call syscs_util.syscs_kill_transaction procedure to kill the old transaction

scp -i tiger.pem C:\frt-dev\tiger\frt-schema\sp\create_patient_tables.sql ec2-user@ec2-54-202-187-87.us-west-2.compute.amazonaws.com:~/frt
select p.patient_id, p.active, p.gender, n.humanname_id, n.use, n.family from Patient p inner join patient_humanname n on p.patient_id = n.patient_id;
delete from patient where patient_id = 10000;
delete from patient_humanname where patient_id = 10000;

Export & Import FHIR Patient Resource Tables
--------------------------------------------
1) Export
   1.1) connect to splice machine with source schema e.g. user 'frt' password 'frt'
		bin/sqlshell.sh
		splice>connect 'jdbc:splice://localhost:1527/splicedb;user=frt;password=frt';
   1.2) export tables into csv files
        splice>run '/home/ec2-user/frt/export_patients.sql';

2) Import
   2.1) connect to splice machine with admin priviledge, e.g. user 'splice' password 'admin'
        bin/sqlshell.sh
        splice>connect 'jdbc:splice://localhost:1527/splicedb;user=splice;password=admin';
   2.2) import into tables from their csv files
        splice>run '/home/ec2-user/frt/import_patients.sql';
Note: import_patients.sql hard coded target schema as 'frt2', change it accordingly.
