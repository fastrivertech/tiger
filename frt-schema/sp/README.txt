
Splice Machine User Reference
https://doc.splicemachine.com/index.html

Default Standalone Splice Machine 2.7 Users/Passwords
splice/admin
YourUserId/YourPassword

Splice Machine Console
http://localhost:4040

Command List:
splice>connect 'jdbc:splice://localhost:1527/splicedb;user=YourUserId;password=YourPassword';
splice>run '/home/ec2-user/frt/create_patient_tables.sql';
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
   