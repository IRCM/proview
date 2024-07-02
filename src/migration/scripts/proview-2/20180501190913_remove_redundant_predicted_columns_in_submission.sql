-- // remove redundant predicted columns in submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
DROP COLUMN digestionDatePredicted,
DROP COLUMN analysisDatePredicted;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
ADD COLUMN digestionDatePredicted tinyint(1) NOT NULL DEFAULT '0' AFTER digestionDate,
ADD COLUMN analysisDatePredicted tinyint(1) NOT NULL DEFAULT '0' AFTER analysisDate;
UPDATE submission
JOIN sample ON sample.submissionId = submission.id
SET digestionDatePredicted = 1
WHERE MAX(sample.status) < 3;
UPDATE submission
JOIN sample ON sample.submissionId = submission.id
SET analysisDatePredicted = 1
WHERE MAX(sample.status) < 5;
