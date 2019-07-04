-- // use default JPA column names for DataAnalysis
-- Migration SQL that makes the change goes here.

ALTER TABLE dataanalysis
DROP FOREIGN KEY dataanalysisSample_ibfk;
ALTER TABLE dataanalysis
CHANGE COLUMN sampleId sample_id bigint(20) NOT NULL,
CHANGE COLUMN maxWorkTime maxworktime double NOT NULL,
CHANGE COLUMN workTime worktime double DEFAULT NULL,
CHANGE COLUMN analysisType type varchar(50) NOT NULL;
ALTER TABLE dataanalysis
ADD CONSTRAINT dataanalysis_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE dataanalysis
DROP FOREIGN KEY dataanalysis_sample_ibfk;
ALTER TABLE dataanalysis
CHANGE COLUMN sample_id sampleId bigint(20) NOT NULL,
CHANGE COLUMN maxWorktime maxWorkTime double NOT NULL,
CHANGE COLUMN worktime workTime double DEFAULT NULL,
CHANGE COLUMN type analysisType varchar(50) NOT NULL;
ALTER TABLE dataanalysis
ADD CONSTRAINT dataanalysisSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE;
