--
-- Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

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
ADD CONSTRAINT standard_sample_ibfk FOREIGN KEY (standards_id) REFERENCES sample(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
DROP COLUMN standards;
