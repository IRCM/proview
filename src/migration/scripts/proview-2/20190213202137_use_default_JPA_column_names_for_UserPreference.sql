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

-- // use default JPA column names for UserPreference
-- Migration SQL that makes the change goes here.

ALTER TABLE userpreference
DROP FOREIGN KEY userpreferencePreference_ibfk,
DROP FOREIGN KEY userpreferenceUser_ibfk;
ALTER TABLE userpreference
CHANGE COLUMN preferenceId preference_id bigint(20) NOT NULL,
CHANGE COLUMN userId user_id bigint(20) NOT NULL;
ALTER TABLE userpreference
ADD CONSTRAINT userpreference_preference_ibfk FOREIGN KEY (preference_id) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT userpreference_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE userpreference
DROP FOREIGN KEY userpreference_preference_ibfk,
DROP FOREIGN KEY userpreference_user_ibfk;
ALTER TABLE userpreference
CHANGE COLUMN preference_id preferenceId bigint(20) NOT NULL,
CHANGE COLUMN user_id userId bigint(20) NOT NULL;
ALTER TABLE userpreference
ADD CONSTRAINT userpreferencePreference_ibfk FOREIGN KEY (preferenceId) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT userpreferenceUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
