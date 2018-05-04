--
-- Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

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
