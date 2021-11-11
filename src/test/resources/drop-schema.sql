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

ALTER TABLE IF EXISTS samplecontainer
DROP CONSTRAINT IF EXISTS samplecontainer_ibfk_2;
DROP TABLE userpreference IF EXISTS;
DROP TABLE preference IF EXISTS;
DROP TABLE activityupdate IF EXISTS;
DROP TABLE activity IF EXISTS;
DROP TABLE dataanalysis IF EXISTS;
DROP TABLE acquisition IF EXISTS;
DROP TABLE msanalysis IF EXISTS;
DROP TABLE treatedsample IF EXISTS;
DROP TABLE treatment IF EXISTS;
DROP TABLE protocol IF EXISTS;
DROP TABLE solvent IF EXISTS;
DROP TABLE standard IF EXISTS;
DROP TABLE sample IF EXISTS;
DROP TABLE samplecontainer IF EXISTS;
DROP TABLE plate IF EXISTS;
DROP TABLE structure IF EXISTS;
DROP TABLE submissionfiles IF EXISTS;
DROP TABLE gelimages IF EXISTS;
DROP TABLE submission IF EXISTS;
DROP TABLE forgotpassword IF EXISTS;
DROP TABLE phonenumber IF EXISTS;
DROP TABLE user IF EXISTS;
DROP TABLE address IF EXISTS;
DROP TABLE laboratory IF EXISTS;
