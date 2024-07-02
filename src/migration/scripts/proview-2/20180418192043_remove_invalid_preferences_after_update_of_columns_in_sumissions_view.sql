-- // remove invalid preferences after update of columns in sumissions view
-- Migration SQL that makes the change goes here.

DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'columnOrder';
DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'submission.goal';


-- //@UNDO
-- SQL to undo the change goes here.

-- Cannot undo.
