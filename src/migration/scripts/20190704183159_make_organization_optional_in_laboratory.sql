-- // make organization optional in laboratory
-- Migration SQL that makes the change goes here.

ALTER TABLE laboratory
MODIFY organization varchar(255) DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE laboratory
SET organization = 'IRCM'
WHERE organization IS NULL;
ALTER TABLE laboratory
MODIFY organization varchar(255) NOT NULL;
