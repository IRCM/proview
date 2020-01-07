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

-- // use default JPA column names for Laboratory
-- Migration SQL that makes the change goes here.

ALTER TABLE laboratorymanager
DROP FOREIGN KEY laboratorymanagerLaboratory_ibfk,
DROP FOREIGN KEY laboratorymanagerUser_ibfk;
ALTER TABLE laboratorymanager
CHANGE COLUMN userId managers_id BIGINT NOT NULL,
CHANGE COLUMN laboratoryId laboratory_id BIGINT NOT NULL;
ALTER TABLE laboratorymanager
ADD CONSTRAINT laboratorymanager_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory(id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratorymanager_managers_ibfk FOREIGN KEY (managers_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE laboratorymanager
DROP FOREIGN KEY laboratorymanager_laboratory_ibfk,
DROP FOREIGN KEY laboratorymanager_managers_ibfk;
ALTER TABLE laboratorymanager
CHANGE COLUMN managers_id userId BIGINT NOT NULL,
CHANGE COLUMN laboratory_id laboratoryId BIGINT NOT NULL;
ALTER TABLE laboratorymanager
ADD CONSTRAINT laboratorymanagerLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory(id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratorymanagerUser_ibfk FOREIGN KEY (userId) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE;
