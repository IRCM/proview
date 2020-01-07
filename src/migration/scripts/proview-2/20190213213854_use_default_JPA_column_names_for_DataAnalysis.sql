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

-- // use default JPA column names for DataAnalysis
-- Migration SQL that makes the change goes here.

ALTER TABLE dataanalysis
DROP FOREIGN KEY dataanalysisSample_ibfk;
ALTER TABLE dataanalysis
CHANGE COLUMN sampleId sample_id bigint(20) NOT NULL,
CHANGE COLUMN maxWorkTime maxworktime double NOT NULL,
CHANGE COLUMN workTime worktime double DEFAULT NULL,
CHANGE COLUMN analysisType type varchar(50) NOT NULL;
ALTER TABLE dataanalysis
ADD CONSTRAINT dataanalysis_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE dataanalysis
DROP FOREIGN KEY dataanalysis_sample_ibfk;
ALTER TABLE dataanalysis
CHANGE COLUMN sample_id sampleId bigint(20) NOT NULL,
CHANGE COLUMN maxWorktime maxWorkTime double NOT NULL,
CHANGE COLUMN worktime workTime double DEFAULT NULL,
CHANGE COLUMN type analysisType varchar(50) NOT NULL;
ALTER TABLE dataanalysis
ADD CONSTRAINT dataanalysisSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;
