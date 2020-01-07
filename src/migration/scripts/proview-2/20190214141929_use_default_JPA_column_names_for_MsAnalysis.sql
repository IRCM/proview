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

-- // use default JPA column names for MsAnalysis
-- Migration SQL that makes the change goes here.

ALTER TABLE msanalysis
CHANGE COLUMN massDetectionInstrument massdetectioninstrument varchar(100) NOT NULL,
CHANGE COLUMN insertTime inserttime datetime NOT NULL,
CHANGE COLUMN deletionType deletiontype varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionExplanation deletionexplanation text;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE msanalysis
CHANGE COLUMN massdetectioninstrument massDetectionInstrument varchar(100) NOT NULL,
CHANGE COLUMN inserttime insertTime datetime NOT NULL,
CHANGE COLUMN deletiontype deletionType varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionexplanation deletionExplanation text;
