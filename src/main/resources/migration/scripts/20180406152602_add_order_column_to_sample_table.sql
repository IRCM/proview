-- // add order column to sample table
-- Migration SQL that makes the change goes here.

ALTER TABLE sample
ADD COLUMN listIndex int(10) DEFAULT NULL AFTER submissionId;
-- @DELIMITER $
CREATE PROCEDURE update_sample_indexes()
BEGIN
  DECLARE submissionId, sampleId BIGINT;
  DECLARE listIndex INT DEFAULT 0;
  DECLARE submission_done, sample_done BOOLEAN DEFAULT false;
  DECLARE submissionIds CURSOR FOR SELECT id FROM submission;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET submission_done = TRUE;
  
  OPEN submissionIds;
  submission_loop: LOOP
    FETCH FROM submissionIds INTO submissionId;
    IF submission_done THEN
      CLOSE submissionIds;
      LEAVE submission_loop;
    END IF;
    
    BLOCK2: BEGIN
      DECLARE sampleIds CURSOR FOR SELECT id FROM sample WHERE sample.submissionId = submissionId ORDER BY id;
      DECLARE CONTINUE HANDLER FOR NOT FOUND SET sample_done = TRUE;
      OPEN sampleIds;
      sample_loop: LOOP
        FETCH FROM sampleIds INTO sampleId;
        IF sample_done THEN
          SET sample_done = false;
          SET listIndex = 0;
          CLOSE sampleIds;
          LEAVE sample_loop;
        END IF;
        
        UPDATE sample
        SET sample.listIndex = listIndex
        WHERE id = sampleId;
        SELECT listIndex + 1 INTO listIndex;
      END LOOP sample_loop;
    END BLOCK2;
  END LOOP submission_loop;
END $
-- @DELIMITER ;
CALL update_sample_indexes();
DROP PROCEDURE update_sample_indexes;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE sample
DROP COLUMN listIndex;
