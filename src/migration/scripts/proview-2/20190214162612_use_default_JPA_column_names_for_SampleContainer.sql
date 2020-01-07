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

-- // use default JPA column names for SampleContainer
-- Migration SQL that makes the change goes here.

ALTER TABLE samplecontainer
DROP FOREIGN KEY samplecontainerSample_ibfk,
DROP FOREIGN KEY samplecontainerPlate_ibfk;
ALTER TABLE samplecontainer
CHANGE COLUMN plateId plate_id bigint(20) DEFAULT NULL,
CHANGE COLUMN locationColumn col int(11) DEFAULT NULL,
CHANGE COLUMN locationRow row int(11) DEFAULT NULL,
CHANGE COLUMN sampleId sample_id bigint(20) DEFAULT NULL,
CHANGE COLUMN time timestamp datetime NOT NULL;
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainer_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT samplecontainer_plate_ibfk FOREIGN KEY (plate_id) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE samplecontainer
DROP FOREIGN KEY samplecontainer_sample_ibfk,
DROP FOREIGN KEY samplecontainer_plate_ibfk;
ALTER TABLE samplecontainer
CHANGE COLUMN plate_id plateId bigint(20) DEFAULT NULL,
CHANGE COLUMN col locationColumn int(11) DEFAULT NULL,
CHANGE COLUMN row locationRow int(11) DEFAULT NULL,
CHANGE COLUMN sample_id sampleId bigint(20) DEFAULT NULL,
CHANGE COLUMN timestamp time datetime NOT NULL;
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainerSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT samplecontainerPlate_ibfk FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE;
