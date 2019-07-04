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

-- // convert submission's solvents to list of enums
-- Migration SQL that makes the change goes here.

UPDATE activityupdate
JOIN solvent ON activityupdate.recordId = solvent.id
SET tableName = 'submission',
  recordId = solvent.submissionId,
  actionColumn = 'solvent',
  newValue = solvent.solvent
WHERE tableName = 'solvent'
AND actionType = 'INSERT';
UPDATE activityupdate
JOIN solvent ON activityupdate.recordId = solvent.id
SET tableName = 'submission',
  recordId = solvent.submissionId,
  actionColumn = 'solvent',
  oldValue = solvent.solvent
WHERE tableName = 'solvent'
AND actionType = 'DELETE';
DELETE FROM solvent
WHERE deleted = 1;


-- //@UNDO
-- SQL to undo the change goes here.

-- No need to undo changes.
