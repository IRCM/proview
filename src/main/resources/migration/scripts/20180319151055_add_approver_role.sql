-- // add_approver_role
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN approver tinyint(1) NOT NULL DEFAULT '0' AFTER admin;
UPDATE user
SET approver = 1
WHERE id = 1
OR email = 'benoit.coulombe@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET admin = 1
WHERE approver = 1;
ALTER TABLE user
DROP COLUMN approver;
