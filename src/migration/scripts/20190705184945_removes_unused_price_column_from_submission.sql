-- // removes unused price column from submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
DROP COLUMN price,
DROP COLUMN additionalprice;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE submission
ADD COLUMN price double DEFAULT NULL,
ADD COLUMN additionalprice double DEFAULT NULL;
