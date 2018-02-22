-- // rename sampleType to category in sample table
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
CHANGE COLUMN sampleType category varchar(50) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sample
CHANGE COLUMN category sampleType varchar(50) DEFAULT NULL;
