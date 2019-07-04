-- // use default JPA column names for TreatedSample
-- Migration SQL that makes the change goes here.

ALTER TABLE treatedsample
DROP FOREIGN KEY treatedsampleTreatment_ibfk,
DROP FOREIGN KEY treatedsampleSample_ibfk,
DROP FOREIGN KEY treatedsampleContainer_ibfk,
DROP FOREIGN KEY treatedsampleDestinationContainer_ibfk;
ALTER TABLE treatedsample
CHANGE COLUMN treatmentId treatment_id bigint(20) DEFAULT NULL,
CHANGE COLUMN sampleId sample_id bigint(20) NOT NULL,
CHANGE COLUMN containerId container_id bigint(20) DEFAULT NULL,
CHANGE COLUMN listIndex listindex int(10) DEFAULT NULL,
CHANGE COLUMN destinationContainerId destinationcontainer_id bigint(20) DEFAULT NULL,
CHANGE COLUMN sourceVolume sourcevolume double DEFAULT NULL,
CHANGE COLUMN solventVolume solventvolume double DEFAULT NULL,
CHANGE COLUMN piInterval piinterval varchar(50) DEFAULT NULL;
ALTER TABLE treatedsample
ADD CONSTRAINT treatedsample_treatment_ibfk FOREIGN KEY (treatment_id) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT treatedsample_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsample_container_ibfk FOREIGN KEY (container_id) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsample_destination_ibfk FOREIGN KEY (destinationcontainer_id) REFERENCES samplecontainer (id) ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE treatedsample
DROP FOREIGN KEY treatedsample_treatment_ibfk,
DROP FOREIGN KEY treatedsample_sample_ibfk,
DROP FOREIGN KEY treatedsample_container_ibfk,
DROP FOREIGN KEY treatedsample_destination_ibfk;
ALTER TABLE treatedsample
CHANGE COLUMN treatment_id treatmentId bigint(20) DEFAULT NULL,
CHANGE COLUMN sample_id sampleId bigint(20) NOT NULL,
CHANGE COLUMN container_id containerId bigint(20) DEFAULT NULL,
CHANGE COLUMN listindex listIndex int(10) DEFAULT NULL,
CHANGE COLUMN destinationcontainer_id destinationContainerId bigint(20) DEFAULT NULL,
CHANGE COLUMN sourcevolume sourceVolume double DEFAULT NULL,
CHANGE COLUMN solventvolume solventVolume double DEFAULT NULL,
CHANGE COLUMN piinterval piInterval varchar(50) DEFAULT NULL;
ALTER TABLE treatedsample
ADD CONSTRAINT treatedsampleTreatment_ibfk FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleDestinationContainer_ibfk FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE;
