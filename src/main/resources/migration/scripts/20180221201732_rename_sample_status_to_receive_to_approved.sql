-- // rename sample status to_receive to approved
-- Migration SQL that makes the change goes here.

UPDATE sample
SET status = 'APPROVED'
WHERE status = 'TO_RECEIVE';


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE sample
SET status = 'TO_RECEIVE'
WHERE status = 'APPROVED';
