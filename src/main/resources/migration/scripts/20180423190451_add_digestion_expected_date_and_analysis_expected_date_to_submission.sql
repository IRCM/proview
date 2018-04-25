--
--    Copyright 2010-2016 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

-- // add digestion expected date and analysis expected date to submission
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
