-- // add denis faubert as approver
-- Migration SQL that makes the change goes here.

UPDATE user
SET approver = 1
WHERE email = 'denis.faubert@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET approver = 0
WHERE email = 'denis.faubert@ircm.qc.ca';
