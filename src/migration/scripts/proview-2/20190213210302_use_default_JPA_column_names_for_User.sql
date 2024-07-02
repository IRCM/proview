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
