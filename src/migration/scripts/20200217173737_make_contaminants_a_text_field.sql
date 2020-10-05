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

-- // make contaminants a text field
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
ADD COLUMN contaminants TEXT AFTER quantificationcomment;

-- @DELIMITER $
CREATE FUNCTION contaminants(submissionid BIGINT) RETURNS TEXT
BEGIN
  DECLARE finished BOOLEAN DEFAULT false;
  DECLARE name varchar(255) DEFAULT "";
  DECLARE quantity varchar(255) DEFAULT "";
  DECLARE comment TEXT DEFAULT "";
  DECLARE contaminants TEXT DEFAULT "";
  DECLARE contaminant_info CURSOR FOR
  SELECT DISTINCT contaminant.name, contaminant.quantity, contaminant.comment
  FROM contaminant
  JOIN sample ON contaminant.contaminants_id = sample.id
  WHERE sample.submission_id = submissionid;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
  OPEN contaminant_info;
  contaminant_loop: LOOP
    FETCH contaminant_info INTO name, quantity, comment;
    IF finished THEN
      LEAVE contaminant_loop;
    END IF;
    SELECT CONCAT(contaminants, ", ", name, " - ", quantity, " (", comment, ")") INTO contaminants;
  END LOOP contaminant_loop;
  CLOSE contaminant_info;
  
  IF LENGTH(contaminants) > 0 THEN
    SELECT SUBSTRING(contaminants, 3) INTO contaminants;
  END IF;
  
  RETURN contaminants;
END $
-- @DELIMITER ;

UPDATE submission
SET contaminants = contaminants(submission.id);

DROP FUNCTION contaminants;

ALTER TABLE contaminant
DROP FOREIGN KEY contaminant_sample_ibfk;
RENAME TABLE contaminant TO old_contaminant;

-- //@UNDO
-- SQL to undo the change goes here.

RENAME TABLE old_contaminant TO contaminant;
ALTER TABLE contaminant
ADD CONSTRAINT contaminant_sample_ibfk FOREIGN KEY (contaminants_id) REFERENCES sample(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
DROP COLUMN contaminants;
