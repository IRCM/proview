-- // make submission's experiment column non-null
-- Migration SQL that makes the change goes here.

UPDATE submission
JOIN sample ON sample.submission_id = submission.id
SET experiment = sample.name
WHERE service = 'SMALL_MOLECULE';
ALTER TABLE submission
CHANGE COLUMN experiment experiment varchar(100) NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
CHANGE COLUMN experiment experiment varchar(100) DEFAULT NULL;
UPDATE submission
SET experiment = NULL
WHERE service = 'SMALL_MOLECULE';
