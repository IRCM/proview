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
