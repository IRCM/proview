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

-- // add order column to treatmentsample column
-- Migration SQL that makes the change goes here.

ALTER TABLE treatmentsample
ADD COLUMN listIndex int(10) DEFAULT NULL AFTER containerId;
-- @DELIMITER $
CREATE PROCEDURE update_treatmentsample_indexes()
BEGIN
  DECLARE treatmentId, treatmentsampleId BIGINT;
  DECLARE listIndex INT DEFAULT 0;
  DECLARE treatment_done, treatmentsample_done BOOLEAN DEFAULT false;
  DECLARE treatmentIds CURSOR FOR SELECT id FROM treatment;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET treatment_done = TRUE;
  
  OPEN treatmentIds;
  treatment_loop: LOOP
    FETCH FROM treatmentIds INTO treatmentId;
    IF treatment_done THEN
      CLOSE treatmentIds;
      LEAVE treatment_loop;
    END IF;
    
    BLOCK2: BEGIN
      DECLARE treatmentsampleIds CURSOR FOR SELECT id FROM treatmentsample WHERE treatmentsample.treatmentId = treatmentId ORDER BY id;
      DECLARE CONTINUE HANDLER FOR NOT FOUND SET treatmentsample_done = TRUE;
      OPEN treatmentsampleIds;
      treatmentsample_loop: LOOP
        FETCH FROM treatmentsampleIds INTO treatmentsampleId;
        IF treatmentsample_done THEN
          SET treatmentsample_done = false;
          SET listIndex = 0;
          CLOSE treatmentsampleIds;
          LEAVE treatmentsample_loop;
        END IF;
        
        UPDATE treatmentsample
        SET treatmentsample.listIndex = listIndex
        WHERE id = treatmentsampleId;
        SELECT listIndex + 1 INTO listIndex;
      END LOOP treatmentsample_loop;
    END BLOCK2;
  END LOOP treatment_loop;
END $
-- @DELIMITER ;
CALL update_treatmentsample_indexes();
DROP PROCEDURE update_treatmentsample_indexes;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE treatmentsample
DROP COLUMN listIndex;
