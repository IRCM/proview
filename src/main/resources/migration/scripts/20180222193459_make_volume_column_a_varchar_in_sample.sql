-- // make volume column a varchar in sample
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
MODIFY COLUMN volume VARCHAR(100) DEFAULT NULL;
UPDATE sample
SET volume = CONCAT(volume, ' μl');

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE sample
SET volume = NULL
WHERE volume NOT REGEXP '^[0-9]*\.?[0-9]* μl$';
UPDATE sample
SET volume = SUBSTRING(volume, 1, LENGTH(volume)-LENGTH(' μl'))
WHERE volume REGEXP '^[0-9]*\.?[0-9]* μl$';
ALTER TABLE sample
MODIFY COLUMN volume DOUBLE DEFAULT NULL;
