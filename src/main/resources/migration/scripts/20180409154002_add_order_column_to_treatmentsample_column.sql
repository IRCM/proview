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
