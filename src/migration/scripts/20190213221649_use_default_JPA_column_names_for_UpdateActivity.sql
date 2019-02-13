-- // use default JPA column names for UpdateActivity
-- Migration SQL that makes the change goes here.

ALTER TABLE activityupdate
CHANGE COLUMN tableName tablename varchar(50) NOT NULL,
CHANGE COLUMN recordId recordid bigint(20) NOT NULL,
CHANGE COLUMN actionType actiontype varchar(50) NOT NULL,
CHANGE COLUMN actionColumn actioncolumn varchar(70) DEFAULT NULL,
CHANGE COLUMN oldValue oldvalue varchar(255) DEFAULT NULL,
CHANGE COLUMN newValue newvalue varchar(255) DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE activityupdate
CHANGE COLUMN tablename tableName varchar(50) NOT NULL,
CHANGE COLUMN recordid recordId bigint(20) NOT NULL,
CHANGE COLUMN actiontype actionType varchar(50) NOT NULL,
CHANGE COLUMN actioncolumn actionColumn varchar(70) DEFAULT NULL,
CHANGE COLUMN oldvalue oldValue varchar(255) DEFAULT NULL,
CHANGE COLUMN newvalue newValue varchar(255) DEFAULT NULL;
