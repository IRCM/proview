-- // rename user table to users
-- Migration SQL that makes the change goes here.

ALTER TABLE phonenumber
DROP CONSTRAINT phonenumber_user_ibfk;
ALTER TABLE forgotpassword
DROP CONSTRAINT forgotpassword_user_ibfk;
ALTER TABLE submission
DROP CONSTRAINT submission_user_ibfk;
ALTER TABLE treatment
DROP CONSTRAINT treatment_user_ibfk;
ALTER TABLE userpreference
DROP CONSTRAINT userpreference_user_ibfk;
RENAME TABLE user TO users;
ALTER TABLE phonenumber
ADD CONSTRAINT phonenumber_user_ibfk FOREIGN KEY (phonenumbers_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpassword_user_ibfk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
ADD CONSTRAINT submission_user_ibfk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE treatment
ADD CONSTRAINT treatment_user_ibfk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE userpreference
ADD CONSTRAINT userpreference_user_ibfk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE phonenumber
DROP CONSTRAINT phonenumber_user_ibfk;
ALTER TABLE forgotpassword
DROP CONSTRAINT forgotpassword_user_ibfk;
ALTER TABLE submission
DROP CONSTRAINT submission_user_ibfk;
ALTER TABLE treatment
DROP CONSTRAINT treatment_user_ibfk;
ALTER TABLE userpreference
DROP CONSTRAINT userpreference_user_ibfk;
RENAME TABLE users TO user;
ALTER TABLE phonenumber
ADD CONSTRAINT phonenumber_user_ibfk FOREIGN KEY (phonenumbers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE forgotpassword
ADD CONSTRAINT forgotpassword_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
ADD CONSTRAINT submission_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE treatment
ADD CONSTRAINT treatment_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE userpreference
ADD CONSTRAINT userpreference_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
