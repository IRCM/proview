--
-- Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

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
