-- // convert submission's solvents to list of enums
-- Migration SQL that makes the change goes here.

UPDATE activityupdate
JOIN solvent ON activityupdate.recordId = solvent.id
SET tableName = 'submission',
  recordId = solvent.submissionId,
  actionColumn = 'solvent',
  newValue = solvent.solvent
WHERE tableName = 'solvent'
AND actionType = 'INSERT';
UPDATE activityupdate
JOIN solvent ON activityupdate.recordId = solvent.id
SET tableName = 'submission',
  recordId = solvent.submissionId,
  actionColumn = 'solvent',
  oldValue = solvent.solvent
WHERE tableName = 'solvent'
AND actionType = 'DELETE';
DELETE FROM solvent
WHERE deleted = 1;


-- //@UNDO
-- SQL to undo the change goes here.

-- No need to undo changes.
