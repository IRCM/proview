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

INSERT INTO laboratory (id,name,director)
VALUES ('1', 'Admin', 'Robot');
INSERT INTO laboratory (id,name,director)
VALUES ('2', 'Translational Proteomics', 'Benoit Coulombe');
INSERT INTO laboratory (id,name,director)
VALUES ('4', 'Biochemistry of Epigenetic Inheritance', 'Robert Williams');
INSERT INTO laboratory (id,name,director)
VALUES ('5', 'Génétique moléculaire et développement', 'Marie Trudel');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('1', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('2', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('3', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('4', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('5', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('6', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('7', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('8', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('9', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('10', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('11', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('12', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('13', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO address (id,line,town,state,country,postalcode)
VALUES ('14', '110, avenue des Pins Ouest', 'Montréal', 'Québec', 'Canada', 'H2W 1R7');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('1', 'proview@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 0, null, 'Robot', null, null, '1', '1', '1', '2008-08-10 12:34:22', '1');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('2', 'christian.poitras@ircm.qc.ca', 'b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7', 'd04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d', '1', 2, '2019-05-11 13:43:51', 'Christian Poitras', 'fr_CA', 1, '1', '1', '1', '2008-08-11 13:43:51', '1');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('3', 'benoit.coulombe@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Benoit Coulombe', 'fr_CA', 2, '1', '0', '1', '2009-10-02 10:56:19', '2');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('4', 'liam.li@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Liam Li', 'en_CA', 3, '1', '1', '0', '2010-01-25 13:20:24', '1');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('5', 'jackson.smith@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Jackson Smith', 'en_CA', 4, '1', '1', '0', '2010-01-25 15:45:24', '1');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('10', 'christopher.anderson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Christopher Anderson', 'en_US', 7, '1', '0', '0', '2011-11-11 09:45:26', '2');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('11', 'robert.stlouis@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Robert St-Louis', 'fr_CA', 5, '0', '1', '0', '2010-01-25 15:48:24', '1');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('12', 'james.johnson@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 3, '2013-11-09 15:48:24', 'James R. Johnson', 'en_US', 9, '0', '0', '0', '2011-07-07 15:48:24', '2');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('19', 'robert.williams@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Robert Williams', 'en_US', 10, '0', '0', '1', '2011-07-07 15:48:24', '4');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('24', 'nicole.francis@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Nicole J. Francis', 'en_US', 11, '0', '0', '1', '2011-07-07 15:48:24', '4');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('25', 'marie.trudel@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Marie Trudel', 'en_US', 12, '0', '0', '1', '2011-07-07 15:48:24', '5');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('26', 'patricia.jones@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Patricia Jones', 'en_US', 13, '1', '0', '0', '2011-07-07 15:48:24', '5');
INSERT INTO user (id,email,hashedpassword,salt,passwordversion,signattempts,lastsignattempt,name,locale,address_id,active,admin,manager,registertime,laboratory_id)
VALUES ('27', 'lucas.martin@ircm.qc.ca', 'da78f3a74658706440f6001b4600d4894d8eea572be0d070f830ca6d716ad55d', '4ae8470fc73a83f369fed012e583b8cb60388919253ea84154610519489a7ba8ab57cde3fc86f04efd02b89175bea7436a8a6a41f5fc6bac5ae6b0f3cf12a535', '1', 0, null, 'Lucas Martin', 'en_US', 14, '1', '0', '1', '2011-07-07 15:48:24', '2');
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('1', '2', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('2', '3', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('3', '4', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('4', '5', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('7', '10', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('8', '11', 'WORK', '514-555-5555', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('9', '12', 'WORK', '514-555-5556', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('10', '19', 'WORK', '514-555-5558', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('11', '24', 'WORK', '514-555-5558', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('12', '25', 'WORK', '514-555-5559', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('13', '26', 'WORK', '514-555-5559', null);
INSERT INTO phonenumber (id,phonenumbers_id,type,number,extension)
VALUES ('14', '27', 'WORK', '514-555-5559', null);
INSERT INTO forgotpassword (id,user_id,requestmoment,confirmnumber,used)
VALUES (7,10,'2014-09-03 11:39:47',803369922,0);
INSERT INTO forgotpassword (id,user_id,requestmoment,confirmnumber,used)
VALUES (8,3,'2013-12-03 11:39:47',-1742054942,1);
INSERT INTO forgotpassword (id,user_id,requestmoment,confirmnumber,used)
VALUES (9,10,CURRENT_TIMESTAMP,174407008,0);
INSERT INTO forgotpassword (id,user_id,requestmoment,confirmnumber,used)
VALUES (10,10,CURRENT_TIMESTAMP,460559412,1);
INSERT INTO preference (id,referer,name)
VALUES (1,'ca.qc.ircm.proview.user.UserPreferenceServiceTest', 'preference_1');
INSERT INTO preference (id,referer,name)
VALUES (2,'ca.qc.ircm.proview.user.UserPreferenceServiceTest', 'preference_2');
INSERT INTO userpreference (id,preference_id,user_id,value)
VALUES (1,1,2,FILE_READ('${project.build.testOutputDirectory}/preference1.ser'));
INSERT INTO userpreference (id,preference_id,user_id,value)
VALUES (2,2,2,FILE_READ('${project.build.testOutputDirectory}/preference2.ser'));
INSERT INTO userpreference (id,preference_id,user_id,value)
VALUES (3,1,3,FILE_READ('${project.build.testOutputDirectory}/preference1.ser'));
INSERT INTO userpreference (id,preference_id,user_id,value)
VALUES (4,2,3,FILE_READ('${project.build.testOutputDirectory}/preference2.ser'));
