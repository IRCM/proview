-- // use default JPA column names for MsAnalysis
-- Migration SQL that makes the change goes here.

ALTER TABLE msanalysis
CHANGE COLUMN massDetectionInstrument massdetectioninstrument varchar(100) NOT NULL,
CHANGE COLUMN insertTime inserttime datetime NOT NULL,
CHANGE COLUMN deletionType deletiontype varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionExplanation deletionexplanation text;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE msanalysis
CHANGE COLUMN massdetectioninstrument massDetectionInstrument varchar(100) NOT NULL,
CHANGE COLUMN inserttime insertTime datetime NOT NULL,
CHANGE COLUMN deletiontype deletionType varchar(50) DEFAULT NULL,
CHANGE COLUMN deletionexplanation deletionExplanation text;
