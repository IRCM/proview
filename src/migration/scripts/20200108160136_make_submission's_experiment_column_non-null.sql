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

-- // make submission's experiment column non-null
-- Migration SQL that makes the change goes here.

UPDATE submission
JOIN sample ON sample.submission_id = submission.id
SET experiment = sample.name
WHERE service = 'SMALL_MOLECULE';
ALTER TABLE submission
CHANGE COLUMN experiment experiment varchar(100) NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
CHANGE COLUMN experiment experiment varchar(100) DEFAULT NULL;
UPDATE submission
SET experiment = NULL
WHERE service = 'SMALL_MOLECULE';
