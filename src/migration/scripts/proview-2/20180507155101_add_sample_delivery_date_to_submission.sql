-- // add sample delivery date to submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN sampleDeliveryDate date DEFAULT NULL AFTER submissionDate;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
DROP COLUMN sampleDeliveryDate;
