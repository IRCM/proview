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

DROP TABLE laboratorymanager IF EXISTS;
DROP TABLE laboratoryuser IF EXISTS;
DROP TABLE userpreferences IF EXISTS;
DROP TABLE phonenumber IF EXISTS;
DROP TABLE address IF EXISTS;
DROP TABLE users IF EXISTS;
DROP TABLE laboratory IF EXISTS;

CREATE TABLE laboratory (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE users (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  proteomic tinyint(1) NOT NULL DEFAULT '0',
  registerTime datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY email (email)
);
CREATE TABLE address (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
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
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  type varchar(255) NOT NULL DEFAULT 'WORK',
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (type IN ('WORK','MOBILE','FAX'))
);
CREATE TABLE userpreferences (
  userId bigint(20) NOT NULL,
  preferenceKey varchar(255) NOT NULL,
  preferenceValue varchar(255) DEFAULT NULL,
  UNIQUE (userId, preferenceKey),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratoryuser (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratorymanager (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (userId, laboratoryId),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO laboratory (id,name,organization)
VALUES ('1', 'Proteomic', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('2', 'Translational Proteomics', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('3', 'Chromatin and Genomic Expression', 'IRCM');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('1', 'proview@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 'Robot', '1', '1', '1', NULL);
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('2', 'christian.poitras@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 'Christian Poitras', '1', '1', '1', '2008-08-11 13:43:51');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('3', 'benoit.coulombe@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Benoit Coulombe', '1', '1', '0', '2009-10-02 10:56:19');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('4', 'robert.stlouis@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Robert St-Louis', '0', '0', '0', '2010-01-25 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('5', 'michel.tremblay@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Michel Tremblay', '0', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('6', 'christopher.anderson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Christopher Anderson', '1', '1', '1', '2011-11-11 09:45:26');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('7', 'francois.robert@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'François Robert', '1', '1', '0', '2014-10-06 11:35:45');
INSERT INTO users (id,email,password,salt,passwordVersion,name,active,valid,proteomic,registerTime)
VALUES ('8', 'james.johnson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'James R. Johnson', '1', '1', '0', '2011-07-07 15:48:24');
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('1', '2', '110, avenue des Pins Ouest', '1234', 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('2', '3', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('3', '4', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('4', '5', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('5', '6', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('6', '7', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO address (id,userId,address,address2,town,state,country,postalCode,billing)
VALUES ('7', '8', '110, avenue des Pins Ouest', null, 'Montréal', 'Québec', 'Canada', 'H2W 1R7', true);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('1', '2', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('2', '3', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('3', '4', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('4', '5', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('5', '6', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('6', '7', 'WORK', '514-555-5557', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('7', '8', 'WORK', '514-555-5557', null);
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('1', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('2', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('3', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('4', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('5', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('6', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('7', '3');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('8', '2');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('1', '1');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('2', '1');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('3', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('7', '3');
