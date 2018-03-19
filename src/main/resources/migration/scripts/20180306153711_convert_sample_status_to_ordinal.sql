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

-- // convert sample status to ordinal
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
ADD COLUMN ordinalStatus int(5) DEFAULT NULL AFTER status;
UPDATE sample
SET ordinalStatus = 0
WHERE status = 'TO_APPROVE';
UPDATE sample
SET ordinalStatus = 1
WHERE status = 'APPROVED';
UPDATE sample
SET ordinalStatus = 2
WHERE status = 'RECEIVED';
UPDATE sample
SET ordinalStatus = 3
WHERE status = 'DIGESTED';
UPDATE sample
SET ordinalStatus = 4
WHERE status = 'ENRICHED';
UPDATE sample
SET ordinalStatus = 5
WHERE status = 'CANCELLED';
UPDATE sample
SET ordinalStatus = 6
WHERE status = 'DATA_ANALYSIS';
UPDATE sample
SET ordinalStatus = 7
WHERE status = 'ANALYSED';
ALTER TABLE sample
DROP COLUMN status;
ALTER TABLE sample
CHANGE COLUMN ordinalStatus status int(5) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sample
CHANGE COLUMN status ordinalStatus int(5) DEFAULT NULL;
ALTER TABLE sample
ADD COLUMN status varchar(50) DEFAULT NULL;
UPDATE sample
SET status = 'TO_APPROVE'
WHERE ordinalStatus = 0;
UPDATE sample
SET status = 'APPROVED'
WHERE ordinalStatus = 1;
UPDATE sample
SET status = 'RECEIVED'
WHERE ordinalStatus = 2;
UPDATE sample
SET status = 'DIGESTED'
WHERE ordinalStatus = 3;
UPDATE sample
SET status = 'ENRICHED'
WHERE ordinalStatus = 4;
UPDATE sample
SET status = 'CANCELLED'
WHERE ordinalStatus = 5;
UPDATE sample
SET status = 'DATA_ANALYSIS'
WHERE ordinalStatus = 6;
UPDATE sample
SET status = 'ANALYSED'
WHERE ordinalStatus = 7;
ALTER TABLE sample
DROP COLUMN ordinalStatus;
