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

-- // use default JPA column names for ForgotPassword
-- Migration SQL that makes the change goes here.

ALTER TABLE forgotpassword
DROP FOREIGN KEY forgotpasswordUser_ibfk;
ALTER TABLE forgotpassword
CHANGE COLUMN userId user_id bigint(20) NOT NULL,
CHANGE COLUMN requestMoment requestmoment datetime NOT NULL,
CHANGE COLUMN confirmNumber confirmnumber int(11) NOT NULL;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpassword_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE forgotpassword
DROP FOREIGN KEY forgotpassword_user_ibfk;
ALTER TABLE forgotpassword
CHANGE COLUMN user_id userId bigint(20) NOT NULL,
CHANGE COLUMN requestmoment requestMoment datetime NOT NULL,
CHANGE COLUMN confirmnumber confirmNumber int(11) NOT NULL;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpasswordUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
