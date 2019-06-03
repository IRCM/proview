-- // fixes samplecontainer columns for H2
-- Migration SQL that makes the change goes here.

ALTER TABLE samplecontainer
CHANGE COLUMN row wellrow int(11) DEFAULT NULL,
CHANGE COLUMN col wellcolumn int(11) DEFAULT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE samplecontainer
CHANGE COLUMN wellrow row int(11) DEFAULT NULL,
CHANGE COLUMN wellcolumn col int(11) DEFAULT NULL;
