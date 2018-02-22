-- // rename sampleType to category in sample table
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
ADD COLUMN category varchar(50) DEFAULT NULL AFTER sampleType;
UPDATE sample
SET category = sampleType;
ALTER TABLE sample
DROP COLUMN sampleType;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sample
ADD COLUMN sampleType varchar(50) DEFAULT NULL AFTER category;
UPDATE sample
SET sampleType = category;
ALTER TABLE sample
DROP COLUMN category;
