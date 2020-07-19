USE FHIR_DB;
# pre-create grafana data source tables with ET - a big int representing unix epoch when the data in the record 
# is generated, these tables will be auto evolved by confluent connect sink mysql;

CREATE TABLE FHIR_GROUPBY_STATE_GENDER (ET TIMESTAMP, TS BIGINT, ADDR_STATE VARCHAR(30), PATIENT_GENDER VARCHAR(20), CNT BIGINT);

# date range table contains one record represent a time range, data point record inflow insert trigger 
# will fetch the date range from this table, and map the influx records to the specified range : this can be in history, current, or future
# e.g. INSERT INTO FHIR_DATE_RANGE VALUES ('2019-01-20 13:00:00', '2019-01-21 13:30:25');
# change the range will cause state / gender aggregate records tagged with ET timestamp that fall in the range.
CREATE TABLE FHIR_DATE_RANGE (START_DATE VARCHAR(20), END_DATE VARCHAR(20));

# define a function that will be called by triggers

DELIMITER $$
CREATE FUNCTION GET_TS_IN_RANGE() 
RETURNS TIMESTAMP
DETERMINISTIC
BEGIN 
  DECLARE TS TIMESTAMP;
  DECLARE TS_BEGIN VARCHAR(20);
  DECLARE TS_END VARCHAR(20);
  DECLARE TS_BEGIN_UNIX TIMESTAMP;
  DECLARE TS_END_UNIX TIMESTAMP;
  DECLARE TS_GAP BIGINT;
  SELECT START_DATE, END_DATE INTO @TS_BEGIN, @TS_END FROM FHIR_DATE_RANGE;
  SET @TS_START_UNIX:=UNIX_TIMESTAMP(@TS_BEGIN);
  SET @TS_END_UNIX:=UNIX_TIMESTAMP(@TS_END);
  SET @TS_GAP:=@TS_END_UNIX-@TS_START_UNIX;
  SET @TS:=@TS_START_UNIX+FLOOR(RAND()*@TS_GAP); 
  RETURN FROM_UNIXTIME(@TS);
END$$
DELIMITER ;

# define trigger on state gender tables

delimiter $$
CREATE TRIGGER DS_TRIGGER_STATE_GENDER BEFORE INSERT ON FHIR_GROUPBY_STATE_GENDER 
FOR EACH ROW 
BEGIN 
SET NEW.ET:=GET_TS_IN_RANGE(); 
END$$
delimiter ;

