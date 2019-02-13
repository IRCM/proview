-- // use default JPA column names for UserPreference
-- Migration SQL that makes the change goes here.

ALTER TABLE userpreference
DROP FOREIGN KEY userpreferencePreference_ibfk,
DROP FOREIGN KEY userpreferenceUser_ibfk;
ALTER TABLE userpreference
CHANGE COLUMN preferenceId preference_id bigint(20) NOT NULL,
CHANGE COLUMN userId user_id bigint(20) NOT NULL;
ALTER TABLE userpreference
ADD CONSTRAINT userpreference_preference_ibfk FOREIGN KEY (preference_id) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT userpreference_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE userpreference
DROP FOREIGN KEY userpreference_preference_ibfk,
DROP FOREIGN KEY userpreference_user_ibfk;
ALTER TABLE userpreference
CHANGE COLUMN preference_id preferenceId bigint(20) NOT NULL,
CHANGE COLUMN user_id userId bigint(20) NOT NULL;
ALTER TABLE userpreference
ADD CONSTRAINT userpreferencePreference_ibfk FOREIGN KEY (preferenceId) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT userpreferenceUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;
