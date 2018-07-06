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

-- // add_approver_role
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN approver tinyint(1) NOT NULL DEFAULT '0' AFTER admin;
UPDATE user
SET approver = 1
WHERE id = 1
OR email = 'benoit.coulombe@ircm.qc.ca';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE user
SET admin = 1
WHERE approver = 1;
ALTER TABLE user
DROP COLUMN approver;
