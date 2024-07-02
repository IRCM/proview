-- // use default JPA column names for Address
-- Migration SQL that makes the change goes here.

ALTER TABLE address
CHANGE COLUMN postalCode postalcode varchar(50) NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE address
CHANGE COLUMN postalcode postalCode varchar(50) NOT NULL;
