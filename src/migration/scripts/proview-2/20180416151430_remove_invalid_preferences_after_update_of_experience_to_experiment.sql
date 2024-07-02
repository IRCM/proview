-- // remove invalid preferences after update of experience to experiment
-- Migration SQL that makes the change goes here.

DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'columnOrder';


-- //@UNDO
-- SQL to undo the change goes here.

-- Cannot undo.
