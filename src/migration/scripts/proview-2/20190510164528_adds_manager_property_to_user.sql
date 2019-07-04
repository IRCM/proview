-- // adds manager property to user
-- Migration SQL that makes the change goes here.

ALTER TABLE user
ADD COLUMN manager TINYINT DEFAULT '0' AFTER admin;
UPDATE user
JOIN laboratorymanager ON user.id = laboratorymanager.managers_id
SET user.manager = 1;
DROP TABLE laboratorymanager;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE IF NOT EXISTS laboratorymanager (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  managers_id bigint(20) NOT NULL,
  laboratory_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (managers_id,laboratory_id),
  CONSTRAINT laboratorymanager_managers_ibfk FOREIGN KEY (managers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratorymanager_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
INSERT INTO laboratorymanager (managers_id,laboratory_id)
SELECT user.id, user.laboratory_id
FROM user
WHERE user.manager = 1;
ALTER TABLE user
DROP COLUMN manager;
