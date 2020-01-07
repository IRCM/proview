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

-- // remove unused dataanalysis table
-- Migration SQL that makes the change goes here.

DROP TABLE dataanalysis;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE dataanalysis (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  sample_id bigint(20) NOT NULL,
  protein varchar(255) NOT NULL,
  peptide varchar(255) DEFAULT NULL,
  maxworktime double NOT NULL,
  score text,
  worktime double DEFAULT NULL,
  status varchar(50) NOT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  KEY sample (sample_id),
  CONSTRAINT dataanalysis_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
