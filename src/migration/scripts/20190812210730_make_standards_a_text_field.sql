-- // make standards a text field
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN standards TEXT AFTER contaminants;

-- @DELIMITER $
CREATE FUNCTION standards(submissionid BIGINT) RETURNS TEXT
BEGIN
  DECLARE finished BOOLEAN DEFAULT false;
  DECLARE name varchar(255) DEFAULT "";
  DECLARE quantity varchar(255) DEFAULT "";
  DECLARE comment TEXT DEFAULT "";
  DECLARE standards TEXT DEFAULT "";
  DECLARE standard_info CURSOR FOR
  SELECT DISTINCT standard.name, standard.quantity, standard.comment
  FROM standard
  JOIN sample ON standard.standards_id = sample.id
  WHERE sample.submission_id = submissionid;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
  OPEN standard_info;
  standard_loop: LOOP
    FETCH standard_info INTO name, quantity, comment;
    IF finished THEN
      LEAVE standard_loop;
    END IF;
    SELECT CONCAT(standards, ", ", name, " - ", quantity, " (", comment, ")") INTO standards;
  END LOOP standard_loop;
  CLOSE standard_info;
  
  IF LENGTH(standards) > 0 THEN
    SELECT SUBSTRING(standards, 3) INTO standards;
  END IF;
  
  RETURN standards;
END $
-- @DELIMITER ;

UPDATE submission
SET standards = standards(submission.id);

DROP FUNCTION standards;

ALTER TABLE standard
DROP FOREIGN KEY standard_sample_ibfk;
RENAME TABLE standard TO old_standard;

-- //@UNDO
-- SQL to undo the change goes here.

RENAME TABLE old_standard TO standard;
ALTER TABLE standard
ADD CONSTRAINT standard_sample_ibfk FOREIGN KEY standards_id REFERENCES sample(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
DROP COLUMN standards;
