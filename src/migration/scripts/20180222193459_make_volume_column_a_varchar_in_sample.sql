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

-- // make volume column a varchar in sample
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
MODIFY COLUMN volume VARCHAR(100) DEFAULT NULL;
UPDATE sample
SET volume = CONCAT(volume, ' μl');

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE sample
SET volume = NULL
WHERE volume NOT REGEXP '^[0-9]*\.?[0-9]* μl$';
UPDATE sample
SET volume = SUBSTRING(volume, 1, LENGTH(volume)-LENGTH(' μl'))
WHERE volume REGEXP '^[0-9]*\.?[0-9]* μl$';
ALTER TABLE sample
MODIFY COLUMN volume DOUBLE DEFAULT NULL;
