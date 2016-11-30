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
INSERT INTO forgotpassword (id,userId,requestMoment,confirmNumber,used)
VALUES (7,10,'2014-09-03 11:39:47',803369922,0);
INSERT INTO forgotpassword (id,userId,requestMoment,confirmNumber,used)
VALUES (8,3,'2013-12-03 11:39:47',-1742054942,1);
INSERT INTO forgotpassword (id,userId,requestMoment,confirmNumber,used)
VALUES (9,10,CURRENT_TIMESTAMP,174407008,0);
INSERT INTO forgotpassword (id,userId,requestMoment,confirmNumber,used)
VALUES (10,10,CURRENT_TIMESTAMP,460559412,1);
