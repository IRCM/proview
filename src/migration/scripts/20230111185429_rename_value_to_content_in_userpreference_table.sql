-- // rename value to content in userpreference table
-- Migration SQL that makes the change goes here.

ALTER TABLE userpreference
CHANGE COLUMN value content longblob DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE userpreference
CHANGE COLUMN content value longblob DEFAULT NULL;
