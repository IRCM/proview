-- // remove unique constraint on plate name
-- Migration SQL that makes the change goes here.

ALTER TABLE plate
DROP INDEX name;
ALTER TABLE plate
ADD INDEX name (name);


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE plate
DROP INDEX name;
ALTER TABLE plate
ADD CONSTRAINT name UNIQUE (name);
