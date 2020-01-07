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

-- // use default JPA column names for Sample
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
DROP FOREIGN KEY sampleSubmission_ibfk,
DROP FOREIGN KEY sampleContainer_ibfk;
ALTER TABLE sample
CHANGE COLUMN controlType controltype varchar(50) DEFAULT NULL,
CHANGE COLUMN containerId originalcontainer_id bigint(20) DEFAULT NULL,
CHANGE COLUMN numberProtein numberprotein int(11) DEFAULT NULL,
CHANGE COLUMN submissionId submission_id bigint(20) DEFAULT NULL,
CHANGE COLUMN listIndex listindex int(10) DEFAULT NULL,
CHANGE COLUMN molecularWeight molecularweight double DEFAULT NULL;
ALTER TABLE sample
ADD CONSTRAINT sample_submission_ibfk FOREIGN KEY (submission_id) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT sample_container_ibfk FOREIGN KEY (originalcontainer_id) REFERENCES samplecontainer (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE contaminant
DROP FOREIGN KEY contaminantSample_ibfk;
ALTER TABLE contaminant
CHANGE COLUMN sampleId contaminants_id bigint(20) DEFAULT NULL;
ALTER TABLE contaminant
ADD CONSTRAINT contaminant_sample_ibfk FOREIGN KEY (contaminants_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE standard
DROP FOREIGN KEY standardSample_ibfk;
ALTER TABLE standard
CHANGE COLUMN sampleId standards_id bigint(20) DEFAULT NULL;
ALTER TABLE standard
ADD CONSTRAINT standard_sample_ibfk FOREIGN KEY (standards_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE standard
DROP FOREIGN KEY standard_sample_ibfk;
ALTER TABLE standard
CHANGE COLUMN standards_id sampleId bigint(20) DEFAULT NULL;
ALTER TABLE standard
ADD CONSTRAINT standardSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE contaminant
DROP FOREIGN KEY contaminant_sample_ibfk;
ALTER TABLE contaminant
CHANGE COLUMN contaminants_id sampleId bigint(20) DEFAULT NULL;
ALTER TABLE contaminant
ADD CONSTRAINT contaminantSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE sample
DROP FOREIGN KEY sample_submission_ibfk,
DROP FOREIGN KEY sample_container_ibfk;
ALTER TABLE sample
CHANGE COLUMN controltype controlType varchar(50) DEFAULT NULL,
CHANGE COLUMN originalcontainer_id containerId bigint(20) DEFAULT NULL,
CHANGE COLUMN numberprotein numberProtein int(11) DEFAULT NULL,
CHANGE COLUMN submission_id submissionId bigint(20) DEFAULT NULL,
CHANGE COLUMN listindex listIndex int(10) DEFAULT NULL,
CHANGE COLUMN molecularweight molecularWeight double DEFAULT NULL;
ALTER TABLE sample
ADD CONSTRAINT sampleSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT sampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON DELETE CASCADE ON UPDATE CASCADE;
