-- // use default JPA column names for Plate
-- Migration SQL that makes the change goes here.

ALTER TABLE plate
CHANGE COLUMN columnCount columncount int NOT NULL,
CHANGE COLUMN rowCount rowcount int NOT NULL,
CHANGE COLUMN insertTime inserttime datetime NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE plate
CHANGE COLUMN columncount columnCount int NOT NULL,
CHANGE COLUMN rowcount rowCount int NOT NULL,
CHANGE COLUMN inserttime insertTime datetime NOT NULL;
