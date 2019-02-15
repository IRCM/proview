-- // use default JPA column names for Submission
-- Migration SQL that makes the change goes here.

ALTER TABLE submission
DROP FOREIGN KEY submissionLaboratory_ibfk,
DROP FOREIGN KEY submissionUser_ibfk;
ALTER TABLE submission
CHANGE COLUMN massDetectionInstrument massdetectioninstrument varchar(100) DEFAULT NULL,
CHANGE COLUMN injectionType injectiontype varchar(50) DEFAULT NULL,
CHANGE COLUMN proteolyticDigestionMethod proteolyticdigestionmethod varchar(50) DEFAULT NULL,
CHANGE COLUMN usedProteolyticDigestionMethod usedproteolyticdigestionmethod varchar(100) DEFAULT NULL,
CHANGE COLUMN otherProteolyticDigestionMethod otherproteolyticdigestionmethod varchar(100) DEFAULT NULL,
CHANGE COLUMN proteinIdentification proteinidentification varchar(50) DEFAULT NULL,
CHANGE COLUMN proteinIdentificationLink proteinidentificationlink varchar(255) DEFAULT NULL,
CHANGE COLUMN enrichmentType enrichmenttype varchar(50) DEFAULT NULL,
CHANGE COLUMN otherEnrichmentType otherenrichmenttype varchar(100) DEFAULT NULL,
CHANGE COLUMN lowResolution lowresolution tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN highResolution highresolution tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN exactMsms exactmsms tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN mudPitFraction mudpitfraction varchar(50) DEFAULT NULL,
CHANGE COLUMN proteinContent proteincontent varchar(50) DEFAULT NULL,
CHANGE COLUMN postTranslationModification posttranslationmodification varchar(150) DEFAULT NULL,
CHANGE COLUMN otherColoration othercoloration varchar(100) DEFAULT NULL,
CHANGE COLUMN developmentTime developmenttime varchar(100) DEFAULT NULL,
CHANGE COLUMN weightMarkerQuantity weightmarkerquantity double DEFAULT NULL,
CHANGE COLUMN proteinQuantity proteinquantity varchar(100) DEFAULT NULL,
CHANGE COLUMN monoisotopicMass monoisotopicmass double DEFAULT NULL,
CHANGE COLUMN averageMass averagemass double DEFAULT NULL,
CHANGE COLUMN solutionSolvent solutionsolvent varchar(100) DEFAULT NULL,
CHANGE COLUMN otherSolvent othersolvent varchar(100) DEFAULT NULL,
CHANGE COLUMN lightSensitive lightsensitive tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN storageTemperature storagetemperature varchar(50) DEFAULT NULL,
CHANGE COLUMN quantificationComment quantificationcomment text DEFAULT NULL,
CHANGE COLUMN additionalPrice additionalprice double DEFAULT NULL,
CHANGE COLUMN submissionDate submissiondate datetime NOT NULL,
CHANGE COLUMN sampleDeliveryDate sampledeliverydate date DEFAULT NULL,
CHANGE COLUMN digestionDate digestiondate date DEFAULT NULL,
CHANGE COLUMN analysisDate analysisdate date DEFAULT NULL,
CHANGE COLUMN dataAvailableDate dataavailabledate date DEFAULT NULL,
CHANGE COLUMN laboratoryId laboratory_id bigint(20) NOT NULL,
CHANGE COLUMN userId user_id bigint(20) DEFAULT NULL;
ALTER TABLE submission
ADD CONSTRAINT submission_laboratory_ibfk FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON UPDATE CASCADE,
ADD CONSTRAINT submission_user_ibfk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE submissionfiles
DROP FOREIGN KEY submissionfilesSubmission_ibfk;
ALTER TABLE submissionfiles
CHANGE COLUMN submissionId files_id bigint(20) DEFAULT NULL;
ALTER TABLE submissionfiles
ADD CONSTRAINT submissionfiles_submission_ibfk FOREIGN KEY (files_id) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE solvent
DROP FOREIGN KEY solventSubmission_ibfk;
ALTER TABLE solvent
CHANGE COLUMN submissionId submission_id bigint(20) DEFAULT NULL,
CHANGE COLUMN solvent solvents varchar(150) NOT NULL;
ALTER TABLE solvent
ADD CONSTRAINT solvent_submission_ibfk FOREIGN KEY (submission_id) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- //@UNDO
-- SQL to undo the change goes here.

