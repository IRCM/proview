-- // allow null for listindex in acquisition
-- Migration SQL that makes the change goes here.

ALTER TABLE acquisition
MODIFY listIndex int(11) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE acquisition
MODIFY listIndex int(11) NOT NULL;
