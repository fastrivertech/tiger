CREATE DATABASE FHIR_DB;
CREATE USER 'frt'@'%' IDENTIFIED WITH mysql_native_password BY 'F@stR1ver@USA';
GRANT ALL PRIVILEGES ON *.* TO 'frt'@'%';
