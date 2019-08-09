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
