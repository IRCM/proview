-- // remove benoit coulombe from approvers
-- Migration SQL that makes the change goes here.

UPDATE user
SET approver = 0
WHERE email = 'benoit.coulombe@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET approver = 1
WHERE email = 'benoit.coulombe@ircm.qc.ca';
