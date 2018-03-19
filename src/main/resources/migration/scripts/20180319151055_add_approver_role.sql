-- // add_approver_role
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN approver tinyint(1) NOT NULL DEFAULT '0';
UPDATE user
SET approver = 1
WHERE id = 1
OR email = 'benoit.coulombe@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE user
DROP COLUMN approver;
