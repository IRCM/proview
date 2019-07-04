-- // use default JPA column names for Laboratory
-- Migration SQL that makes the change goes here.

ALTER TABLE laboratorymanager
DROP FOREIGN KEY laboratorymanagerLaboratory_ibfk,
DROP FOREIGN KEY laboratorymanagerUser_ibfk;
ALTER TABLE laboratorymanager
CHANGE COLUMN userId managers_id BIGINT NOT NULL,
CHANGE COLUMN laboratoryId laboratory_id BIGINT NOT NULL;
ALTER TABLE laboratorymanager
ADD CONSTRAINT laboratorymanager_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory(id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratorymanager_managers_ibfk FOREIGN KEY (managers_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE laboratorymanager
DROP FOREIGN KEY laboratorymanager_laboratory_ibfk,
DROP FOREIGN KEY laboratorymanager_managers_ibfk;
ALTER TABLE laboratorymanager
CHANGE COLUMN managers_id userId BIGINT NOT NULL,
CHANGE COLUMN laboratory_id laboratoryId BIGINT NOT NULL;
ALTER TABLE laboratorymanager
ADD CONSTRAINT laboratorymanagerLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory(id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT laboratorymanagerUser_ibfk FOREIGN KEY (userId) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE;
