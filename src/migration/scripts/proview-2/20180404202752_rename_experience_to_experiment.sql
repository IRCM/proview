-- // rename experiment to experiment
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
CHANGE experience experiment varchar(100) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
CHANGE experiment experience varchar(100) DEFAULT NULL;
