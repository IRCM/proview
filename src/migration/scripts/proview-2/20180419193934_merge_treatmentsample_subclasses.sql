-- // merge treatmentsample subclasses
-- Migration SQL that makes the change goes here.

ALTER TABLE treatmentsample
DROP COLUMN treatmentType;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE treatmentsample
ADD COLUMN treatmentType varchar(100) DEFAULT NULL AFTER treatmentId;
UPDATE treatmentsample
JOIN treatment ON treatmentsample.treatmentId = treatment.id
SET treatmentType = treatment.type;
