-- // remove data analysis sample status
-- Migration SQL that makes the change goes here.

UPDATE sample
SET status = status - 1
WHERE status >= 6;

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE sample
SET status = status + 1
WHERE status >= 5;
