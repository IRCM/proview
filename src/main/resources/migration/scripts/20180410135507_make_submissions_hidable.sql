-- // make submissions hidable
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN hidden tinyint(1) NOT NULL DEFAULT '0';


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
DROP COLUMN hidden;
