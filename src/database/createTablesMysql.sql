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

DROP TABLE IF EXISTS laboratorymanager;
DROP TABLE IF EXISTS laboratoryuser;
DROP TABLE IF EXISTS phonenumber;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS laboratory;

CREATE TABLE laboratory (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  proteomic tinyint(1) NOT NULL DEFAULT '0',
  registerTime datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY email (email)
);
CREATE TABLE address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) DEFAULT NULL,
  address varchar(150) NOT NULL,
  address2 varchar(150) DEFAULT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(50) NOT NULL,
  billing tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE phonenumber (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) DEFAULT NULL,
  type enum('WORK','MOBILE','FAX') NOT NULL DEFAULT 'WORK',
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratoryuser (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratorymanager (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (userId, laboratoryId),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);