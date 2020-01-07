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

-- // rename submission properties with long names
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
CHANGE massDetectionInstrument instrument varchar(100) DEFAULT NULL,
CHANGE proteolyticDigestionMethod digestion varchar(50) DEFAULT NULL,
CHANGE usedProteolyticDigestionMethod usedDigestion varchar(100) DEFAULT NULL,
CHANGE otherProteolyticDigestionMethod otherDigestion varchar(100) DEFAULT NULL,
CHANGE proteinIdentification identification varchar(100) DEFAULT NULL,
CHANGE proteinIdentificationLink identificationLink varchar(100) DEFAULT NULL;
UPDATE activityupdate
SET actioncolumn = 'instrument'
WHERE actioncolumn = 'massDetectionInstrument';
UPDATE activityupdate
SET actioncolumn = 'digestion'
WHERE actioncolumn = 'proteolyticDigestionMethod';
UPDATE activityupdate
SET actioncolumn = 'usedDigestion'
WHERE actioncolumn = 'usedProteolyticDigestionMethod';
UPDATE activityupdate
SET actioncolumn = 'otherDigestion'
WHERE actioncolumn = 'otherProteolyticDigestionMethod';
UPDATE activityupdate
SET actioncolumn = 'identification'
WHERE actioncolumn = 'proteinIdentification';
UPDATE activityupdate
SET actioncolumn = 'identificationLink'
WHERE actioncolumn = 'proteinIdentificationLink';

-- //@UNDO
-- SQL to undo the change goes here.

UPDATE activityupdate
SET actioncolumn = 'proteinIdentificationLink'
WHERE actioncolumn = 'identificationLink';
UPDATE activityupdate
SET actioncolumn = 'proteinIdentification'
WHERE actioncolumn = 'identification';
UPDATE activityupdate
SET actioncolumn = 'otherProteolyticDigestionMethod'
WHERE actioncolumn = 'otherDigestion';
UPDATE activityupdate
SET actioncolumn = 'usedProteolyticDigestionMethod'
WHERE actioncolumn = 'usedDigestion';
UPDATE activityupdate
SET actioncolumn = 'proteolyticDigestionMethod'
WHERE actioncolumn = 'digestion';
UPDATE activityupdate
SET actioncolumn = 'massDetectionInstrument'
WHERE actioncolumn = 'instrument';
ALTER TABLE submission
CHANGE instrument massDetectionInstrument varchar(100) DEFAULT NULL,
CHANGE digestion proteolyticDigestionMethod varchar(50) DEFAULT NULL,
CHANGE usedDigestion usedProteolyticDigestionMethod varchar(100) DEFAULT NULL,
CHANGE otherDigestion otherProteolyticDigestionMethod varchar(100) DEFAULT NULL,
CHANGE identification proteinIdentification varchar(100) DEFAULT NULL,
CHANGE identificationLink proteinIdentificationLink varchar(100) DEFAULT NULL;
