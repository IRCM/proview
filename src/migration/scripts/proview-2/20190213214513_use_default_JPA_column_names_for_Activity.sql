-- // use default JPA column names for Activity
-- Migration SQL that makes the change goes here.

ALTER TABLE activity
CHANGE COLUMN userId user_id bigint(20) NOT NULL,
CHANGE COLUMN tableName tablename varchar(50) NOT NULL,
CHANGE COLUMN recordId recordid bigint(20) NOT NULL,
CHANGE COLUMN actionType actiontype varchar(50) NOT NULL,
CHANGE COLUMN time timestamp datetime NOT NULL;
ALTER TABLE activityupdate
DROP FOREIGN KEY activityupdateActivity_ibfk;
ALTER TABLE activityupdate
CHANGE COLUMN activityId updates_id bigint(20) DEFAULT NULL;
ALTER TABLE activityupdate
ADD CONSTRAINT activityupdate_activity_ibfk FOREIGN KEY (updates_id) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE activityupdate
DROP FOREIGN KEY activityupdate_activity_ibfk;
ALTER TABLE activityupdate
CHANGE COLUMN updates_id activityId bigint(20) DEFAULT NULL;
ALTER TABLE activityupdate
ADD CONSTRAINT activityupdateActivity_ibfk FOREIGN KEY (activityId) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE activity
CHANGE COLUMN user_id userId bigint(20) NOT NULL,
CHANGE COLUMN tablename tableName varchar(50) NOT NULL,
CHANGE COLUMN recordid recordId bigint(20) NOT NULL,
CHANGE COLUMN actiontype actionType varchar(50) NOT NULL,
CHANGE COLUMN timestamp time datetime NOT NULL;
