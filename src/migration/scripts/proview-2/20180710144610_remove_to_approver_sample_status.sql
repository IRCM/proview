-- // remove to approver sample status
-- Migration SQL that makes the change goes here.

UPDATE sample
SET status = status - 1
WHERE status > 0;


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE sample
SET status = status + 1;
