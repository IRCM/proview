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

/*
DROP TABLE acquisition_to_mascotfile IF EXISTS;
DROP TABLE acquisition IF EXISTS;
*/
DROP TABLE laboratorymanager IF EXISTS;
DROP TABLE laboratoryuser IF EXISTS;
DROP TABLE phonenumber IF EXISTS;
DROP TABLE users IF EXISTS;
DROP TABLE address IF EXISTS;
DROP TABLE laboratory IF EXISTS;

CREATE TABLE laboratory (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE address (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  line varchar(200) NOT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE users (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  addressId bigint(20) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  admin tinyint(1) NOT NULL DEFAULT '0',
  registerTime datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY email (email),
  FOREIGN KEY (addressId) REFERENCES address (id) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE TABLE phonenumber (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) DEFAULT NULL,
  type varchar(255) NOT NULL DEFAULT 'WORK',
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (type IN ('WORK','MOBILE','FAX'))
);
CREATE TABLE laboratoryuser (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
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
/*
CREATE TABLE acquisition (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  msAnalysisId bigint(20) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  numberOfAcquisition int(11) DEFAULT NULL,
  sampleListName varchar(255) DEFAULT NULL,
  acquisitionFile varchar(255) DEFAULT NULL,
  position int(11) NOT NULL,
  listIndex int(11) NOT NULL,
  comments varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (sampleId,position),
  FOREIGN KEY (msAnalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE INDEX acquisitionFile ON acquisition (acquisitionFile,sampleId);
CREATE TABLE acquisition_to_mascotfile (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  acquisitionId bigint(20) NOT NULL,
  mascotFileId bigint(20) NOT NULL,
  visible tinyint(4) NOT NULL DEFAULT '1',
  comments varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY acquisitionId (acquisitionId,mascotFileId),
  FOREIGN KEY (acquisitionId) REFERENCES acquisition (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (mascotFileId) REFERENCES mascotfile (id) ON DELETE CASCADE ON UPDATE CASCADE
);
*/

INSERT INTO laboratory (id,name,organization)
VALUES ('1', 'Admin', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('2', 'Translational Proteomics', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('3', 'Chromatin and Genomic Expression', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('4', 'Biochemistry of Epigenetic Inheritance', 'IRCM');
INSERT INTO laboratory (id,name,organization)
VALUES ('5', 'Génétique moléculaire et développement', 'IRCM');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('1', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('2', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('3', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('4', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('5', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('6', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('7', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('8', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('9', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('10', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('11', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('12', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('13', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalCode)
VALUES ('14', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('1', 'proview@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 'Robot', null, null, '1', '1', '1', NULL);
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('2', 'christian.poitras@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 'Christian Poitras', 	'fr_CA', 1, '1', '1', '1', '2008-08-11 13:43:51');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('3', 'benoit.coulombe@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Benoit Coulombe', 'fr_CA', 2, '1', '1', '0', '2009-10-02 10:56:19');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('4', 'liam.li@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Liam Li', 'en_CA', 3, '1', '1', '1', '2010-01-25 13:20:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('5', 'jackson.smith@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Jackson Smith', 'en_CA', 4, '1', '1', '1', '2010-01-25 15:45:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('6', 'francois.robert@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'François Robert', 'fr_CA', 8, '0', '0', '0', '2014-10-06 11:35:45');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('7', 'michel.tremblay@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Michel Tremblay', 'fr_CA', 6, '0', '0', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('10', 'christopher.anderson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Christopher Anderson', 'en_US', 7, '1', '1', '0', '2011-11-11 09:45:26');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('11', 'robert.stlouis@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Robert St-Louis', 'fr_CA', 5, '0', '1', '1', '2010-01-25 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('12', 'james.johnson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'James R. Johnson', 'en_US', 9, '0', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('19', 'robert.williams@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Robert Williams', 'en_US', 10, '0', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('24', 'nicole.francis@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Nicole J. Francis', 'en_US', 11, '0', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('25', 'marie.trudel@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Marie Trudel', 'en_US', 12, '0', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('26', 'patricia.jones@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Marie Trudel', 'en_US', 13, '1', '1', '0', '2011-07-07 15:48:24');
INSERT INTO users (id,email,password,salt,passwordVersion,name,locale,addressId,active,valid,admin,registerTime)
VALUES ('27', 'lucas.martin@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 'Lucas Martin', 'en_US', 14, '1', '1', '0', '2011-07-07 15:48:24');
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('1', '2', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('2', '3', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('3', '4', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('4', '5', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('5', '6', 'WORK', '514-555-5557', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('6', '7', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('7', '10', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('8', '11', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('9', '12', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('10', '19', 'WORK', '514-555-5558', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('11', '24', 'WORK', '514-555-5558', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('12', '25', 'WORK', '514-555-5559', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('13', '26', 'WORK', '514-555-5559', null);
INSERT INTO phonenumber (id,userId,type,number,extension)
VALUES ('14', '27', 'WORK', '514-555-5559', null);
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('1', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('2', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('3', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('4', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('5', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('6', '3');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('7', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('10', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('11', '1');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('12', '2');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('19', '4');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('24', '4');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('25', '5');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('26', '5');
INSERT INTO laboratoryuser (userId,laboratoryId)
VALUES ('27', '2');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('1', '1');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('2', '1');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('3', '2');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('6', '3');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('19', '4');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('24', '4');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('25', '5');
INSERT INTO laboratorymanager (userId,laboratoryId)
VALUES ('27', '2');
/*
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (1,1,1,1,1,'XL_20100614_02','XL_20100614_COU_09',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (409,12,442,2,1,'XL_20111014_01','XL_20111014_COU_01',1,0,'after ADH');
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (410,13,446,8,1,'XL_20111014_02','XL_20111014_COU_02',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (411,14,444,4,1,'XL_20111115_01','XL_20111115_COU_01',2,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (412,14,445,5,1,'XL_20111115_01','XL_20111115_COU_02',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (413,13,444,4,1,'XL_20111014_03','XL_20111014_COU_03',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (418,19,610,66,1,'XL_20141015_01','XL_20141015_01_COU_01',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (419,20,612,1077,1,'XL_20141015_02','XL_20141015_02_COU_02',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (420,21,622,1281,1,'XL_20141017_01','QE_20150519_ADH_04',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (421,21,622,1293,1,'XL_20141017_01','VL_20150519_ADH_32',2,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (422,22,627,85,1,'XL_20141022_01','VL_20150519_ADH_33',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (423,23,628,86,1,'XL_20141022_02','VL_20150514_SMI_06',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (424,24,629,87,1,'XL_20141022_03','XL_20141022_03_COU_01',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comments)
VALUES (425,25,630,88,1,'XL_20141022_04','XL_20141022_04_COU_01',1,0,NULL);
INSERT INTO acquisition_to_mascotfile (id,acquisitionId,mascotFileId,visible,comments)
VALUES (1,409,2,1,'complete report');
INSERT INTO acquisition_to_mascotfile (id,acquisitionId,mascotFileId,visible,comments)
VALUES (2,410,3,1,NULL);
INSERT INTO acquisition_to_mascotfile (id,acquisitionId,mascotFileId,visible,comments)
VALUES (3,410,4,0,NULL);
INSERT INTO acquisition_to_mascotfile (id,acquisitionId,mascotFileId,visible,comments)
VALUES (4,412,5,0,NULL);
*/
