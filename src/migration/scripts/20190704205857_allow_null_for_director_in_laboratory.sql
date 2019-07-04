-- // allow null for director in laboratory
-- Migration SQL that makes the change goes here.

ALTER TABLE laboratory
MODIFY director varchar(255) DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE laboratory
SET director = 'Unknown'
WHERE director IS NULL;
ALTER TABLE laboratory
MODIFY director varchar(255) NOT NULL;
