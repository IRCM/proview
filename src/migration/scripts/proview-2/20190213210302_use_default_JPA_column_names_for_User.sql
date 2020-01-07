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

-- // use default JPA column names for User
-- Migration SQL that makes the change goes here.

ALTER TABLE user
DROP FOREIGN KEY userAddress_ibfk;
ALTER TABLE user
CHANGE COLUMN password hashedpassword varchar(255) DEFAULT NULL,
CHANGE COLUMN passwordVersion passwordversion int(10) NOT NULL,
CHANGE COLUMN signAttempts signattempts int(10) DEFAULT NULL,
CHANGE COLUMN lastSignAttempt lastsignattempt datetime DEFAULT NULL,
CHANGE COLUMN addressId address_id bigint(20) DEFAULT NULL,
CHANGE COLUMN registerTime registertime datetime NOT NULL;
ALTER TABLE user
ADD CONSTRAINT user_address_ibfk FOREIGN KEY (address_id) REFERENCES address (id) ON UPDATE CASCADE;
ALTER TABLE laboratoryuser
DROP FOREIGN KEY laboratoryuserLaboratory_ibfk,
DROP FOREIGN KEY laboratoryuserUser_ibfk;
ALTER TABLE laboratoryuser
CHANGE COLUMN userId user_id bigint(20) NOT NULL,
CHANGE COLUMN laboratoryId laboratory_id bigint(20) NOT NULL;
ALTER TABLE laboratoryuser
ADD CONSTRAINT laboratoryuser_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratoryuser_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE phonenumber
DROP FOREIGN KEY phonenumberUser_ibfk;
ALTER TABLE phonenumber
CHANGE COLUMN userId phonenumbers_id bigint(20) DEFAULT NULL;
ALTER TABLE phonenumber
ADD CONSTRAINT phonenumber_user_ibfk FOREIGN KEY (phonenumbers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE phonenumber
DROP FOREIGN KEY phonenumber_user_ibfk;
ALTER TABLE phonenumber
CHANGE COLUMN phonenumbers_id userId bigint(20) DEFAULT NULL;
ALTER TABLE phonenumber
ADD CONSTRAINT phonenumberUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE laboratoryuser
DROP FOREIGN KEY laboratoryuser_laboratory_ibfk,
DROP FOREIGN KEY laboratoryuser_user_ibfk;
ALTER TABLE laboratoryuser
CHANGE COLUMN user_id userId bigint(20) NOT NULL,
CHANGE COLUMN laboratory_id laboratoryId bigint(20) NOT NULL;
ALTER TABLE laboratoryuser
ADD CONSTRAINT laboratoryuserUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratoryuserLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE user
DROP FOREIGN KEY user_address_ibfk;
ALTER TABLE user
CHANGE COLUMN hashedpassword password varchar(255) DEFAULT NULL,
CHANGE COLUMN passwordVersion passwordversion int(10) NOT NULL,
CHANGE COLUMN signattempts signAttempts int(10) DEFAULT NULL,
CHANGE COLUMN lastsignattempt lastSignAttempt datetime DEFAULT NULL,
CHANGE COLUMN address_id addressId bigint(20) DEFAULT NULL,
CHANGE COLUMN registertime registerTime datetime NOT NULL;
ALTER TABLE user
ADD CONSTRAINT userAddress_ibfk FOREIGN KEY (addressId) REFERENCES address (id) ON UPDATE CASCADE;