ALTER TABLE solvent
DROP FOREIGN KEY solvent_submission_ibfk;
ALTER TABLE solvent
CHANGE COLUMN submission_id submissionId bigint(20) DEFAULT NULL,
CHANGE COLUMN solvents solvent varchar(150) NOT NULL;
ALTER TABLE solvent
ADD CONSTRAINT solventSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submissionfiles
DROP FOREIGN KEY submissionfiles_submission_ibfk;
ALTER TABLE submissionfiles
CHANGE COLUMN files_id submissionId bigint(20) DEFAULT NULL;
ALTER TABLE submissionfiles
ADD CONSTRAINT submissionfilesSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE submission
DROP FOREIGN KEY submission_laboratory_ibfk,
DROP FOREIGN KEY submission_user_ibfk;
ALTER TABLE submission
CHANGE COLUMN massdetectioninstrument massDetectionInstrument varchar(100) DEFAULT NULL,
CHANGE COLUMN injectiontype injectionType varchar(50) DEFAULT NULL,
CHANGE COLUMN proteolyticdigestionmethod proteolyticDigestionMethod varchar(50) DEFAULT NULL,
CHANGE COLUMN usedproteolyticdigestionmethod usedProteolyticDigestionMethod varchar(100) DEFAULT NULL,
CHANGE COLUMN otherproteolyticdigestionmethod otherProteolyticDigestionMethod varchar(100) DEFAULT NULL,
CHANGE COLUMN proteinidentification proteinIdentification varchar(50) DEFAULT NULL,
CHANGE COLUMN proteinidentificationlink proteinIdentificationLink varchar(255) DEFAULT NULL,
CHANGE COLUMN enrichmenttype enrichmentType varchar(50) DEFAULT NULL,
CHANGE COLUMN otherenrichmenttype otherEnrichmentType varchar(100) DEFAULT NULL,
CHANGE COLUMN lowresolution lowResolution tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN highresolution highResolution tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN exactmsms exactMsms tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN mudpitfraction mudPitFraction varchar(50) DEFAULT NULL,
CHANGE COLUMN proteincontent proteinContent varchar(50) DEFAULT NULL,
CHANGE COLUMN posttranslationmodification postTranslationModification varchar(150) DEFAULT NULL,
CHANGE COLUMN othercoloration otherColoration varchar(100) DEFAULT NULL,
CHANGE COLUMN developmenttime developmentTime varchar(100) DEFAULT NULL,
CHANGE COLUMN weightmarkerquantity weightMarkerQuantity double DEFAULT NULL,
CHANGE COLUMN proteinquantity proteinQuantity varchar(100) DEFAULT NULL,
CHANGE COLUMN monoisotopicmass monoisotopicMass double DEFAULT NULL,
CHANGE COLUMN averagemass averageMass double DEFAULT NULL,
CHANGE COLUMN solutionsolvent solutionSolvent varchar(100) DEFAULT NULL,
CHANGE COLUMN othersolvent otherSolvent varchar(100) DEFAULT NULL,
CHANGE COLUMN lightsensitive lightSensitive tinyint(1) NOT NULL DEFAULT '0',
CHANGE COLUMN storagetemperature storageTemperature varchar(50) DEFAULT NULL,
CHANGE COLUMN quantificationcomment quantificationComment text DEFAULT NULL,
CHANGE COLUMN additionalprice additionalPrice double DEFAULT NULL,
CHANGE COLUMN submissiondate submissionDate datetime NOT NULL,
CHANGE COLUMN sampledeliverydate sampleDeliveryDate date DEFAULT NULL,
CHANGE COLUMN digestiondate digestionDate date DEFAULT NULL,
CHANGE COLUMN analysisdate analysisDate date DEFAULT NULL,
CHANGE COLUMN dataavailabledate dataAvailableDate date DEFAULT NULL,
CHANGE COLUMN laboratory_id laboratoryId bigint(20) NOT NULL,
CHANGE COLUMN user_id userId bigint(20) DEFAULT NULL;
ALTER TABLE submission
ADD CONSTRAINT submissionLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON UPDATE CASCADE,
ADD CONSTRAINT submissionUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE;
