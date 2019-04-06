CREATE DATABASE FHIR_DB;
CREATE USER 'frt'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'frt'@'%';