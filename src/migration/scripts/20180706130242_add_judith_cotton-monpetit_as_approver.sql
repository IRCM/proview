-- // add judith cotton-monpetit as approver
-- Migration SQL that makes the change goes here.

UPDATE user
SET approver = 1
WHERE email = 'judith.cotton-monpetit@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET approver = 0
WHERE email = 'judith.cotton-monpetit@ircm.qc.ca';
