-- // remove invalid preferences after changes in submissions view
-- Migration SQL that makes the change goes here.

DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'columnOrder';
DELETE FROM preference
WHERE referer = 'ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter'
AND name = 'treatments';

-- //@UNDO
-- SQL to undo the change goes here.

