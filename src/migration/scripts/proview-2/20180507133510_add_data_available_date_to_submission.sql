-- // add data available date to submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN dataAvailableDate date DEFAULT NULL AFTER analysisDate;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
DROP COLUMN dataAvailableDate;
