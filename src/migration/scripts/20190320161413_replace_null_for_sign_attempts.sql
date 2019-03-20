-- // replace null for sign attempts
-- Migration SQL that makes the change goes here.

UPDATE user
SET signattempts = 0
WHERE signattempts IS NULL;
ALTER TABLE user
MODIFY COLUMN signattempts int(10) NOT NULL DEFAULT '0';

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE user
MODIFY COLUMN signattempts int(10) DEFAULT NULL;
