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

-- // merges laboratoryuser table into user
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN laboratory_id bigint(20) DEFAULT NULL;
UPDATE user
JOIN laboratoryuser ON user.id = laboratoryuser.user_id
SET user.laboratory_id = laboratoryuser.laboratory_id;
ALTER TABLE user
MODIFY COLUMN laboratory_id bigint(20) NOT NULL,
ADD CONSTRAINT userLaboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON UPDATE CASCADE;
DROP TABLE laboratoryuser;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE IF NOT EXISTS laboratoryuser (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id bigint(20) NOT NULL,
  laboratory_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY member (user_id,laboratory_id),
  KEY user (user_id),
  KEY laboratory (laboratory_id),
  CONSTRAINT laboratoryuserUser_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratoryuserLaboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
INSERT INTO laboratoryuser (user_id,laboratory_id)
SELECT user.id, user.laboratory_id
FROM user;
ALTER TABLE user
DROP CONSTRAINT userLaboratory_ibfk;
ALTER TABLE user
DROP COLUMN laboratory_id;
