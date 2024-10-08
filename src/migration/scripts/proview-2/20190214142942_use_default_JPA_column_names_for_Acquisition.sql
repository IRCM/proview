-- // use default JPA column names for Acquisition
-- Migration SQL that makes the change goes here.

ALTER TABLE acquisition
DROP FOREIGN KEY acquisitionMsAnalysis_ibfk,
DROP FOREIGN KEY acquisitionSample_ibfk,
DROP FOREIGN KEY acquisitionContainer_ibfk;
ALTER TABLE acquisition
CHANGE COLUMN msAnalysisId msanalysis_id bigint(20) DEFAULT NULL,
CHANGE COLUMN sampleId sample_id bigint(20) NOT NULL,
CHANGE COLUMN containerId container_id bigint(20) DEFAULT NULL,
CHANGE COLUMN numberOfAcquisition numberofacquisition int(11) DEFAULT NULL,
CHANGE COLUMN sampleListName samplelistname varchar(255) DEFAULT NULL,
CHANGE COLUMN acquisitionFile acquisitionfile varchar(255) DEFAULT NULL,
CHANGE COLUMN listIndex listindex int(11) DEFAULT NULL;
ALTER TABLE acquisition
ADD CONSTRAINT acquisition_msanalysis_ibfk FOREIGN KEY (msanalysis_id) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT acquisition_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT acquisition_container_ibfk FOREIGN KEY (container_id) REFERENCES samplecontainer (id) ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE acquisition
DROP FOREIGN KEY acquisition_msanalysis_ibfk,
DROP FOREIGN KEY acquisition_sample_ibfk,
DROP FOREIGN KEY acquisition_container_ibfk;
ALTER TABLE acquisition
CHANGE COLUMN msanalysis_id msanalysisId bigint(20) DEFAULT NULL,
CHANGE COLUMN sample_id sampleId bigint(20) NOT NULL,
CHANGE COLUMN container_id containerId bigint(20) DEFAULT NULL,
CHANGE COLUMN numberofacquisition numberOfAcquisition int(11) DEFAULT NULL,
CHANGE COLUMN samplelistname sampleListName varchar(255) DEFAULT NULL,
CHANGE COLUMN acquisitionfile acquisitionFile varchar(255) DEFAULT NULL,
CHANGE COLUMN listindex listIndex int(11) DEFAULT NULL;
ALTER TABLE acquisition
ADD CONSTRAINT acquisitionMsAnalysis_ibfk FOREIGN KEY (msanalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT acquisitionSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT acquisitionContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE;
