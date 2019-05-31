-- // make passwordversion optional in user table
-- Migration SQL that makes the change goes here.

ALTER TABLE user
MODIFY passwordversion int(10) DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET passwordversion = (SELECT max(passwordversion) FROM user)
WHERE passwordversion IS NULL;
ALTER TABLE user
MODIFY passwordversion int(10) NOT NULL;
