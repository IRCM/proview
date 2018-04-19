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
DROP FOREIGN KEY samplecontainerSample_ibfk;
DROP TABLE IF EXISTS userpreference;
DROP TABLE IF EXISTS preference;
DROP TABLE IF EXISTS activityupdate;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS dataanalysis;
DROP TABLE IF EXISTS acquisition;
DROP TABLE IF EXISTS msanalysis;
DROP TABLE IF EXISTS treatedsample;
DROP TABLE IF EXISTS treatment;
DROP TABLE IF EXISTS protocol;
DROP TABLE IF EXISTS solvent;
DROP TABLE IF EXISTS contaminant;
DROP TABLE IF EXISTS standard;
DROP TABLE IF EXISTS sample;
DROP TABLE IF EXISTS samplecontainer;
DROP TABLE IF EXISTS plate;
DROP TABLE IF EXISTS submissionfiles;
DROP TABLE IF EXISTS submission;
DROP TABLE IF EXISTS forgotpassword;
DROP TABLE IF EXISTS laboratorymanager;
DROP TABLE IF EXISTS laboratoryuser;
DROP TABLE IF EXISTS phonenumber;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS laboratory;
