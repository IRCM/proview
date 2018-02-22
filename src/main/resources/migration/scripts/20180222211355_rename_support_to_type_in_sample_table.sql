-- // rename support to type in sample table
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
CHANGE COLUMN support type varchar(50) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sample
CHANGE COLUMN type support varchar(50) DEFAULT NULL;
