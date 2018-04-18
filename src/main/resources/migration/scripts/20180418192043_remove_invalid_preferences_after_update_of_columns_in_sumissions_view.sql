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

-- // remove invalid preferences after update of columns in sumissions view
-- Migration SQL that makes the change goes here.

DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'columnOrder';
DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'submission.goal';


-- //@UNDO
-- SQL to undo the change goes here.


