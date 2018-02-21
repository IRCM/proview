-- // add benoit coulombe as admin
-- Migration SQL that makes the change goes here.

UPDATE user
SET admin = 1
WHERE email = 'benoit.coulombe@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET admin = 0
WHERE email = 'benoit.coulombe@ircm.qc.ca';
