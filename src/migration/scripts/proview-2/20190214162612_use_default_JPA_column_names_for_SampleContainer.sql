-- // use default JPA column names for SampleContainer
-- Migration SQL that makes the change goes here.

ALTER TABLE samplecontainer
DROP FOREIGN KEY samplecontainerSample_ibfk,
DROP FOREIGN KEY samplecontainerPlate_ibfk;
ALTER TABLE samplecontainer
CHANGE COLUMN plateId plate_id bigint(20) DEFAULT NULL,
CHANGE COLUMN locationColumn col int(11) DEFAULT NULL,
CHANGE COLUMN locationRow row int(11) DEFAULT NULL,
CHANGE COLUMN sampleId sample_id bigint(20) DEFAULT NULL,
CHANGE COLUMN time timestamp datetime NOT NULL;
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainer_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT samplecontainer_plate_ibfk FOREIGN KEY (plate_id) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE samplecontainer
DROP FOREIGN KEY samplecontainer_sample_ibfk,
DROP FOREIGN KEY samplecontainer_plate_ibfk;
ALTER TABLE samplecontainer
CHANGE COLUMN plate_id plateId bigint(20) DEFAULT NULL,
CHANGE COLUMN col locationColumn int(11) DEFAULT NULL,
CHANGE COLUMN row locationRow int(11) DEFAULT NULL,
CHANGE COLUMN sample_id sampleId bigint(20) DEFAULT NULL,
CHANGE COLUMN timestamp time datetime NOT NULL;
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainerSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT samplecontainerPlate_ibfk FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE;
