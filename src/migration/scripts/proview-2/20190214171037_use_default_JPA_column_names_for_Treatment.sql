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

-- // use default JPA column names for Treatment
-- Migration SQL that makes the change goes here.

ALTER TABLE treatment
DROP FOREIGN KEY treatmentProtocol_ibfk,
DROP FOREIGN KEY treatmentUser_ibfk;
ALTER TABLE treatment
CHANGE COLUMN protocolId protocol_id bigint(20) DEFAULT NULL,
CHANGE COLUMN fractionationType fractionationtype varchar(50) DEFAULT NULL,
CHANGE COLUMN userId user_id bigint(20) DEFAULT NULL,
CHANGE COLUMN insertTime inserttime datetime NOT NULL,
CHANGE COLUMN deletionType deletiontype varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionExplanation deletionexplanation text;
ALTER TABLE treatment
ADD CONSTRAINT treatment_protocol_ibfk FOREIGN KEY (protocol_id) REFERENCES protocol (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatment_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE treatment
DROP FOREIGN KEY treatment_protocol_ibfk,
DROP FOREIGN KEY treatment_user_ibfk;
ALTER TABLE treatment
CHANGE COLUMN protocol_id protocolId bigint(20) DEFAULT NULL,
CHANGE COLUMN fractionationtype fractionationType varchar(50) DEFAULT NULL,
CHANGE COLUMN user_id userId bigint(20) DEFAULT NULL,
CHANGE COLUMN inserttime insertTime datetime NOT NULL,
CHANGE COLUMN deletiontype deletionType varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionexplanation deletionExplanation text;
ALTER TABLE treatment
ADD CONSTRAINT treatmentProtocol_ibfk FOREIGN KEY (protocolId) REFERENCES protocol (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatmentUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;
