-- // remove approver role
-- Migration SQL that makes the change goes here.

ALTER TABLE user
DROP COLUMN approver;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE user
ADD COLUMN approver tinyint(1) NOT NULL DEFAULT '0' AFTER admin;
UPDATE user
SET approver = 1
WHERE id = 1
OR email = 'denis.faubert@ircm.qc.ca';
