-- // add digestion predicted date and analysis predicted date to submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN digestionDate date DEFAULT NULL AFTER submissionDate,
ADD COLUMN digestionDatePredicted tinyint(1) NOT NULL DEFAULT '0' AFTER digestionDate,
ADD COLUMN analysisDate date DEFAULT NULL AFTER digestionDatePredicted,
ADD COLUMN analysisDatePredicted tinyint(1) NOT NULL DEFAULT '0' AFTER analysisDate;
UPDATE submission
JOIN sample ON sample.submissionid = submission.id
JOIN treatedsample ON treatedsample.sampleId = sample.id
JOIN treatment ON treatment.id = treatedsample.treatmentId
SET submission.digestionDate = treatment.insertTime
WHERE treatment.type = 'DIGESTION';
UPDATE submission
JOIN sample ON sample.submissionid = submission.id
JOIN acquisition ON acquisition.sampleId = sample.id
JOIN msanalysis ON msanalysis.id = acquisition.msanalysisId
SET submission.analysisDate = msanalysis.insertTime;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
DROP COLUMN digestionDate,
DROP COLUMN digestionDatePredicted,
DROP COLUMN analysisDate,
DROP COLUMN analysisDatePredicted;
