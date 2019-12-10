-- // replace confirm number by string in forgot password
-- Migration SQL that makes the change goes here.

ALTER TABLE forgotpassword
MODIFY confirmnumber varchar(100) NOT NULL;


-- //@UNDO
-- SQL to undo the change goes here.

UPDATE forgotpassword
SET confirmnumber = 0,
  used = 1
WHERE confirmnumber NOT REGEXP '^-?[0-9]+$';
ALTER TABLE forgotpassword
MODIFY confirmnumber int(11) NOT NULL;
