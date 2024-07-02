-- // add version number to sample and submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN version int(11) DEFAULT '0' AFTER dataAvailableDate;
ALTER TABLE sample
ADD COLUMN version int(11) DEFAULT '0' AFTER category;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
DROP COLUMN version;
ALTER TABLE sample
DROP COLUMN version;
