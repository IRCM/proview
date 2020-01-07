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

-- // adds manager property to user
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN manager TINYINT DEFAULT '0' AFTER admin;
UPDATE user
JOIN laboratorymanager ON user.id = laboratorymanager.managers_id
SET user.manager = 1;
DROP TABLE laboratorymanager;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE IF NOT EXISTS laboratorymanager (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  managers_id bigint(20) NOT NULL,
  laboratory_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (managers_id,laboratory_id),
  CONSTRAINT laboratorymanager_managers_ibfk FOREIGN KEY (managers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratorymanager_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
INSERT INTO laboratorymanager (managers_id,laboratory_id)
SELECT user.id, user.laboratory_id
FROM user
WHERE user.manager = 1;
ALTER TABLE user
DROP COLUMN manager;
