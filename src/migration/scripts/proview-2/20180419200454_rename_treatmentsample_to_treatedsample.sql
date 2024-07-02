-- // rename treatmentsample to treatedsample
-- Migration SQL that makes the change goes here.

ALTER TABLE treatmentsample
DROP FOREIGN KEY treatmentsampleContainer_ibfk,
DROP FOREIGN KEY treatmentsampleDestinationContainer_ibfk,
DROP FOREIGN KEY treatmentsampleSample_ibfk,
DROP FOREIGN KEY treatmentsampleTreatment_ibfk;
RENAME TABLE treatmentsample TO treatedsample;
ALTER TABLE treatedsample
ADD CONSTRAINT treatedsampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleDestinationContainer_ibfk FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatedsampleTreatment_ibfk FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE;


-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE treatedsample
DROP FOREIGN KEY treatedsampleContainer_ibfk,
DROP FOREIGN KEY treatedsampleDestinationContainer_ibfk,
DROP FOREIGN KEY treatedsampleSample_ibfk,
DROP FOREIGN KEY treatedsampleTreatment_ibfk;
RENAME TABLE treatedsample TO treatmentsample;
ALTER TABLE treatmentsample
ADD CONSTRAINT treatmentsampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatmentsampleDestinationContainer_ibfk FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatmentsampleSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
ADD CONSTRAINT treatmentsampleTreatment_ibfk FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE;
