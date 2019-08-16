Oracle Database 12c
--------------------------
Global database name: orcl
admin user: sys
Password: Welcome1
Pluggable database name: orclpdb
Oracle Enterprise Manager Database Express URL: https://localhost:5500/em

Create a MPI User
----------------------------------------
alter session set "_oracle_script"=true;
create user mpi identified by mpi;
grant all privileges to mpi;
mpi/mpi

Oracle OHMPI Data Manager
-----------------------------
http://localhost:7001/console
weblogic/weblogic1
mpi/Welcome1

Create webservice from WSDL 
------------------------------
wsimport -keep -p com.sun.mdm.index.webservice.client http://localhost:7001/PatientEJB/PatientEJBService?WSDL
jar cvf patient.ws.jar .

OHMPI Documents
----------------------------------------------
https://docs.oracle.com/cd/E72226_02/index.htm
