-- // use default JPA column names for ForgotPassword
-- Migration SQL that makes the change goes here.

ALTER TABLE forgotpassword
DROP FOREIGN KEY forgotpasswordUser_ibfk;
ALTER TABLE forgotpassword
CHANGE COLUMN userId user_id bigint(20) NOT NULL,
CHANGE COLUMN requestMoment requestmoment datetime NOT NULL,
CHANGE COLUMN confirmNumber confirmnumber int(11) NOT NULL;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpassword_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE forgotpassword
DROP FOREIGN KEY forgotpassword_user_ibfk;
ALTER TABLE forgotpassword
CHANGE COLUMN user_id userId bigint(20) NOT NULL,
CHANGE COLUMN requestmoment requestMoment datetime NOT NULL,
CHANGE COLUMN confirmnumber confirmNumber int(11) NOT NULL;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpasswordUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
