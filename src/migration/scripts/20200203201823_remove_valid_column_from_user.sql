-- // remove valid column from user
-- Migration SQL that makes the change goes here.

DELETE FROM user
WHERE valid = 0;
ALTER TABLE user
DROP COLUMN valid;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE user
ADD COLUMN valid tinyint(1) NOT NULL DEFAULT '0';
