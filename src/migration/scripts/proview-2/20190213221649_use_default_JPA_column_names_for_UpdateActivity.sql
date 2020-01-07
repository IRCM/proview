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

-- // use default JPA column names for UpdateActivity
-- Migration SQL that makes the change goes here.

ALTER TABLE activityupdate
CHANGE COLUMN tableName tablename varchar(50) NOT NULL,
CHANGE COLUMN recordId recordid bigint(20) NOT NULL,
CHANGE COLUMN actionType actiontype varchar(50) NOT NULL,
CHANGE COLUMN actionColumn actioncolumn varchar(70) DEFAULT NULL,
CHANGE COLUMN oldValue oldvalue varchar(255) DEFAULT NULL,
CHANGE COLUMN newValue newvalue varchar(255) DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE activityupdate
CHANGE COLUMN tablename tableName varchar(50) NOT NULL,
CHANGE COLUMN recordid recordId bigint(20) NOT NULL,
CHANGE COLUMN actiontype actionType varchar(50) NOT NULL,
CHANGE COLUMN actioncolumn actionColumn varchar(70) DEFAULT NULL,
CHANGE COLUMN oldvalue oldValue varchar(255) DEFAULT NULL,
CHANGE COLUMN newvalue newValue varchar(255) DEFAULT NULL;
