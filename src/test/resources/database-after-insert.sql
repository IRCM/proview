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

ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainer_ibfk_2 FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE;

-- Fix generated columns next value.
ALTER TABLE laboratory ALTER COLUMN id RESTART WITH 6;
ALTER TABLE address ALTER COLUMN id RESTART WITH 15;
ALTER TABLE users ALTER COLUMN id RESTART WITH 28;
ALTER TABLE phonenumber ALTER COLUMN id RESTART WITH 15;
ALTER TABLE forgotpassword ALTER COLUMN id RESTART WITH 11;
ALTER TABLE submission ALTER COLUMN id RESTART WITH 165;
ALTER TABLE submissionfiles ALTER COLUMN id RESTART WITH 4;
ALTER TABLE plate ALTER COLUMN id RESTART WITH 124;
ALTER TABLE samplecontainer ALTER COLUMN id RESTART WITH 2281;
ALTER TABLE sample ALTER COLUMN id RESTART WITH 644;
ALTER TABLE standard ALTER COLUMN id RESTART WITH 1;
ALTER TABLE solvent ALTER COLUMN id RESTART WITH 55;
ALTER TABLE protocol ALTER COLUMN id RESTART WITH 5;
ALTER TABLE treatment ALTER COLUMN id RESTART WITH 325;
ALTER TABLE treatedsample ALTER COLUMN id RESTART WITH 475;
ALTER TABLE msanalysis ALTER COLUMN id RESTART WITH 26;
ALTER TABLE acquisition ALTER COLUMN id RESTART WITH 426;
ALTER TABLE dataanalysis ALTER COLUMN id RESTART WITH 6;
ALTER TABLE activity ALTER COLUMN id RESTART WITH 5937;
ALTER TABLE activityupdate ALTER COLUMN id RESTART WITH 1446;
ALTER TABLE preference ALTER COLUMN id RESTART WITH 3;
ALTER TABLE userpreference ALTER COLUMN id RESTART WITH 5;
