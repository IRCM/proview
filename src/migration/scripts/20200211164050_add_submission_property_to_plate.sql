-- // add submission property to plate
-- Migration SQL that makes the change goes here.

ALTER TABLE plate
ADD COLUMN submission_id bigint(20) DEFAULT NULL AFTER submission;
ALTER TABLE plate
ADD CONSTRAINT plateSubmission_ibfk FOREIGN KEY (submission_id) REFERENCES submission (id) ON UPDATE CASCADE ON DELETE CASCADE;
UPDATE plate
JOIN samplecontainer ON samplecontainer.plate_id = plate.id
JOIN sample ON sample.originalcontainer_id = samplecontainer.id
SET plate.submission_id = sample.submission_id
WHERE plate.submission = 1;
ALTER TABLE plate
DROP COLUMN submission;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE plate
ADD COLUMN submission tinyint NOT NULL DEFAULT '0' AFTER submission_id;
UPDATE plate
SET submission = 1
WHERE submission_id IS NOT NULL;
ALTER TABLE plate
DROP FOREIGN KEY plateSubmission_ibfk;
ALTER TABLE plate
DROP COLUMN submission_id;
