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

INSERT INTO structure (id,filename,content)
VALUES (1,'glucose.png',FILE_READ('${project.build.testOutputDirectory}/structure1'));
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (1,'LC_MS_MS','Coulombe','G100429',NULL,'Human','LTQ_ORBI_TRAP',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,'ONE_DIMENSION','ONE','SILVER',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2010-10-15','Philippe',NULL,NULL,2,3);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (32,'LC_MS_MS','cap_project','cap_experience','cap_goal','human','LTQ_ORBI_TRAP',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'MEDIUM',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2011-10-13',NULL,NULL,NULL,2,3);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (33,'SMALL_MOLECULE',NULL,NULL,NULL,NULL,NULL,'ESI',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'C100H100O100',654.654,654.654,'MeOH/TFA 0.1%',NULL,NULL,0,'MEDIUM',NULL,NULL,1,'2011-10-13',NULL,NULL,NULL,2,3);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (34,'LC_MS_MS','cap_project','cap_experience','cap_goal','human','LTQ_ORBI_TRAP',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'MEDIUM',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2011-10-17',NULL,NULL,NULL,1,2);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (35,'LC_MS_MS','cap_project','cap_experience','cap_goal','human','LTQ_ORBI_TRAP',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'MEDIUM',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2011-11-09',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (36,'LC_MS_MS','cap_project','cap_experience','cap_goal','human','LTQ_ORBI_TRAP',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'MEDIUM',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2011-11-16',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (147,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-08',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (148,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-08',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (149,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-09',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (150,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-14',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (151,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-15',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (152,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-15',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (153,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-15',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (154,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-15',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (155,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-17',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (156,'LC_MS_MS','Flag','POLR2A-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2014-10-22',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (161,'LC_MS_MS','Flag','POLR2B-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,'SILAC','Heavy:Lys8,Arg10\nMedium:Lys4,Arg6\nLight:None',NULL,'2015-05-27',NULL,NULL,NULL,2,10);
INSERT INTO submission (id,service,project,experience,goal,taxonomy,massDetectionInstrument,source,proteolyticDigestionMethod,usedProteolyticDigestionMethod,otherProteolyticDigestionMethod,proteinIdentification,proteinIdentificationLink,enrichmentType,otherEnrichmentType,lowResolution,highResolution,msms,exactMsms,mudPitFraction,proteinContent,protein,postTranslationModification,separation,thickness,coloration,otherColoration,developmentTime,decoloration,weightMarkerQuantity,proteinQuantity,formula,monoisotopicMass,averageMass,solutionSolvent,otherSolvent,toxicity,lightSensitive,storageTemperature,quantification,quantificationLabels,structureId,submissionDate,comments,price,additionalPrice,laboratoryId,userId)
VALUES (162,'LC_MS_MS','Flag','POLR2B-Flag',NULL,'Homo Sapiens','VELOS',NULL,'TRYPSIN',NULL,NULL,'NCBINR',NULL,NULL,NULL,0,0,0,0,NULL,'XLARGE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,'LABEL_FREE',NULL,NULL,'2015-05-27',NULL,NULL,NULL,2,10);
INSERT INTO gelimages (id,submissionId,filename,content)
VALUES (1,1,'frag.jpg',FILE_READ('${project.build.testOutputDirectory}/gelimages1'));
INSERT INTO plate (id,name,type,insertTime)
VALUES (26,'A_20111108','A','2011-11-08 13:33:21');
INSERT INTO plate (id,name,type,insertTime)
VALUES (107,'G_20141008_01','G','2014-10-08 10:41:39');
INSERT INTO plate (id,name,type,insertTime)
VALUES (108,'A_20141008_01','A','2014-10-08 11:18:03');
INSERT INTO plate (id,name,type,insertTime)
VALUES (109,'A_20141008_02','A','2014-10-08 11:24:47');
INSERT INTO plate (id,name,type,insertTime)
VALUES (110,'A_20141009_01','A','2014-10-09 12:19:34');
INSERT INTO plate (id,name,type,insertTime)
VALUES (111,'PM_20141009_01','PM','2014-10-09 12:20:19');
INSERT INTO plate (id,name,type,insertTime)
VALUES (112,'A_20141009_02','A','2014-10-09 13:29:26');
INSERT INTO plate (id,name,type,insertTime)
VALUES (113,'A_20141014_01','A','2014-10-14 14:06:42');
INSERT INTO plate (id,name,type,insertTime)
VALUES (114,'A_20141014_02','A','2014-10-14 14:06:46');
INSERT INTO plate (id,name,type,insertTime)
VALUES (115,'A_20141015_01','A','2014-10-15 09:57:17');
INSERT INTO plate (id,name,type,insertTime)
VALUES (116,'A_20141015_02','A','2014-10-15 09:57:20');
INSERT INTO plate (id,name,type,insertTime)
VALUES (117,'A_20141015_03','A','2014-10-15 16:03:39');
INSERT INTO plate (id,name,type,insertTime)
VALUES (118,'A_20141017_01','A','2014-10-17 11:32:34');
INSERT INTO plate (id,name,type,insertTime)
VALUES (119,'A_20141017_02','A','2014-10-17 11:50:01');
INSERT INTO plate (id,name,type,insertTime)
VALUES (120,'A_20141022_01','A','2014-10-22 09:51:11');
INSERT INTO plate (id,name,type,insertTime)
VALUES (121,'A_20141022_02','A','2014-10-22 09:56:23');
INSERT INTO plate (id,name,type,insertTime)
VALUES (122,'A_20141024_01','A','2014-10-24 14:30:21');
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1,'TUBE','FAM119A_band_01',NULL,NULL,NULL,1,NULL,'2010-10-15 10:44:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (2,'TUBE','CAP_20111013_01',NULL,NULL,NULL,442,NULL,'2011-10-13 10:36:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (3,'TUBE','CAP_20111013_05',NULL,NULL,NULL,443,NULL,'2011-10-13 11:00:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (4,'TUBE','control_01',NULL,NULL,NULL,444,NULL,'2011-10-13 11:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (5,'TUBE','CAP_20111017_01',NULL,NULL,NULL,445,NULL,'2011-10-17 15:15:09',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (6,'TUBE','FAM119A_band_01_F1',NULL,NULL,NULL,1,2,'2011-10-19 12:20:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (7,'TUBE','FAM119A_band_01_T1',NULL,NULL,NULL,1,3,'2011-10-19 15:01:00',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (8,'TUBE','CAP_20111109_01',NULL,NULL,NULL,446,NULL,'2011-11-09 14:35:54',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (9,'TUBE','CAP_20111116_01',NULL,NULL,NULL,447,NULL,'2011-11-16 14:29:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (10,'TUBE','control_02',NULL,NULL,NULL,448,NULL,'2011-11-16 14:47:48',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (11,'TUBE','POLR2A_20141008_1',NULL,NULL,NULL,559,NULL,'2014-10-08 10:00:49',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (12,'TUBE','POLR2A_20141008_2',NULL,NULL,NULL,560,NULL,'2014-10-08 10:00:49',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (13,'TUBE','POLR2A_20140908_01',NULL,NULL,NULL,561,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (14,'TUBE','POLR2A_20140908_02',NULL,NULL,NULL,562,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (15,'TUBE','POLR2A_20140908_03',NULL,NULL,NULL,563,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (16,'TUBE','POLR2A_20140908_04',NULL,NULL,NULL,564,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (17,'TUBE','POLR2A_20140908_05',NULL,NULL,NULL,565,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (18,'TUBE','POLR2A_20140908_06',NULL,NULL,NULL,566,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (19,'TUBE','POLR2A_20140908_07',NULL,NULL,NULL,567,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (20,'TUBE','POLR2A_20140908_08',NULL,NULL,NULL,568,NULL,'2014-10-08 11:15:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (21,'TUBE','POLR2A_20140909_01',NULL,NULL,NULL,569,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (22,'TUBE','POLR2A_20140909_02',NULL,NULL,NULL,570,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (23,'TUBE','POLR2A_20140909_03',NULL,NULL,NULL,571,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (24,'TUBE','POLR2A_20140909_04',NULL,NULL,NULL,572,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (25,'TUBE','POLR2A_20140909_05',NULL,NULL,NULL,573,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (26,'TUBE','POLR2A_20140909_06',NULL,NULL,NULL,574,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (27,'TUBE','POLR2A_20140909_07',NULL,NULL,NULL,575,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (28,'TUBE','POLR2A_20140909_08',NULL,NULL,NULL,576,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (29,'TUBE','POLR2A_20140909_09',NULL,NULL,NULL,577,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (30,'TUBE','POLR2A_20140909_10',NULL,NULL,NULL,578,NULL,'2014-10-09 12:18:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (31,'TUBE','POLR2A_20140914_01',NULL,NULL,NULL,579,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (32,'TUBE','POLR2A_20140914_02',NULL,NULL,NULL,580,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (33,'TUBE','POLR2A_20140914_03',NULL,NULL,NULL,581,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (34,'TUBE','POLR2A_20140914_04',NULL,NULL,NULL,582,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (35,'TUBE','POLR2A_20140914_05',NULL,NULL,NULL,583,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (36,'TUBE','POLR2A_20140914_06',NULL,NULL,NULL,584,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (37,'TUBE','POLR2A_20140914_07',NULL,NULL,NULL,585,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (38,'TUBE','POLR2A_20140914_08',NULL,NULL,NULL,586,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (39,'TUBE','POLR2A_20140914_09',NULL,NULL,NULL,587,NULL,'2014-10-14 14:05:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (40,'TUBE','POLR2A_20140914_10',NULL,NULL,NULL,588,NULL,'2014-10-14 14:05:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (41,'TUBE','POLR2A_20141015_01',NULL,NULL,NULL,589,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (42,'TUBE','POLR2A_20141015_02',NULL,NULL,NULL,590,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (43,'TUBE','POLR2A_20141015_03',NULL,NULL,NULL,591,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (44,'TUBE','POLR2A_20141015_04',NULL,NULL,NULL,592,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (45,'TUBE','POLR2A_20141015_05',NULL,NULL,NULL,593,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (46,'TUBE','POLR2A_20141015_06',NULL,NULL,NULL,594,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (47,'TUBE','POLR2A_20141015_07',NULL,NULL,NULL,595,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (48,'TUBE','POLR2A_20141015_08',NULL,NULL,NULL,596,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (49,'TUBE','POLR2A_20141015_09',NULL,NULL,NULL,597,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (50,'TUBE','POLR2A_20141015_10',NULL,NULL,NULL,598,NULL,'2014-10-15 09:56:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (51,'TUBE','POLR2A_20141015_11',NULL,NULL,NULL,599,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (52,'TUBE','POLR2A_20141015_12',NULL,NULL,NULL,600,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (53,'TUBE','POLR2A_20141015_13',NULL,NULL,NULL,601,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (54,'TUBE','POLR2A_20141015_14',NULL,NULL,NULL,602,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (55,'TUBE','POLR2A_20141015_15',NULL,NULL,NULL,603,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (56,'TUBE','POLR2A_20141015_16',NULL,NULL,NULL,604,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (57,'TUBE','POLR2A_20141015_17',NULL,NULL,NULL,605,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (58,'TUBE','POLR2A_20141015_18',NULL,NULL,NULL,606,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (59,'TUBE','POLR2A_20141015_19',NULL,NULL,NULL,607,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (60,'TUBE','POLR2A_20141015_20',NULL,NULL,NULL,608,NULL,'2014-10-15 13:43:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (61,'TUBE','POLR2A_20141015_21',NULL,NULL,NULL,609,NULL,'2014-10-15 15:43:25',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (62,'TUBE','POLR2A_20141015_22',NULL,NULL,NULL,610,NULL,'2014-10-15 15:43:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (63,'TUBE','POLR2A_20141015_23',NULL,NULL,NULL,611,NULL,'2014-10-15 15:43:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (64,'TUBE','POLR2A_20141015_24',NULL,NULL,NULL,612,NULL,'2014-10-15 15:43:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (65,'TUBE','POLR2A_20141015_21_2',NULL,NULL,NULL,609,374,'2014-10-15 15:44:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (66,'TUBE','POLR2A_20141015_22_2',NULL,NULL,NULL,610,375,'2014-10-15 15:45:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (67,'TUBE','POLR2A_20141015_31',NULL,NULL,NULL,613,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (68,'TUBE','POLR2A_20141015_32',NULL,NULL,NULL,614,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (69,'TUBE','POLR2A_20141015_33',NULL,NULL,NULL,615,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (70,'TUBE','POLR2A_20141015_34',NULL,NULL,NULL,616,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (71,'TUBE','POLR2A_20141015_35',NULL,NULL,NULL,617,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (72,'TUBE','POLR2A_20141015_36',NULL,NULL,NULL,618,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (73,'TUBE','POLR2A_20141015_37',NULL,NULL,NULL,619,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (74,'TUBE','POLR2A_20141015_38',NULL,NULL,NULL,620,NULL,'2014-10-15 16:01:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (75,'TUBE','POLR2A_20141015_31_2',NULL,NULL,NULL,613,380,'2014-10-15 16:02:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (76,'TUBE','POLR2A_20141015_33_2',NULL,NULL,NULL,615,381,'2014-10-15 16:02:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (77,'TUBE','POLR2A_20141015_35_2',NULL,NULL,NULL,617,382,'2014-10-15 16:03:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (78,'TUBE','POLR2A_20141015_37_2',NULL,NULL,NULL,619,383,'2014-10-15 16:03:22',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (79,'TUBE','POLR2A_20141017_01',NULL,NULL,NULL,621,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (80,'TUBE','POLR2A_20141017_02',NULL,NULL,NULL,622,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (81,'TUBE','POLR2A_20141017_03',NULL,NULL,NULL,623,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (82,'TUBE','POLR2A_20141017_04',NULL,NULL,NULL,624,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (83,'TUBE','POLR2A_20141017_05',NULL,NULL,NULL,625,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (84,'TUBE','POLR2A_20141017_06',NULL,NULL,NULL,626,NULL,'2014-10-17 11:29:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (85,'TUBE','POLR2A_20141022_01',NULL,NULL,NULL,627,NULL,'2014-10-22 09:47:04',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (86,'TUBE','POLR2A_20141022_02',NULL,NULL,NULL,628,NULL,'2014-10-22 09:47:04',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (87,'TUBE','POLR2A_20141022_03',NULL,NULL,NULL,629,NULL,'2014-10-22 09:47:04',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (88,'TUBE','POLR2A_20141022_04',NULL,NULL,NULL,630,NULL,'2014-10-22 09:47:04',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (128,'SPOT',NULL,26,0,0,1,8,'2011-11-16 13:31:12',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (129,'SPOT',NULL,26,1,0,1,9,'2011-11-16 15:07:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (130,'SPOT',NULL,26,2,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (131,'SPOT',NULL,26,3,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (132,'SPOT',NULL,26,4,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (133,'SPOT',NULL,26,5,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (134,'SPOT',NULL,26,6,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (135,'SPOT',NULL,26,7,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (136,'SPOT',NULL,26,8,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (137,'SPOT',NULL,26,9,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (138,'SPOT',NULL,26,10,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (139,'SPOT',NULL,26,11,0,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (140,'SPOT',NULL,26,0,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (141,'SPOT',NULL,26,1,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (142,'SPOT',NULL,26,2,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (143,'SPOT',NULL,26,3,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (144,'SPOT',NULL,26,4,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (145,'SPOT',NULL,26,5,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (146,'SPOT',NULL,26,6,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (147,'SPOT',NULL,26,7,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (148,'SPOT',NULL,26,8,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (149,'SPOT',NULL,26,9,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (150,'SPOT',NULL,26,10,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (151,'SPOT',NULL,26,11,1,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (152,'SPOT',NULL,26,0,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (153,'SPOT',NULL,26,1,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (154,'SPOT',NULL,26,2,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (155,'SPOT',NULL,26,3,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (156,'SPOT',NULL,26,4,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (157,'SPOT',NULL,26,5,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (158,'SPOT',NULL,26,6,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (159,'SPOT',NULL,26,7,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (160,'SPOT',NULL,26,8,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (161,'SPOT',NULL,26,9,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (162,'SPOT',NULL,26,10,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (163,'SPOT',NULL,26,11,2,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (164,'SPOT',NULL,26,0,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (165,'SPOT',NULL,26,1,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (166,'SPOT',NULL,26,2,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (167,'SPOT',NULL,26,3,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (168,'SPOT',NULL,26,4,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (169,'SPOT',NULL,26,5,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (170,'SPOT',NULL,26,6,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (171,'SPOT',NULL,26,7,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (172,'SPOT',NULL,26,8,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (173,'SPOT',NULL,26,9,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (174,'SPOT',NULL,26,10,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (175,'SPOT',NULL,26,11,3,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (176,'SPOT',NULL,26,0,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (177,'SPOT',NULL,26,1,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (178,'SPOT',NULL,26,2,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (179,'SPOT',NULL,26,3,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (180,'SPOT',NULL,26,4,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (181,'SPOT',NULL,26,5,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (182,'SPOT',NULL,26,6,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (183,'SPOT',NULL,26,7,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (184,'SPOT',NULL,26,8,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (185,'SPOT',NULL,26,9,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (186,'SPOT',NULL,26,10,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (187,'SPOT',NULL,26,11,4,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (188,'SPOT',NULL,26,0,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (189,'SPOT',NULL,26,1,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (190,'SPOT',NULL,26,2,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (191,'SPOT',NULL,26,3,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (192,'SPOT',NULL,26,4,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (193,'SPOT',NULL,26,5,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (194,'SPOT',NULL,26,6,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (195,'SPOT',NULL,26,7,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (196,'SPOT',NULL,26,8,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (197,'SPOT',NULL,26,9,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (198,'SPOT',NULL,26,10,5,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (199,'SPOT',NULL,26,11,5,NULL,NULL,'2011-11-08 13:33:21',1);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (200,'SPOT',NULL,26,0,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (201,'SPOT',NULL,26,1,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (202,'SPOT',NULL,26,2,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (203,'SPOT',NULL,26,3,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (204,'SPOT',NULL,26,4,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (205,'SPOT',NULL,26,5,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (206,'SPOT',NULL,26,6,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (207,'SPOT',NULL,26,7,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (208,'SPOT',NULL,26,8,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (209,'SPOT',NULL,26,9,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (210,'SPOT',NULL,26,10,6,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (211,'SPOT',NULL,26,11,6,NULL,NULL,'2011-11-08 13:33:21',1);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (212,'SPOT',NULL,26,0,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (213,'SPOT',NULL,26,1,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (214,'SPOT',NULL,26,2,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (215,'SPOT',NULL,26,3,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (216,'SPOT',NULL,26,4,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (217,'SPOT',NULL,26,5,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (218,'SPOT',NULL,26,6,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (219,'SPOT',NULL,26,7,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (220,'SPOT',NULL,26,8,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (221,'SPOT',NULL,26,9,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (222,'SPOT',NULL,26,10,7,NULL,NULL,'2011-11-08 13:33:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (223,'SPOT',NULL,26,11,7,NULL,NULL,'2011-11-08 13:33:21',1);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (224,'SPOT',NULL,107,0,0,559,194,'2014-10-08 10:41:52',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (225,'SPOT',NULL,107,1,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (226,'SPOT',NULL,107,2,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (227,'SPOT',NULL,107,3,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (228,'SPOT',NULL,107,4,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (229,'SPOT',NULL,107,5,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (230,'SPOT',NULL,107,6,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (231,'SPOT',NULL,107,7,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (232,'SPOT',NULL,107,8,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (233,'SPOT',NULL,107,9,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (234,'SPOT',NULL,107,10,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (235,'SPOT',NULL,107,11,0,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (236,'SPOT',NULL,107,0,1,560,195,'2014-10-08 10:41:52',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (237,'SPOT',NULL,107,1,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (238,'SPOT',NULL,107,2,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (239,'SPOT',NULL,107,3,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (240,'SPOT',NULL,107,4,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (241,'SPOT',NULL,107,5,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (242,'SPOT',NULL,107,6,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (243,'SPOT',NULL,107,7,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (244,'SPOT',NULL,107,8,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (245,'SPOT',NULL,107,9,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (246,'SPOT',NULL,107,10,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (247,'SPOT',NULL,107,11,1,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (248,'SPOT',NULL,107,0,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (249,'SPOT',NULL,107,1,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (250,'SPOT',NULL,107,2,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (251,'SPOT',NULL,107,3,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (252,'SPOT',NULL,107,4,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (253,'SPOT',NULL,107,5,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (254,'SPOT',NULL,107,6,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (255,'SPOT',NULL,107,7,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (256,'SPOT',NULL,107,8,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (257,'SPOT',NULL,107,9,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (258,'SPOT',NULL,107,10,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (259,'SPOT',NULL,107,11,2,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (260,'SPOT',NULL,107,0,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (261,'SPOT',NULL,107,1,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (262,'SPOT',NULL,107,2,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (263,'SPOT',NULL,107,3,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (264,'SPOT',NULL,107,4,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (265,'SPOT',NULL,107,5,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (266,'SPOT',NULL,107,6,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (267,'SPOT',NULL,107,7,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (268,'SPOT',NULL,107,8,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (269,'SPOT',NULL,107,9,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (270,'SPOT',NULL,107,10,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (271,'SPOT',NULL,107,11,3,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (272,'SPOT',NULL,107,0,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (273,'SPOT',NULL,107,1,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (274,'SPOT',NULL,107,2,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (275,'SPOT',NULL,107,3,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (276,'SPOT',NULL,107,4,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (277,'SPOT',NULL,107,5,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (278,'SPOT',NULL,107,6,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (279,'SPOT',NULL,107,7,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (280,'SPOT',NULL,107,8,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (281,'SPOT',NULL,107,9,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (282,'SPOT',NULL,107,10,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (283,'SPOT',NULL,107,11,4,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (284,'SPOT',NULL,107,0,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (285,'SPOT',NULL,107,1,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (286,'SPOT',NULL,107,2,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (287,'SPOT',NULL,107,3,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (288,'SPOT',NULL,107,4,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (289,'SPOT',NULL,107,5,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (290,'SPOT',NULL,107,6,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (291,'SPOT',NULL,107,7,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (292,'SPOT',NULL,107,8,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (293,'SPOT',NULL,107,9,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (294,'SPOT',NULL,107,10,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (295,'SPOT',NULL,107,11,5,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (296,'SPOT',NULL,107,0,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (297,'SPOT',NULL,107,1,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (298,'SPOT',NULL,107,2,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (299,'SPOT',NULL,107,3,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (300,'SPOT',NULL,107,4,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (301,'SPOT',NULL,107,5,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (302,'SPOT',NULL,107,6,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (303,'SPOT',NULL,107,7,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (304,'SPOT',NULL,107,8,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (305,'SPOT',NULL,107,9,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (306,'SPOT',NULL,107,10,6,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (307,'SPOT',NULL,107,11,6,NULL,NULL,'2014-10-08 10:41:39',1);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (308,'SPOT',NULL,107,0,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (309,'SPOT',NULL,107,1,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (310,'SPOT',NULL,107,2,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (311,'SPOT',NULL,107,3,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (312,'SPOT',NULL,107,4,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (313,'SPOT',NULL,107,5,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (314,'SPOT',NULL,107,6,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (315,'SPOT',NULL,107,7,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (316,'SPOT',NULL,107,8,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (317,'SPOT',NULL,107,9,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (318,'SPOT',NULL,107,10,7,NULL,NULL,'2014-10-08 10:41:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (319,'SPOT',NULL,107,11,7,NULL,NULL,'2014-10-08 10:41:39',1);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (320,'SPOT',NULL,108,0,0,561,226,'2014-10-08 11:43:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (321,'SPOT',NULL,108,1,0,565,208,'2014-10-08 11:19:10',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (322,'SPOT',NULL,108,2,0,564,228,'2014-10-08 11:44:38',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (323,'SPOT',NULL,108,3,0,567,214,'2014-10-08 11:23:05',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (324,'SPOT',NULL,108,4,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (325,'SPOT',NULL,108,5,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (326,'SPOT',NULL,108,6,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (327,'SPOT',NULL,108,7,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (328,'SPOT',NULL,108,8,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (329,'SPOT',NULL,108,9,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (330,'SPOT',NULL,108,10,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (331,'SPOT',NULL,108,11,0,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (332,'SPOT',NULL,108,0,1,562,227,'2014-10-08 11:43:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (333,'SPOT',NULL,108,1,1,566,209,'2014-10-08 11:19:10',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (334,'SPOT',NULL,108,2,1,564,229,'2014-10-08 11:44:38',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (335,'SPOT',NULL,108,3,1,567,215,'2014-10-08 11:23:05',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (336,'SPOT',NULL,108,4,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (337,'SPOT',NULL,108,5,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (338,'SPOT',NULL,108,6,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (339,'SPOT',NULL,108,7,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (340,'SPOT',NULL,108,8,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (341,'SPOT',NULL,108,9,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (342,'SPOT',NULL,108,10,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (343,'SPOT',NULL,108,11,1,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (344,'SPOT',NULL,108,0,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (345,'SPOT',NULL,108,1,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (346,'SPOT',NULL,108,2,2,563,230,'2014-10-08 11:44:38',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (347,'SPOT',NULL,108,3,2,568,216,'2014-10-08 11:23:05',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (348,'SPOT',NULL,108,4,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (349,'SPOT',NULL,108,5,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (350,'SPOT',NULL,108,6,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (351,'SPOT',NULL,108,7,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (352,'SPOT',NULL,108,8,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (353,'SPOT',NULL,108,9,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (354,'SPOT',NULL,108,10,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (355,'SPOT',NULL,108,11,2,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (356,'SPOT',NULL,108,0,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (357,'SPOT',NULL,108,1,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (358,'SPOT',NULL,108,2,3,563,231,'2014-10-08 11:44:38',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (359,'SPOT',NULL,108,3,3,568,217,'2014-10-08 11:23:05',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (360,'SPOT',NULL,108,4,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (361,'SPOT',NULL,108,5,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (362,'SPOT',NULL,108,6,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (363,'SPOT',NULL,108,7,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (364,'SPOT',NULL,108,8,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (365,'SPOT',NULL,108,9,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (366,'SPOT',NULL,108,10,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (367,'SPOT',NULL,108,11,3,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (368,'SPOT',NULL,108,0,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (369,'SPOT',NULL,108,1,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (370,'SPOT',NULL,108,2,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (371,'SPOT',NULL,108,3,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (372,'SPOT',NULL,108,4,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (373,'SPOT',NULL,108,5,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (374,'SPOT',NULL,108,6,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (375,'SPOT',NULL,108,7,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (376,'SPOT',NULL,108,8,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (377,'SPOT',NULL,108,9,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (378,'SPOT',NULL,108,10,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (379,'SPOT',NULL,108,11,4,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (380,'SPOT',NULL,108,0,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (381,'SPOT',NULL,108,1,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (382,'SPOT',NULL,108,2,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (383,'SPOT',NULL,108,3,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (384,'SPOT',NULL,108,4,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (385,'SPOT',NULL,108,5,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (386,'SPOT',NULL,108,6,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (387,'SPOT',NULL,108,7,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (388,'SPOT',NULL,108,8,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (389,'SPOT',NULL,108,9,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (390,'SPOT',NULL,108,10,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (391,'SPOT',NULL,108,11,5,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (392,'SPOT',NULL,108,0,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (393,'SPOT',NULL,108,1,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (394,'SPOT',NULL,108,2,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (395,'SPOT',NULL,108,3,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (396,'SPOT',NULL,108,4,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (397,'SPOT',NULL,108,5,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (398,'SPOT',NULL,108,6,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (399,'SPOT',NULL,108,7,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (400,'SPOT',NULL,108,8,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (401,'SPOT',NULL,108,9,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (402,'SPOT',NULL,108,10,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (403,'SPOT',NULL,108,11,6,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (404,'SPOT',NULL,108,0,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (405,'SPOT',NULL,108,1,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (406,'SPOT',NULL,108,2,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (407,'SPOT',NULL,108,3,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (408,'SPOT',NULL,108,4,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (409,'SPOT',NULL,108,5,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (410,'SPOT',NULL,108,6,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (411,'SPOT',NULL,108,7,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (412,'SPOT',NULL,108,8,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (413,'SPOT',NULL,108,9,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (414,'SPOT',NULL,108,10,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (415,'SPOT',NULL,108,11,7,NULL,NULL,'2014-10-08 11:18:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (416,'SPOT',NULL,109,0,0,565,218,'2014-10-08 11:25:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (417,'SPOT',NULL,109,1,0,567,222,'2014-10-08 11:26:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (418,'SPOT',NULL,109,2,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (419,'SPOT',NULL,109,3,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (420,'SPOT',NULL,109,4,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (421,'SPOT',NULL,109,5,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (422,'SPOT',NULL,109,6,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (423,'SPOT',NULL,109,7,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (424,'SPOT',NULL,109,8,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (425,'SPOT',NULL,109,9,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (426,'SPOT',NULL,109,10,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (427,'SPOT',NULL,109,11,0,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (428,'SPOT',NULL,109,0,1,565,219,'2014-10-08 11:25:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (429,'SPOT',NULL,109,1,1,567,223,'2014-10-08 11:26:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (430,'SPOT',NULL,109,2,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (431,'SPOT',NULL,109,3,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (432,'SPOT',NULL,109,4,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (433,'SPOT',NULL,109,5,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (434,'SPOT',NULL,109,6,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (435,'SPOT',NULL,109,7,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (436,'SPOT',NULL,109,8,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (437,'SPOT',NULL,109,9,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (438,'SPOT',NULL,109,10,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (439,'SPOT',NULL,109,11,1,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (440,'SPOT',NULL,109,0,2,566,220,'2014-10-08 11:25:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (441,'SPOT',NULL,109,1,2,568,224,'2014-10-08 11:26:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (442,'SPOT',NULL,109,2,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (443,'SPOT',NULL,109,3,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (444,'SPOT',NULL,109,4,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (445,'SPOT',NULL,109,5,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (446,'SPOT',NULL,109,6,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (447,'SPOT',NULL,109,7,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (448,'SPOT',NULL,109,8,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (449,'SPOT',NULL,109,9,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (450,'SPOT',NULL,109,10,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (451,'SPOT',NULL,109,11,2,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (452,'SPOT',NULL,109,0,3,566,221,'2014-10-08 11:25:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (453,'SPOT',NULL,109,1,3,568,225,'2014-10-08 11:26:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (454,'SPOT',NULL,109,2,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (455,'SPOT',NULL,109,3,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (456,'SPOT',NULL,109,4,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (457,'SPOT',NULL,109,5,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (458,'SPOT',NULL,109,6,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (459,'SPOT',NULL,109,7,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (460,'SPOT',NULL,109,8,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (461,'SPOT',NULL,109,9,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (462,'SPOT',NULL,109,10,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (463,'SPOT',NULL,109,11,3,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (464,'SPOT',NULL,109,0,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (465,'SPOT',NULL,109,1,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (466,'SPOT',NULL,109,2,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (467,'SPOT',NULL,109,3,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (468,'SPOT',NULL,109,4,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (469,'SPOT',NULL,109,5,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (470,'SPOT',NULL,109,6,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (471,'SPOT',NULL,109,7,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (472,'SPOT',NULL,109,8,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (473,'SPOT',NULL,109,9,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (474,'SPOT',NULL,109,10,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (475,'SPOT',NULL,109,11,4,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (476,'SPOT',NULL,109,0,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (477,'SPOT',NULL,109,1,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (478,'SPOT',NULL,109,2,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (479,'SPOT',NULL,109,3,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (480,'SPOT',NULL,109,4,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (481,'SPOT',NULL,109,5,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (482,'SPOT',NULL,109,6,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (483,'SPOT',NULL,109,7,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (484,'SPOT',NULL,109,8,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (485,'SPOT',NULL,109,9,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (486,'SPOT',NULL,109,10,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (487,'SPOT',NULL,109,11,5,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (488,'SPOT',NULL,109,0,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (489,'SPOT',NULL,109,1,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (490,'SPOT',NULL,109,2,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (491,'SPOT',NULL,109,3,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (492,'SPOT',NULL,109,4,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (493,'SPOT',NULL,109,5,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (494,'SPOT',NULL,109,6,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (495,'SPOT',NULL,109,7,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (496,'SPOT',NULL,109,8,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (497,'SPOT',NULL,109,9,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (498,'SPOT',NULL,109,10,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (499,'SPOT',NULL,109,11,6,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (500,'SPOT',NULL,109,0,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (501,'SPOT',NULL,109,1,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (502,'SPOT',NULL,109,2,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (503,'SPOT',NULL,109,3,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (504,'SPOT',NULL,109,4,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (505,'SPOT',NULL,109,5,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (506,'SPOT',NULL,109,6,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (507,'SPOT',NULL,109,7,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (508,'SPOT',NULL,109,8,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (509,'SPOT',NULL,109,9,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (510,'SPOT',NULL,109,10,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (511,'SPOT',NULL,109,11,7,NULL,NULL,'2014-10-08 11:24:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (512,'SPOT',NULL,110,0,0,569,NULL,'2014-10-09 12:19:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (513,'SPOT',NULL,110,1,0,571,240,'2014-10-09 12:22:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (514,'SPOT',NULL,110,2,0,569,NULL,'2014-10-09 13:11:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (515,'SPOT',NULL,110,3,0,574,248,'2014-10-09 13:17:15',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (516,'SPOT',NULL,110,4,0,575,254,'2014-10-09 13:28:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (517,'SPOT',NULL,110,5,0,577,262,'2014-10-09 13:31:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (518,'SPOT',NULL,110,6,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (519,'SPOT',NULL,110,7,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (520,'SPOT',NULL,110,8,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (521,'SPOT',NULL,110,9,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (522,'SPOT',NULL,110,10,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (523,'SPOT',NULL,110,11,0,NULL,NULL,'2014-10-09 12:19:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (524,'SPOT',NULL,110,0,1,570,NULL,'2014-10-09 12:19:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (525,'SPOT',NULL,110,1,1,572,241,'2014-10-09 12:22:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (526,'SPOT',NULL,110,2,1,569,NULL,'2014-10-09 13:11:12',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (527,'SPOT',NULL,110,3,1,574,249,'2014-10-09 13:17:15',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (528,'SPOT',NULL,110,4,1,576,255,'2014-10-09 13:28:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (529,'SPOT',NULL,110,5,1,577,263,'2014-10-09 13:31:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (530,'SPOT',NULL,110,6,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (531,'SPOT',NULL,110,7,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (532,'SPOT',NULL,110,8,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (533,'SPOT',NULL,110,9,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (534,'SPOT',NULL,110,10,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (535,'SPOT',NULL,110,11,1,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (536,'SPOT',NULL,110,0,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (537,'SPOT',NULL,110,1,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (538,'SPOT',NULL,110,2,2,570,NULL,'2014-10-09 13:11:12',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (539,'SPOT',NULL,110,3,2,573,250,'2014-10-09 13:17:15',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (540,'SPOT',NULL,110,4,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (541,'SPOT',NULL,110,5,2,578,264,'2014-10-09 13:31:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (542,'SPOT',NULL,110,6,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (543,'SPOT',NULL,110,7,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (544,'SPOT',NULL,110,8,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (545,'SPOT',NULL,110,9,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (546,'SPOT',NULL,110,10,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (547,'SPOT',NULL,110,11,2,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (548,'SPOT',NULL,110,0,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (549,'SPOT',NULL,110,1,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (550,'SPOT',NULL,110,2,3,570,NULL,'2014-10-09 13:11:12',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (551,'SPOT',NULL,110,3,3,573,251,'2014-10-09 13:17:15',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (552,'SPOT',NULL,110,4,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (553,'SPOT',NULL,110,5,3,578,265,'2014-10-09 13:31:02',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (554,'SPOT',NULL,110,6,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (555,'SPOT',NULL,110,7,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (556,'SPOT',NULL,110,8,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (557,'SPOT',NULL,110,9,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (558,'SPOT',NULL,110,10,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (559,'SPOT',NULL,110,11,3,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (560,'SPOT',NULL,110,0,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (561,'SPOT',NULL,110,1,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (562,'SPOT',NULL,110,2,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (563,'SPOT',NULL,110,3,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (564,'SPOT',NULL,110,4,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (565,'SPOT',NULL,110,5,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (566,'SPOT',NULL,110,6,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (567,'SPOT',NULL,110,7,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (568,'SPOT',NULL,110,8,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (569,'SPOT',NULL,110,9,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (570,'SPOT',NULL,110,10,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (571,'SPOT',NULL,110,11,4,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (572,'SPOT',NULL,110,0,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (573,'SPOT',NULL,110,1,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (574,'SPOT',NULL,110,2,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (575,'SPOT',NULL,110,3,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (576,'SPOT',NULL,110,4,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (577,'SPOT',NULL,110,5,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (578,'SPOT',NULL,110,6,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (579,'SPOT',NULL,110,7,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (580,'SPOT',NULL,110,8,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (581,'SPOT',NULL,110,9,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (582,'SPOT',NULL,110,10,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (583,'SPOT',NULL,110,11,5,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (584,'SPOT',NULL,110,0,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (585,'SPOT',NULL,110,1,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (586,'SPOT',NULL,110,2,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (587,'SPOT',NULL,110,3,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (588,'SPOT',NULL,110,4,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (589,'SPOT',NULL,110,5,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (590,'SPOT',NULL,110,6,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (591,'SPOT',NULL,110,7,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (592,'SPOT',NULL,110,8,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (593,'SPOT',NULL,110,9,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (594,'SPOT',NULL,110,10,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (595,'SPOT',NULL,110,11,6,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (596,'SPOT',NULL,110,0,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (597,'SPOT',NULL,110,1,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (598,'SPOT',NULL,110,2,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (599,'SPOT',NULL,110,3,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (600,'SPOT',NULL,110,4,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (601,'SPOT',NULL,110,5,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (602,'SPOT',NULL,110,6,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (603,'SPOT',NULL,110,7,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (604,'SPOT',NULL,110,8,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (605,'SPOT',NULL,110,9,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (606,'SPOT',NULL,110,10,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (607,'SPOT',NULL,110,11,7,NULL,NULL,'2014-10-09 12:19:35',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (608,'SPOT',NULL,111,0,0,569,234,'2014-10-09 12:20:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (609,'SPOT',NULL,111,1,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (610,'SPOT',NULL,111,2,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (611,'SPOT',NULL,111,3,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (612,'SPOT',NULL,111,4,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (613,'SPOT',NULL,111,5,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (614,'SPOT',NULL,111,6,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (615,'SPOT',NULL,111,7,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (616,'SPOT',NULL,111,8,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (617,'SPOT',NULL,111,9,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (618,'SPOT',NULL,111,10,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (619,'SPOT',NULL,111,11,0,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (620,'SPOT',NULL,111,0,1,570,235,'2014-10-09 12:20:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (621,'SPOT',NULL,111,1,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (622,'SPOT',NULL,111,2,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (623,'SPOT',NULL,111,3,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (624,'SPOT',NULL,111,4,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (625,'SPOT',NULL,111,5,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (626,'SPOT',NULL,111,6,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (627,'SPOT',NULL,111,7,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (628,'SPOT',NULL,111,8,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (629,'SPOT',NULL,111,9,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (630,'SPOT',NULL,111,10,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (631,'SPOT',NULL,111,11,1,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (632,'SPOT',NULL,111,0,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (633,'SPOT',NULL,111,1,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (634,'SPOT',NULL,111,2,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (635,'SPOT',NULL,111,3,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (636,'SPOT',NULL,111,4,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (637,'SPOT',NULL,111,5,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (638,'SPOT',NULL,111,6,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (639,'SPOT',NULL,111,7,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (640,'SPOT',NULL,111,8,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (641,'SPOT',NULL,111,9,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (642,'SPOT',NULL,111,10,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (643,'SPOT',NULL,111,11,2,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (644,'SPOT',NULL,111,0,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (645,'SPOT',NULL,111,1,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (646,'SPOT',NULL,111,2,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (647,'SPOT',NULL,111,3,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (648,'SPOT',NULL,111,4,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (649,'SPOT',NULL,111,5,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (650,'SPOT',NULL,111,6,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (651,'SPOT',NULL,111,7,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (652,'SPOT',NULL,111,8,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (653,'SPOT',NULL,111,9,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (654,'SPOT',NULL,111,10,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (655,'SPOT',NULL,111,11,3,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (656,'SPOT',NULL,111,0,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (657,'SPOT',NULL,111,1,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (658,'SPOT',NULL,111,2,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (659,'SPOT',NULL,111,3,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (660,'SPOT',NULL,111,4,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (661,'SPOT',NULL,111,5,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (662,'SPOT',NULL,111,6,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (663,'SPOT',NULL,111,7,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (664,'SPOT',NULL,111,8,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (665,'SPOT',NULL,111,9,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (666,'SPOT',NULL,111,10,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (667,'SPOT',NULL,111,11,4,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (668,'SPOT',NULL,111,0,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (669,'SPOT',NULL,111,1,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (670,'SPOT',NULL,111,2,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (671,'SPOT',NULL,111,3,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (672,'SPOT',NULL,111,4,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (673,'SPOT',NULL,111,5,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (674,'SPOT',NULL,111,6,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (675,'SPOT',NULL,111,7,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (676,'SPOT',NULL,111,8,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (677,'SPOT',NULL,111,9,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (678,'SPOT',NULL,111,10,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (679,'SPOT',NULL,111,11,5,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (680,'SPOT',NULL,111,0,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (681,'SPOT',NULL,111,1,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (682,'SPOT',NULL,111,2,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (683,'SPOT',NULL,111,3,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (684,'SPOT',NULL,111,4,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (685,'SPOT',NULL,111,5,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (686,'SPOT',NULL,111,6,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (687,'SPOT',NULL,111,7,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (688,'SPOT',NULL,111,8,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (689,'SPOT',NULL,111,9,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (690,'SPOT',NULL,111,10,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (691,'SPOT',NULL,111,11,6,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (692,'SPOT',NULL,111,0,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (693,'SPOT',NULL,111,1,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (694,'SPOT',NULL,111,2,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (695,'SPOT',NULL,111,3,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (696,'SPOT',NULL,111,4,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (697,'SPOT',NULL,111,5,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (698,'SPOT',NULL,111,6,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (699,'SPOT',NULL,111,7,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (700,'SPOT',NULL,111,8,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (701,'SPOT',NULL,111,9,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (702,'SPOT',NULL,111,10,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (703,'SPOT',NULL,111,11,7,NULL,NULL,'2014-10-09 12:20:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (704,'SPOT',NULL,112,0,0,575,256,'2014-10-09 13:29:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (705,'SPOT',NULL,112,1,0,577,266,'2014-10-09 13:31:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (706,'SPOT',NULL,112,2,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (707,'SPOT',NULL,112,3,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (708,'SPOT',NULL,112,4,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (709,'SPOT',NULL,112,5,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (710,'SPOT',NULL,112,6,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (711,'SPOT',NULL,112,7,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (712,'SPOT',NULL,112,8,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (713,'SPOT',NULL,112,9,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (714,'SPOT',NULL,112,10,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (715,'SPOT',NULL,112,11,0,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (716,'SPOT',NULL,112,0,1,575,257,'2014-10-09 13:29:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (717,'SPOT',NULL,112,1,1,577,267,'2014-10-09 13:31:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (718,'SPOT',NULL,112,2,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (719,'SPOT',NULL,112,3,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (720,'SPOT',NULL,112,4,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (721,'SPOT',NULL,112,5,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (722,'SPOT',NULL,112,6,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (723,'SPOT',NULL,112,7,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (724,'SPOT',NULL,112,8,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (725,'SPOT',NULL,112,9,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (726,'SPOT',NULL,112,10,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (727,'SPOT',NULL,112,11,1,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (728,'SPOT',NULL,112,0,2,576,258,'2014-10-09 13:29:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (729,'SPOT',NULL,112,1,2,578,268,'2014-10-09 13:31:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (730,'SPOT',NULL,112,2,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (731,'SPOT',NULL,112,3,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (732,'SPOT',NULL,112,4,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (733,'SPOT',NULL,112,5,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (734,'SPOT',NULL,112,6,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (735,'SPOT',NULL,112,7,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (736,'SPOT',NULL,112,8,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (737,'SPOT',NULL,112,9,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (738,'SPOT',NULL,112,10,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (739,'SPOT',NULL,112,11,2,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (740,'SPOT',NULL,112,0,3,576,259,'2014-10-09 13:29:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (741,'SPOT',NULL,112,1,3,578,269,'2014-10-09 13:31:32',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (742,'SPOT',NULL,112,2,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (743,'SPOT',NULL,112,3,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (744,'SPOT',NULL,112,4,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (745,'SPOT',NULL,112,5,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (746,'SPOT',NULL,112,6,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (747,'SPOT',NULL,112,7,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (748,'SPOT',NULL,112,8,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (749,'SPOT',NULL,112,9,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (750,'SPOT',NULL,112,10,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (751,'SPOT',NULL,112,11,3,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (752,'SPOT',NULL,112,0,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (753,'SPOT',NULL,112,1,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (754,'SPOT',NULL,112,2,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (755,'SPOT',NULL,112,3,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (756,'SPOT',NULL,112,4,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (757,'SPOT',NULL,112,5,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (758,'SPOT',NULL,112,6,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (759,'SPOT',NULL,112,7,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (760,'SPOT',NULL,112,8,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (761,'SPOT',NULL,112,9,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (762,'SPOT',NULL,112,10,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (763,'SPOT',NULL,112,11,4,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (764,'SPOT',NULL,112,0,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (765,'SPOT',NULL,112,1,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (766,'SPOT',NULL,112,2,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (767,'SPOT',NULL,112,3,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (768,'SPOT',NULL,112,4,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (769,'SPOT',NULL,112,5,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (770,'SPOT',NULL,112,6,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (771,'SPOT',NULL,112,7,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (772,'SPOT',NULL,112,8,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (773,'SPOT',NULL,112,9,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (774,'SPOT',NULL,112,10,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (775,'SPOT',NULL,112,11,5,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (776,'SPOT',NULL,112,0,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (777,'SPOT',NULL,112,1,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (778,'SPOT',NULL,112,2,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (779,'SPOT',NULL,112,3,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (780,'SPOT',NULL,112,4,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (781,'SPOT',NULL,112,5,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (782,'SPOT',NULL,112,6,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (783,'SPOT',NULL,112,7,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (784,'SPOT',NULL,112,8,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (785,'SPOT',NULL,112,9,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (786,'SPOT',NULL,112,10,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (787,'SPOT',NULL,112,11,6,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (788,'SPOT',NULL,112,0,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (789,'SPOT',NULL,112,1,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (790,'SPOT',NULL,112,2,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (791,'SPOT',NULL,112,3,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (792,'SPOT',NULL,112,4,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (793,'SPOT',NULL,112,5,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (794,'SPOT',NULL,112,6,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (795,'SPOT',NULL,112,7,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (796,'SPOT',NULL,112,8,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (797,'SPOT',NULL,112,9,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (798,'SPOT',NULL,112,10,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (799,'SPOT',NULL,112,11,7,NULL,NULL,'2014-10-09 13:29:26',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (800,'SPOT',NULL,113,0,0,579,270,'2014-10-14 14:06:54',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (801,'SPOT',NULL,113,1,0,581,274,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (802,'SPOT',NULL,113,2,0,587,302,'2014-10-14 14:15:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (803,'SPOT',NULL,113,3,0,585,306,'2014-10-14 14:16:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (804,'SPOT',NULL,113,4,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (805,'SPOT',NULL,113,5,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (806,'SPOT',NULL,113,6,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (807,'SPOT',NULL,113,7,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (808,'SPOT',NULL,113,8,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (809,'SPOT',NULL,113,9,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (810,'SPOT',NULL,113,10,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (811,'SPOT',NULL,113,11,0,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (812,'SPOT',NULL,113,0,1,580,271,'2014-10-14 14:06:54',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (813,'SPOT',NULL,113,1,1,582,275,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (814,'SPOT',NULL,113,2,1,587,303,'2014-10-14 14:15:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (815,'SPOT',NULL,113,3,1,585,307,'2014-10-14 14:16:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (816,'SPOT',NULL,113,4,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (817,'SPOT',NULL,113,5,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (818,'SPOT',NULL,113,6,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (819,'SPOT',NULL,113,7,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (820,'SPOT',NULL,113,8,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (821,'SPOT',NULL,113,9,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (822,'SPOT',NULL,113,10,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (823,'SPOT',NULL,113,11,1,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (824,'SPOT',NULL,113,0,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (825,'SPOT',NULL,113,1,2,583,276,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (826,'SPOT',NULL,113,2,2,588,304,'2014-10-14 14:15:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (827,'SPOT',NULL,113,3,2,586,308,'2014-10-14 14:16:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (828,'SPOT',NULL,113,4,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (829,'SPOT',NULL,113,5,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (830,'SPOT',NULL,113,6,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (831,'SPOT',NULL,113,7,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (832,'SPOT',NULL,113,8,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (833,'SPOT',NULL,113,9,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (834,'SPOT',NULL,113,10,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (835,'SPOT',NULL,113,11,2,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (836,'SPOT',NULL,113,0,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (837,'SPOT',NULL,113,1,3,584,277,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (838,'SPOT',NULL,113,2,3,588,305,'2014-10-14 14:15:33',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (839,'SPOT',NULL,113,3,3,586,309,'2014-10-14 14:16:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (840,'SPOT',NULL,113,4,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (841,'SPOT',NULL,113,5,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (842,'SPOT',NULL,113,6,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (843,'SPOT',NULL,113,7,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (844,'SPOT',NULL,113,8,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (845,'SPOT',NULL,113,9,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (846,'SPOT',NULL,113,10,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (847,'SPOT',NULL,113,11,3,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (848,'SPOT',NULL,113,0,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (849,'SPOT',NULL,113,1,4,585,278,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (850,'SPOT',NULL,113,2,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (851,'SPOT',NULL,113,3,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (852,'SPOT',NULL,113,4,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (853,'SPOT',NULL,113,5,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (854,'SPOT',NULL,113,6,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (855,'SPOT',NULL,113,7,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (856,'SPOT',NULL,113,8,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (857,'SPOT',NULL,113,9,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (858,'SPOT',NULL,113,10,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (859,'SPOT',NULL,113,11,4,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (860,'SPOT',NULL,113,0,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (861,'SPOT',NULL,113,1,5,586,279,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (862,'SPOT',NULL,113,2,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (863,'SPOT',NULL,113,3,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (864,'SPOT',NULL,113,4,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (865,'SPOT',NULL,113,5,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (866,'SPOT',NULL,113,6,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (867,'SPOT',NULL,113,7,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (868,'SPOT',NULL,113,8,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (869,'SPOT',NULL,113,9,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (870,'SPOT',NULL,113,10,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (871,'SPOT',NULL,113,11,5,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (872,'SPOT',NULL,113,0,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (873,'SPOT',NULL,113,1,6,587,280,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (874,'SPOT',NULL,113,2,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (875,'SPOT',NULL,113,3,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (876,'SPOT',NULL,113,4,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (877,'SPOT',NULL,113,5,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (878,'SPOT',NULL,113,6,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (879,'SPOT',NULL,113,7,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (880,'SPOT',NULL,113,8,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (881,'SPOT',NULL,113,9,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (882,'SPOT',NULL,113,10,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (883,'SPOT',NULL,113,11,6,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (884,'SPOT',NULL,113,0,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (885,'SPOT',NULL,113,1,7,588,281,'2014-10-14 14:08:31',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (886,'SPOT',NULL,113,2,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (887,'SPOT',NULL,113,3,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (888,'SPOT',NULL,113,4,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (889,'SPOT',NULL,113,5,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (890,'SPOT',NULL,113,6,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (891,'SPOT',NULL,113,7,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (892,'SPOT',NULL,113,8,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (893,'SPOT',NULL,113,9,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (894,'SPOT',NULL,113,10,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (895,'SPOT',NULL,113,11,7,NULL,NULL,'2014-10-14 14:06:42',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (896,'SPOT',NULL,114,0,0,581,290,'2014-10-14 14:10:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (897,'SPOT',NULL,114,1,0,583,294,'2014-10-14 14:11:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (898,'SPOT',NULL,114,2,0,585,292,'2014-10-14 14:10:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (899,'SPOT',NULL,114,3,0,587,298,'2014-10-14 14:13:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (900,'SPOT',NULL,114,4,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (901,'SPOT',NULL,114,5,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (902,'SPOT',NULL,114,6,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (903,'SPOT',NULL,114,7,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (904,'SPOT',NULL,114,8,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (905,'SPOT',NULL,114,9,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (906,'SPOT',NULL,114,10,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (907,'SPOT',NULL,114,11,0,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (908,'SPOT',NULL,114,0,1,582,291,'2014-10-14 14:10:03',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (909,'SPOT',NULL,114,1,1,583,295,'2014-10-14 14:11:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (910,'SPOT',NULL,114,2,1,586,293,'2014-10-14 14:10:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (911,'SPOT',NULL,114,3,1,587,299,'2014-10-14 14:13:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (912,'SPOT',NULL,114,4,1,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (913,'SPOT',NULL,114,5,1,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (914,'SPOT',NULL,114,6,1,NULL,NULL,'2014-10-14 14:06:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (915,'SPOT',NULL,114,7,1,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (916,'SPOT',NULL,114,8,1,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (917,'SPOT',NULL,114,9,1,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (918,'SPOT',NULL,114,10,1,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (919,'SPOT',NULL,114,11,1,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (920,'SPOT',NULL,114,0,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (921,'SPOT',NULL,114,1,2,584,296,'2014-10-14 14:11:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (922,'SPOT',NULL,114,2,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (923,'SPOT',NULL,114,3,2,588,300,'2014-10-14 14:13:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (924,'SPOT',NULL,114,4,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (925,'SPOT',NULL,114,5,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (926,'SPOT',NULL,114,6,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (927,'SPOT',NULL,114,7,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (928,'SPOT',NULL,114,8,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (929,'SPOT',NULL,114,9,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (930,'SPOT',NULL,114,10,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (931,'SPOT',NULL,114,11,2,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (932,'SPOT',NULL,114,0,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (933,'SPOT',NULL,114,1,3,584,297,'2014-10-14 14:11:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (934,'SPOT',NULL,114,2,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (935,'SPOT',NULL,114,3,3,588,301,'2014-10-14 14:13:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (936,'SPOT',NULL,114,4,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (937,'SPOT',NULL,114,5,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (938,'SPOT',NULL,114,6,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (939,'SPOT',NULL,114,7,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (940,'SPOT',NULL,114,8,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (941,'SPOT',NULL,114,9,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (942,'SPOT',NULL,114,10,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (943,'SPOT',NULL,114,11,3,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (944,'SPOT',NULL,114,0,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (945,'SPOT',NULL,114,1,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (946,'SPOT',NULL,114,2,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (947,'SPOT',NULL,114,3,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (948,'SPOT',NULL,114,4,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (949,'SPOT',NULL,114,5,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (950,'SPOT',NULL,114,6,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (951,'SPOT',NULL,114,7,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (952,'SPOT',NULL,114,8,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (953,'SPOT',NULL,114,9,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (954,'SPOT',NULL,114,10,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (955,'SPOT',NULL,114,11,4,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (956,'SPOT',NULL,114,0,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (957,'SPOT',NULL,114,1,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (958,'SPOT',NULL,114,2,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (959,'SPOT',NULL,114,3,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (960,'SPOT',NULL,114,4,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (961,'SPOT',NULL,114,5,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (962,'SPOT',NULL,114,6,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (963,'SPOT',NULL,114,7,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (964,'SPOT',NULL,114,8,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (965,'SPOT',NULL,114,9,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (966,'SPOT',NULL,114,10,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (967,'SPOT',NULL,114,11,5,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (968,'SPOT',NULL,114,0,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (969,'SPOT',NULL,114,1,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (970,'SPOT',NULL,114,2,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (971,'SPOT',NULL,114,3,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (972,'SPOT',NULL,114,4,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (973,'SPOT',NULL,114,5,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (974,'SPOT',NULL,114,6,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (975,'SPOT',NULL,114,7,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (976,'SPOT',NULL,114,8,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (977,'SPOT',NULL,114,9,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (978,'SPOT',NULL,114,10,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (979,'SPOT',NULL,114,11,6,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (980,'SPOT',NULL,114,0,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (981,'SPOT',NULL,114,1,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (982,'SPOT',NULL,114,2,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (983,'SPOT',NULL,114,3,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (984,'SPOT',NULL,114,4,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (985,'SPOT',NULL,114,5,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (986,'SPOT',NULL,114,6,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (987,'SPOT',NULL,114,7,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (988,'SPOT',NULL,114,8,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (989,'SPOT',NULL,114,9,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (990,'SPOT',NULL,114,10,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (991,'SPOT',NULL,114,11,7,NULL,NULL,'2014-10-14 14:06:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (992,'SPOT',NULL,115,0,0,589,310,'2014-10-15 09:57:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (993,'SPOT',NULL,115,1,0,591,322,'2014-10-15 10:31:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (994,'SPOT',NULL,115,2,0,593,326,'2014-10-15 10:33:55',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (995,'SPOT',NULL,115,3,0,595,324,'2014-10-15 10:31:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (996,'SPOT',NULL,115,4,0,597,330,'2014-10-15 10:34:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (997,'SPOT',NULL,115,5,0,599,342,'2014-10-15 13:44:44',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (998,'SPOT',NULL,115,6,0,601,354,'2014-10-15 13:48:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (999,'SPOT',NULL,115,7,0,603,358,'2014-10-15 13:49:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1000,'SPOT',NULL,115,8,0,605,356,'2014-10-15 13:48:37',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1001,'SPOT',NULL,115,9,0,608,362,'2014-10-15 13:50:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1002,'SPOT',NULL,115,10,0,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1003,'SPOT',NULL,115,11,0,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1004,'SPOT',NULL,115,0,1,590,311,'2014-10-15 09:57:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1005,'SPOT',NULL,115,1,1,592,323,'2014-10-15 10:31:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1006,'SPOT',NULL,115,2,1,593,327,'2014-10-15 10:33:55',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1007,'SPOT',NULL,115,3,1,596,325,'2014-10-15 10:31:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1008,'SPOT',NULL,115,4,1,597,331,'2014-10-15 10:34:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1009,'SPOT',NULL,115,5,1,600,343,'2014-10-15 13:44:44',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1010,'SPOT',NULL,115,6,1,602,355,'2014-10-15 13:48:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1011,'SPOT',NULL,115,7,1,603,359,'2014-10-15 13:49:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1012,'SPOT',NULL,115,8,1,606,357,'2014-10-15 13:48:37',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1013,'SPOT',NULL,115,9,1,608,363,'2014-10-15 13:50:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1014,'SPOT',NULL,115,10,1,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1015,'SPOT',NULL,115,11,1,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1016,'SPOT',NULL,115,0,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1017,'SPOT',NULL,115,1,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1018,'SPOT',NULL,115,2,2,594,328,'2014-10-15 10:33:55',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1019,'SPOT',NULL,115,3,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1020,'SPOT',NULL,115,4,2,598,332,'2014-10-15 10:34:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1021,'SPOT',NULL,115,5,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1022,'SPOT',NULL,115,6,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1023,'SPOT',NULL,115,7,2,604,360,'2014-10-15 13:49:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1024,'SPOT',NULL,115,8,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1025,'SPOT',NULL,115,9,2,607,364,'2014-10-15 13:50:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1026,'SPOT',NULL,115,10,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1027,'SPOT',NULL,115,11,2,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1028,'SPOT',NULL,115,0,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1029,'SPOT',NULL,115,1,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1030,'SPOT',NULL,115,2,3,594,329,'2014-10-15 10:33:55',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1031,'SPOT',NULL,115,3,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1032,'SPOT',NULL,115,4,3,598,333,'2014-10-15 10:34:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1033,'SPOT',NULL,115,5,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1034,'SPOT',NULL,115,6,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1035,'SPOT',NULL,115,7,3,604,361,'2014-10-15 13:49:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1036,'SPOT',NULL,115,8,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1037,'SPOT',NULL,115,9,3,607,365,'2014-10-15 13:50:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1038,'SPOT',NULL,115,10,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1039,'SPOT',NULL,115,11,3,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1040,'SPOT',NULL,115,0,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1041,'SPOT',NULL,115,1,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1042,'SPOT',NULL,115,2,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1043,'SPOT',NULL,115,3,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1044,'SPOT',NULL,115,4,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1045,'SPOT',NULL,115,5,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1046,'SPOT',NULL,115,6,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1047,'SPOT',NULL,115,7,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1048,'SPOT',NULL,115,8,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1049,'SPOT',NULL,115,9,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1050,'SPOT',NULL,115,10,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1051,'SPOT',NULL,115,11,4,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1052,'SPOT',NULL,115,0,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1053,'SPOT',NULL,115,1,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1054,'SPOT',NULL,115,2,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1055,'SPOT',NULL,115,3,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1056,'SPOT',NULL,115,4,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1057,'SPOT',NULL,115,5,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1058,'SPOT',NULL,115,6,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1059,'SPOT',NULL,115,7,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1060,'SPOT',NULL,115,8,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1061,'SPOT',NULL,115,9,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1062,'SPOT',NULL,115,10,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1063,'SPOT',NULL,115,11,5,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1064,'SPOT',NULL,115,0,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1065,'SPOT',NULL,115,1,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1066,'SPOT',NULL,115,2,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1067,'SPOT',NULL,115,3,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1068,'SPOT',NULL,115,4,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1069,'SPOT',NULL,115,5,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1070,'SPOT',NULL,115,6,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1071,'SPOT',NULL,115,7,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1072,'SPOT',NULL,115,8,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1073,'SPOT',NULL,115,9,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1074,'SPOT',NULL,115,10,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1075,'SPOT',NULL,115,11,6,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1076,'SPOT',NULL,115,0,7,611,376,'2014-10-15 15:45:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1077,'SPOT',NULL,115,1,7,612,377,'2014-10-15 15:46:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1078,'SPOT',NULL,115,2,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1079,'SPOT',NULL,115,3,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1080,'SPOT',NULL,115,4,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1081,'SPOT',NULL,115,5,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1082,'SPOT',NULL,115,6,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1083,'SPOT',NULL,115,7,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1084,'SPOT',NULL,115,8,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1085,'SPOT',NULL,115,9,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1086,'SPOT',NULL,115,10,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1087,'SPOT',NULL,115,11,7,NULL,NULL,'2014-10-15 09:57:17',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1088,'SPOT',NULL,116,0,0,595,334,'2014-10-15 10:35:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1089,'SPOT',NULL,116,1,0,597,338,'2014-10-15 10:35:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1090,'SPOT',NULL,116,2,0,605,366,'2014-10-15 13:51:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1091,'SPOT',NULL,116,3,0,608,370,'2014-10-15 13:52:13',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1092,'SPOT',NULL,116,4,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1093,'SPOT',NULL,116,5,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1094,'SPOT',NULL,116,6,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1095,'SPOT',NULL,116,7,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1096,'SPOT',NULL,116,8,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1097,'SPOT',NULL,116,9,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1098,'SPOT',NULL,116,10,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1099,'SPOT',NULL,116,11,0,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1100,'SPOT',NULL,116,0,1,595,335,'2014-10-15 10:35:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1101,'SPOT',NULL,116,1,1,597,339,'2014-10-15 10:35:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1102,'SPOT',NULL,116,2,1,605,367,'2014-10-15 13:51:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1103,'SPOT',NULL,116,3,1,608,371,'2014-10-15 13:52:13',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1104,'SPOT',NULL,116,4,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1105,'SPOT',NULL,116,5,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1106,'SPOT',NULL,116,6,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1107,'SPOT',NULL,116,7,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1108,'SPOT',NULL,116,8,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1109,'SPOT',NULL,116,9,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1110,'SPOT',NULL,116,10,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1111,'SPOT',NULL,116,11,1,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1112,'SPOT',NULL,116,0,2,596,336,'2014-10-15 10:35:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1113,'SPOT',NULL,116,1,2,598,340,'2014-10-15 10:35:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1114,'SPOT',NULL,116,2,2,606,368,'2014-10-15 13:51:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1115,'SPOT',NULL,116,3,2,607,372,'2014-10-15 13:52:13',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1116,'SPOT',NULL,116,4,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1117,'SPOT',NULL,116,5,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1118,'SPOT',NULL,116,6,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1119,'SPOT',NULL,116,7,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1120,'SPOT',NULL,116,8,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1121,'SPOT',NULL,116,9,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1122,'SPOT',NULL,116,10,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1123,'SPOT',NULL,116,11,2,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1124,'SPOT',NULL,116,0,3,596,337,'2014-10-15 10:35:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1125,'SPOT',NULL,116,1,3,598,341,'2014-10-15 10:35:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1126,'SPOT',NULL,116,2,3,606,369,'2014-10-15 13:51:30',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1127,'SPOT',NULL,116,3,3,607,373,'2014-10-15 13:52:13',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1128,'SPOT',NULL,116,4,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1129,'SPOT',NULL,116,5,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1130,'SPOT',NULL,116,6,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1131,'SPOT',NULL,116,7,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1132,'SPOT',NULL,116,8,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1133,'SPOT',NULL,116,9,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1134,'SPOT',NULL,116,10,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1135,'SPOT',NULL,116,11,3,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1136,'SPOT',NULL,116,0,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1137,'SPOT',NULL,116,1,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1138,'SPOT',NULL,116,2,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1139,'SPOT',NULL,116,3,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1140,'SPOT',NULL,116,4,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1141,'SPOT',NULL,116,5,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1142,'SPOT',NULL,116,6,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1143,'SPOT',NULL,116,7,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1144,'SPOT',NULL,116,8,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1145,'SPOT',NULL,116,9,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1146,'SPOT',NULL,116,10,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1147,'SPOT',NULL,116,11,4,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1148,'SPOT',NULL,116,0,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1149,'SPOT',NULL,116,1,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1150,'SPOT',NULL,116,2,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1151,'SPOT',NULL,116,3,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1152,'SPOT',NULL,116,4,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1153,'SPOT',NULL,116,5,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1154,'SPOT',NULL,116,6,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1155,'SPOT',NULL,116,7,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1156,'SPOT',NULL,116,8,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1157,'SPOT',NULL,116,9,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1158,'SPOT',NULL,116,10,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1159,'SPOT',NULL,116,11,5,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1160,'SPOT',NULL,116,0,6,614,389,'2014-10-15 16:05:47',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1161,'SPOT',NULL,116,1,6,616,392,'2014-10-15 16:07:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1162,'SPOT',NULL,116,2,6,618,395,'2014-10-15 16:28:12',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1163,'SPOT',NULL,116,3,6,617,396,'2014-10-15 16:29:06',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1164,'SPOT',NULL,116,4,6,620,402,'2014-10-15 16:31:56',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1165,'SPOT',NULL,116,5,6,619,404,'2014-10-15 16:33:25',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1166,'SPOT',NULL,116,6,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1167,'SPOT',NULL,116,7,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1168,'SPOT',NULL,116,8,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1169,'SPOT',NULL,116,9,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1170,'SPOT',NULL,116,10,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1171,'SPOT',NULL,116,11,6,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1172,'SPOT',NULL,116,0,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1173,'SPOT',NULL,116,1,7,616,393,'2014-10-15 16:07:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1174,'SPOT',NULL,116,2,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1175,'SPOT',NULL,116,3,7,617,397,'2014-10-15 16:29:06',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1176,'SPOT',NULL,116,4,7,620,403,'2014-10-15 16:31:56',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1177,'SPOT',NULL,116,5,7,619,405,'2014-10-15 16:33:25',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1178,'SPOT',NULL,116,6,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1179,'SPOT',NULL,116,7,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1180,'SPOT',NULL,116,8,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1181,'SPOT',NULL,116,9,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1182,'SPOT',NULL,116,10,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1183,'SPOT',NULL,116,11,7,NULL,NULL,'2014-10-15 09:57:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1184,'SPOT',NULL,117,0,0,614,384,'2014-10-15 16:03:46',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1185,'SPOT',NULL,117,1,0,616,385,'2014-10-15 16:04:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1186,'SPOT',NULL,117,2,0,618,386,'2014-10-15 16:04:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1187,'SPOT',NULL,117,3,0,620,387,'2014-10-15 16:04:41',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1188,'SPOT',NULL,117,4,0,615,390,'2014-10-15 16:06:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1189,'SPOT',NULL,117,5,0,617,394,'2014-10-15 16:27:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1190,'SPOT',NULL,117,6,0,618,398,'2014-10-15 16:30:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1191,'SPOT',NULL,117,7,0,619,400,'2014-10-15 16:30:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1192,'SPOT',NULL,117,8,0,620,406,'2014-10-15 16:34:07',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1193,'SPOT',NULL,117,9,0,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1194,'SPOT',NULL,117,10,0,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1195,'SPOT',NULL,117,11,0,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1196,'SPOT',NULL,117,0,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1197,'SPOT',NULL,117,1,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1198,'SPOT',NULL,117,2,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1199,'SPOT',NULL,117,3,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1200,'SPOT',NULL,117,4,1,615,391,'2014-10-15 16:06:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1201,'SPOT',NULL,117,5,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1202,'SPOT',NULL,117,6,1,618,399,'2014-10-15 16:30:08',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1203,'SPOT',NULL,117,7,1,619,401,'2014-10-15 16:30:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1204,'SPOT',NULL,117,8,1,620,407,'2014-10-15 16:34:07',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1205,'SPOT',NULL,117,9,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1206,'SPOT',NULL,117,10,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1207,'SPOT',NULL,117,11,1,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1208,'SPOT',NULL,117,0,2,613,388,'2014-10-15 16:05:18',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1209,'SPOT',NULL,117,1,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1210,'SPOT',NULL,117,2,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1211,'SPOT',NULL,117,3,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1212,'SPOT',NULL,117,4,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1213,'SPOT',NULL,117,5,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1214,'SPOT',NULL,117,6,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1215,'SPOT',NULL,117,7,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1216,'SPOT',NULL,117,8,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1217,'SPOT',NULL,117,9,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1218,'SPOT',NULL,117,10,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1219,'SPOT',NULL,117,11,2,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1220,'SPOT',NULL,117,0,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1221,'SPOT',NULL,117,1,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1222,'SPOT',NULL,117,2,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1223,'SPOT',NULL,117,3,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1224,'SPOT',NULL,117,4,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1225,'SPOT',NULL,117,5,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1226,'SPOT',NULL,117,6,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1227,'SPOT',NULL,117,7,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1228,'SPOT',NULL,117,8,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1229,'SPOT',NULL,117,9,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1230,'SPOT',NULL,117,10,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1231,'SPOT',NULL,117,11,3,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1232,'SPOT',NULL,117,0,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1233,'SPOT',NULL,117,1,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1234,'SPOT',NULL,117,2,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1235,'SPOT',NULL,117,3,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1236,'SPOT',NULL,117,4,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1237,'SPOT',NULL,117,5,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1238,'SPOT',NULL,117,6,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1239,'SPOT',NULL,117,7,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1240,'SPOT',NULL,117,8,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1241,'SPOT',NULL,117,9,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1242,'SPOT',NULL,117,10,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1243,'SPOT',NULL,117,11,4,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1244,'SPOT',NULL,117,0,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1245,'SPOT',NULL,117,1,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1246,'SPOT',NULL,117,2,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1247,'SPOT',NULL,117,3,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1248,'SPOT',NULL,117,4,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1249,'SPOT',NULL,117,5,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1250,'SPOT',NULL,117,6,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1251,'SPOT',NULL,117,7,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1252,'SPOT',NULL,117,8,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1253,'SPOT',NULL,117,9,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1254,'SPOT',NULL,117,10,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1255,'SPOT',NULL,117,11,5,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1256,'SPOT',NULL,117,0,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1257,'SPOT',NULL,117,1,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1258,'SPOT',NULL,117,2,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1259,'SPOT',NULL,117,3,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1260,'SPOT',NULL,117,4,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1261,'SPOT',NULL,117,5,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1262,'SPOT',NULL,117,6,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1263,'SPOT',NULL,117,7,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1264,'SPOT',NULL,117,8,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1265,'SPOT',NULL,117,9,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1266,'SPOT',NULL,117,10,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1267,'SPOT',NULL,117,11,6,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1268,'SPOT',NULL,117,0,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1269,'SPOT',NULL,117,1,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1270,'SPOT',NULL,117,2,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1271,'SPOT',NULL,117,3,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1272,'SPOT',NULL,117,4,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1273,'SPOT',NULL,117,5,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1274,'SPOT',NULL,117,6,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1275,'SPOT',NULL,117,7,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1276,'SPOT',NULL,117,8,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1277,'SPOT',NULL,117,9,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1278,'SPOT',NULL,117,10,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1279,'SPOT',NULL,117,11,7,NULL,NULL,'2014-10-15 16:03:39',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1280,'SPOT',NULL,118,0,0,621,408,'2014-10-17 11:32:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1281,'SPOT',NULL,118,1,0,622,410,'2014-10-17 11:33:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1282,'SPOT',NULL,118,2,0,623,414,'2014-10-17 11:47:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1283,'SPOT',NULL,118,3,0,624,416,'2014-10-17 11:48:28',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1284,'SPOT',NULL,118,4,0,625,418,'2014-10-17 11:48:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1285,'SPOT',NULL,118,5,0,626,420,'2014-10-17 11:49:14',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1286,'SPOT',NULL,118,6,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1287,'SPOT',NULL,118,7,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1288,'SPOT',NULL,118,8,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1289,'SPOT',NULL,118,9,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1290,'SPOT',NULL,118,10,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1291,'SPOT',NULL,118,11,0,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1292,'SPOT',NULL,118,0,1,621,409,'2014-10-17 11:32:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1293,'SPOT',NULL,118,1,1,622,411,'2014-10-17 11:33:20',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1294,'SPOT',NULL,118,2,1,623,415,'2014-10-17 11:47:57',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1295,'SPOT',NULL,118,3,1,624,417,'2014-10-17 11:48:28',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1296,'SPOT',NULL,118,4,1,625,419,'2014-10-17 11:48:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1297,'SPOT',NULL,118,5,1,626,421,'2014-10-17 11:49:14',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1298,'SPOT',NULL,118,6,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1299,'SPOT',NULL,118,7,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1300,'SPOT',NULL,118,8,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1301,'SPOT',NULL,118,9,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1302,'SPOT',NULL,118,10,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1303,'SPOT',NULL,118,11,1,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1304,'SPOT',NULL,118,0,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1305,'SPOT',NULL,118,1,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1306,'SPOT',NULL,118,2,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1307,'SPOT',NULL,118,3,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1308,'SPOT',NULL,118,4,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1309,'SPOT',NULL,118,5,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1310,'SPOT',NULL,118,6,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1311,'SPOT',NULL,118,7,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1312,'SPOT',NULL,118,8,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1313,'SPOT',NULL,118,9,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1314,'SPOT',NULL,118,10,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1315,'SPOT',NULL,118,11,2,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1316,'SPOT',NULL,118,0,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1317,'SPOT',NULL,118,1,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1318,'SPOT',NULL,118,2,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1319,'SPOT',NULL,118,3,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1320,'SPOT',NULL,118,4,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1321,'SPOT',NULL,118,5,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1322,'SPOT',NULL,118,6,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1323,'SPOT',NULL,118,7,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1324,'SPOT',NULL,118,8,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1325,'SPOT',NULL,118,9,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1326,'SPOT',NULL,118,10,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1327,'SPOT',NULL,118,11,3,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1328,'SPOT',NULL,118,0,4,625,434,'2014-10-17 11:53:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1329,'SPOT',NULL,118,1,4,626,438,'2014-10-17 11:54:22',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1330,'SPOT',NULL,118,2,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1331,'SPOT',NULL,118,3,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1332,'SPOT',NULL,118,4,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1333,'SPOT',NULL,118,5,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1334,'SPOT',NULL,118,6,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1335,'SPOT',NULL,118,7,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1336,'SPOT',NULL,118,8,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1337,'SPOT',NULL,118,9,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1338,'SPOT',NULL,118,10,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1339,'SPOT',NULL,118,11,4,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1340,'SPOT',NULL,118,0,5,625,435,'2014-10-17 11:53:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1341,'SPOT',NULL,118,1,5,626,439,'2014-10-17 11:54:22',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1342,'SPOT',NULL,118,2,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1343,'SPOT',NULL,118,3,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1344,'SPOT',NULL,118,4,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1345,'SPOT',NULL,118,5,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1346,'SPOT',NULL,118,6,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1347,'SPOT',NULL,118,7,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1348,'SPOT',NULL,118,8,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1349,'SPOT',NULL,118,9,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1350,'SPOT',NULL,118,10,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1351,'SPOT',NULL,118,11,5,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1352,'SPOT',NULL,118,0,6,625,436,'2014-10-17 11:53:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1353,'SPOT',NULL,118,1,6,626,440,'2014-10-17 11:54:22',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1354,'SPOT',NULL,118,2,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1355,'SPOT',NULL,118,3,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1356,'SPOT',NULL,118,4,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1357,'SPOT',NULL,118,5,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1358,'SPOT',NULL,118,6,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1359,'SPOT',NULL,118,7,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1360,'SPOT',NULL,118,8,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1361,'SPOT',NULL,118,9,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1362,'SPOT',NULL,118,10,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1363,'SPOT',NULL,118,11,6,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1364,'SPOT',NULL,118,0,7,625,437,'2014-10-17 11:53:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1365,'SPOT',NULL,118,1,7,626,441,'2014-10-17 11:54:22',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1366,'SPOT',NULL,118,2,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1367,'SPOT',NULL,118,3,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1368,'SPOT',NULL,118,4,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1369,'SPOT',NULL,118,5,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1370,'SPOT',NULL,118,6,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1371,'SPOT',NULL,118,7,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1372,'SPOT',NULL,118,8,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1373,'SPOT',NULL,118,9,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1374,'SPOT',NULL,118,10,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1375,'SPOT',NULL,118,11,7,NULL,NULL,'2014-10-17 11:32:34',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1376,'SPOT',NULL,119,0,0,623,422,'2014-10-17 11:50:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1377,'SPOT',NULL,119,1,0,625,424,'2014-10-17 11:51:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1378,'SPOT',NULL,119,2,0,624,426,'2014-10-17 11:51:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1379,'SPOT',NULL,119,3,0,626,430,'2014-10-17 11:52:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1380,'SPOT',NULL,119,4,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1381,'SPOT',NULL,119,5,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1382,'SPOT',NULL,119,6,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1383,'SPOT',NULL,119,7,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1384,'SPOT',NULL,119,8,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1385,'SPOT',NULL,119,9,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1386,'SPOT',NULL,119,10,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1387,'SPOT',NULL,119,11,0,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1388,'SPOT',NULL,119,0,1,623,423,'2014-10-17 11:50:19',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1389,'SPOT',NULL,119,1,1,625,425,'2014-10-17 11:51:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1390,'SPOT',NULL,119,2,1,624,427,'2014-10-17 11:51:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1391,'SPOT',NULL,119,3,1,626,431,'2014-10-17 11:52:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1392,'SPOT',NULL,119,4,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1393,'SPOT',NULL,119,5,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1394,'SPOT',NULL,119,6,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1395,'SPOT',NULL,119,7,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1396,'SPOT',NULL,119,8,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1397,'SPOT',NULL,119,9,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1398,'SPOT',NULL,119,10,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1399,'SPOT',NULL,119,11,1,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1400,'SPOT',NULL,119,0,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1401,'SPOT',NULL,119,1,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1402,'SPOT',NULL,119,2,2,624,428,'2014-10-17 11:51:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1403,'SPOT',NULL,119,3,2,626,432,'2014-10-17 11:52:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1404,'SPOT',NULL,119,4,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1405,'SPOT',NULL,119,5,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1406,'SPOT',NULL,119,6,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1407,'SPOT',NULL,119,7,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1408,'SPOT',NULL,119,8,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1409,'SPOT',NULL,119,9,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1410,'SPOT',NULL,119,10,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1411,'SPOT',NULL,119,11,2,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1412,'SPOT',NULL,119,0,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1413,'SPOT',NULL,119,1,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1414,'SPOT',NULL,119,2,3,624,429,'2014-10-17 11:51:50',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1415,'SPOT',NULL,119,3,3,626,433,'2014-10-17 11:52:40',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1416,'SPOT',NULL,119,4,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1417,'SPOT',NULL,119,5,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1418,'SPOT',NULL,119,6,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1419,'SPOT',NULL,119,7,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1420,'SPOT',NULL,119,8,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1421,'SPOT',NULL,119,9,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1422,'SPOT',NULL,119,10,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1423,'SPOT',NULL,119,11,3,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1424,'SPOT',NULL,119,0,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1425,'SPOT',NULL,119,1,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1426,'SPOT',NULL,119,2,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1427,'SPOT',NULL,119,3,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1428,'SPOT',NULL,119,4,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1429,'SPOT',NULL,119,5,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1430,'SPOT',NULL,119,6,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1431,'SPOT',NULL,119,7,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1432,'SPOT',NULL,119,8,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1433,'SPOT',NULL,119,9,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1434,'SPOT',NULL,119,10,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1435,'SPOT',NULL,119,11,4,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1436,'SPOT',NULL,119,0,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1437,'SPOT',NULL,119,1,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1438,'SPOT',NULL,119,2,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1439,'SPOT',NULL,119,3,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1440,'SPOT',NULL,119,4,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1441,'SPOT',NULL,119,5,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1442,'SPOT',NULL,119,6,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1443,'SPOT',NULL,119,7,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1444,'SPOT',NULL,119,8,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1445,'SPOT',NULL,119,9,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1446,'SPOT',NULL,119,10,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1447,'SPOT',NULL,119,11,5,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1448,'SPOT',NULL,119,0,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1449,'SPOT',NULL,119,1,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1450,'SPOT',NULL,119,2,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1451,'SPOT',NULL,119,3,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1452,'SPOT',NULL,119,4,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1453,'SPOT',NULL,119,5,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1454,'SPOT',NULL,119,6,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1455,'SPOT',NULL,119,7,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1456,'SPOT',NULL,119,8,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1457,'SPOT',NULL,119,9,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1458,'SPOT',NULL,119,10,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1459,'SPOT',NULL,119,11,6,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1460,'SPOT',NULL,119,0,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1461,'SPOT',NULL,119,1,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1462,'SPOT',NULL,119,2,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1463,'SPOT',NULL,119,3,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1464,'SPOT',NULL,119,4,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1465,'SPOT',NULL,119,5,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1466,'SPOT',NULL,119,6,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1467,'SPOT',NULL,119,7,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1468,'SPOT',NULL,119,8,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1469,'SPOT',NULL,119,9,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1470,'SPOT',NULL,119,10,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1471,'SPOT',NULL,119,11,7,NULL,NULL,'2014-10-17 11:50:01',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1472,'SPOT',NULL,120,0,0,627,442,'2014-10-22 09:51:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1473,'SPOT',NULL,120,1,0,628,444,'2014-10-22 09:55:29',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1474,'SPOT',NULL,120,2,0,629,443,'2014-10-22 09:51:43',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1475,'SPOT',NULL,120,3,0,630,446,'2014-10-22 09:55:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1476,'SPOT',NULL,120,4,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1477,'SPOT',NULL,120,5,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1478,'SPOT',NULL,120,6,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1479,'SPOT',NULL,120,7,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1480,'SPOT',NULL,120,8,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1481,'SPOT',NULL,120,9,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1482,'SPOT',NULL,120,10,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1483,'SPOT',NULL,120,11,0,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1484,'SPOT',NULL,120,0,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1485,'SPOT',NULL,120,1,1,628,445,'2014-10-22 09:55:29',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1486,'SPOT',NULL,120,2,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1487,'SPOT',NULL,120,3,1,630,447,'2014-10-22 09:55:59',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1488,'SPOT',NULL,120,4,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1489,'SPOT',NULL,120,5,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1490,'SPOT',NULL,120,6,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1491,'SPOT',NULL,120,7,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1492,'SPOT',NULL,120,8,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1493,'SPOT',NULL,120,9,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1494,'SPOT',NULL,120,10,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1495,'SPOT',NULL,120,11,1,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1496,'SPOT',NULL,120,0,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1497,'SPOT',NULL,120,1,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1498,'SPOT',NULL,120,2,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1499,'SPOT',NULL,120,3,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1500,'SPOT',NULL,120,4,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1501,'SPOT',NULL,120,5,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1502,'SPOT',NULL,120,6,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1503,'SPOT',NULL,120,7,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1504,'SPOT',NULL,120,8,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1505,'SPOT',NULL,120,9,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1506,'SPOT',NULL,120,10,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1507,'SPOT',NULL,120,11,2,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1508,'SPOT',NULL,120,0,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1509,'SPOT',NULL,120,1,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1510,'SPOT',NULL,120,2,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1511,'SPOT',NULL,120,3,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1512,'SPOT',NULL,120,4,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1513,'SPOT',NULL,120,5,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1514,'SPOT',NULL,120,6,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1515,'SPOT',NULL,120,7,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1516,'SPOT',NULL,120,8,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1517,'SPOT',NULL,120,9,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1518,'SPOT',NULL,120,10,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1519,'SPOT',NULL,120,11,3,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1520,'SPOT',NULL,120,0,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1521,'SPOT',NULL,120,1,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1522,'SPOT',NULL,120,2,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1523,'SPOT',NULL,120,3,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1524,'SPOT',NULL,120,4,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1525,'SPOT',NULL,120,5,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1526,'SPOT',NULL,120,6,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1527,'SPOT',NULL,120,7,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1528,'SPOT',NULL,120,8,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1529,'SPOT',NULL,120,9,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1530,'SPOT',NULL,120,10,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1531,'SPOT',NULL,120,11,4,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1532,'SPOT',NULL,120,0,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1533,'SPOT',NULL,120,1,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1534,'SPOT',NULL,120,2,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1535,'SPOT',NULL,120,3,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1536,'SPOT',NULL,120,4,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1537,'SPOT',NULL,120,5,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1538,'SPOT',NULL,120,6,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1539,'SPOT',NULL,120,7,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1540,'SPOT',NULL,120,8,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1541,'SPOT',NULL,120,9,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1542,'SPOT',NULL,120,10,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1543,'SPOT',NULL,120,11,5,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1544,'SPOT',NULL,120,0,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1545,'SPOT',NULL,120,1,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1546,'SPOT',NULL,120,2,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1547,'SPOT',NULL,120,3,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1548,'SPOT',NULL,120,4,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1549,'SPOT',NULL,120,5,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1550,'SPOT',NULL,120,6,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1551,'SPOT',NULL,120,7,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1552,'SPOT',NULL,120,8,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1553,'SPOT',NULL,120,9,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1554,'SPOT',NULL,120,10,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1555,'SPOT',NULL,120,11,6,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1556,'SPOT',NULL,120,0,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1557,'SPOT',NULL,120,1,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1558,'SPOT',NULL,120,2,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1559,'SPOT',NULL,120,3,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1560,'SPOT',NULL,120,4,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1561,'SPOT',NULL,120,5,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1562,'SPOT',NULL,120,6,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1563,'SPOT',NULL,120,7,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1564,'SPOT',NULL,120,8,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1565,'SPOT',NULL,120,9,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1566,'SPOT',NULL,120,10,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1567,'SPOT',NULL,120,11,7,NULL,NULL,'2014-10-22 09:51:11',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1568,'SPOT',NULL,121,0,0,629,448,'2014-10-22 09:56:54',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1569,'SPOT',NULL,121,1,0,630,450,'2014-10-22 09:57:18',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1570,'SPOT',NULL,121,2,0,NULL,NULL,'2015-05-27 13:47:51',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1571,'SPOT',NULL,121,3,0,NULL,NULL,'2015-05-27 14:06:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1572,'SPOT',NULL,121,4,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1573,'SPOT',NULL,121,5,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1574,'SPOT',NULL,121,6,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1575,'SPOT',NULL,121,7,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1576,'SPOT',NULL,121,8,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1577,'SPOT',NULL,121,9,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1578,'SPOT',NULL,121,10,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1579,'SPOT',NULL,121,11,0,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1580,'SPOT',NULL,121,0,1,629,449,'2014-10-22 09:56:54',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1581,'SPOT',NULL,121,1,1,630,451,'2014-10-22 09:57:18',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1582,'SPOT',NULL,121,2,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1583,'SPOT',NULL,121,3,1,NULL,NULL,'2015-05-27 14:06:27',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1584,'SPOT',NULL,121,4,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1585,'SPOT',NULL,121,5,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1586,'SPOT',NULL,121,6,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1587,'SPOT',NULL,121,7,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1588,'SPOT',NULL,121,8,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1589,'SPOT',NULL,121,9,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1590,'SPOT',NULL,121,10,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1591,'SPOT',NULL,121,11,1,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1592,'SPOT',NULL,121,0,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1593,'SPOT',NULL,121,1,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1594,'SPOT',NULL,121,2,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1595,'SPOT',NULL,121,3,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1596,'SPOT',NULL,121,4,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1597,'SPOT',NULL,121,5,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1598,'SPOT',NULL,121,6,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1599,'SPOT',NULL,121,7,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1600,'SPOT',NULL,121,8,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1601,'SPOT',NULL,121,9,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1602,'SPOT',NULL,121,10,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1603,'SPOT',NULL,121,11,2,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1604,'SPOT',NULL,121,0,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1605,'SPOT',NULL,121,1,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1606,'SPOT',NULL,121,2,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1607,'SPOT',NULL,121,3,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1608,'SPOT',NULL,121,4,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1609,'SPOT',NULL,121,5,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1610,'SPOT',NULL,121,6,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1611,'SPOT',NULL,121,7,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1612,'SPOT',NULL,121,8,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1613,'SPOT',NULL,121,9,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1614,'SPOT',NULL,121,10,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1615,'SPOT',NULL,121,11,3,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1616,'SPOT',NULL,121,0,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1617,'SPOT',NULL,121,1,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1618,'SPOT',NULL,121,2,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1619,'SPOT',NULL,121,3,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1620,'SPOT',NULL,121,4,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1621,'SPOT',NULL,121,5,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1622,'SPOT',NULL,121,6,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1623,'SPOT',NULL,121,7,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1624,'SPOT',NULL,121,8,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1625,'SPOT',NULL,121,9,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1626,'SPOT',NULL,121,10,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1627,'SPOT',NULL,121,11,4,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1628,'SPOT',NULL,121,0,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1629,'SPOT',NULL,121,1,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1630,'SPOT',NULL,121,2,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1631,'SPOT',NULL,121,3,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1632,'SPOT',NULL,121,4,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1633,'SPOT',NULL,121,5,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1634,'SPOT',NULL,121,6,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1635,'SPOT',NULL,121,7,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1636,'SPOT',NULL,121,8,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1637,'SPOT',NULL,121,9,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1638,'SPOT',NULL,121,10,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1639,'SPOT',NULL,121,11,5,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1640,'SPOT',NULL,121,0,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1641,'SPOT',NULL,121,1,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1642,'SPOT',NULL,121,2,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1643,'SPOT',NULL,121,3,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1644,'SPOT',NULL,121,4,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1645,'SPOT',NULL,121,5,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1646,'SPOT',NULL,121,6,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1647,'SPOT',NULL,121,7,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1648,'SPOT',NULL,121,8,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1649,'SPOT',NULL,121,9,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1650,'SPOT',NULL,121,10,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1651,'SPOT',NULL,121,11,6,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1652,'SPOT',NULL,121,0,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1653,'SPOT',NULL,121,1,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1654,'SPOT',NULL,121,2,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1655,'SPOT',NULL,121,3,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1656,'SPOT',NULL,121,4,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1657,'SPOT',NULL,121,5,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1658,'SPOT',NULL,121,6,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1659,'SPOT',NULL,121,7,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1660,'SPOT',NULL,121,8,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1661,'SPOT',NULL,121,9,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1662,'SPOT',NULL,121,10,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1663,'SPOT',NULL,121,11,7,NULL,NULL,'2014-10-22 09:56:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1664,'SPOT',NULL,122,0,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1665,'SPOT',NULL,122,1,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1666,'SPOT',NULL,122,2,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1667,'SPOT',NULL,122,3,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1668,'SPOT',NULL,122,4,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1669,'SPOT',NULL,122,5,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1670,'SPOT',NULL,122,6,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1671,'SPOT',NULL,122,7,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1672,'SPOT',NULL,122,8,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1673,'SPOT',NULL,122,9,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1674,'SPOT',NULL,122,10,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1675,'SPOT',NULL,122,11,0,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1676,'SPOT',NULL,122,0,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1677,'SPOT',NULL,122,1,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1678,'SPOT',NULL,122,2,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1679,'SPOT',NULL,122,3,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1680,'SPOT',NULL,122,4,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1681,'SPOT',NULL,122,5,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1682,'SPOT',NULL,122,6,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1683,'SPOT',NULL,122,7,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1684,'SPOT',NULL,122,8,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1685,'SPOT',NULL,122,9,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1686,'SPOT',NULL,122,10,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1687,'SPOT',NULL,122,11,1,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1688,'SPOT',NULL,122,0,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1689,'SPOT',NULL,122,1,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1690,'SPOT',NULL,122,2,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1691,'SPOT',NULL,122,3,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1692,'SPOT',NULL,122,4,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1693,'SPOT',NULL,122,5,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1694,'SPOT',NULL,122,6,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1695,'SPOT',NULL,122,7,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1696,'SPOT',NULL,122,8,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1697,'SPOT',NULL,122,9,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1698,'SPOT',NULL,122,10,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1699,'SPOT',NULL,122,11,2,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1700,'SPOT',NULL,122,0,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1701,'SPOT',NULL,122,1,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1702,'SPOT',NULL,122,2,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1703,'SPOT',NULL,122,3,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1704,'SPOT',NULL,122,4,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1705,'SPOT',NULL,122,5,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1706,'SPOT',NULL,122,6,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1707,'SPOT',NULL,122,7,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1708,'SPOT',NULL,122,8,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1709,'SPOT',NULL,122,9,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1710,'SPOT',NULL,122,10,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1711,'SPOT',NULL,122,11,3,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1712,'SPOT',NULL,122,0,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1713,'SPOT',NULL,122,1,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1714,'SPOT',NULL,122,2,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1715,'SPOT',NULL,122,3,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1716,'SPOT',NULL,122,4,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1717,'SPOT',NULL,122,5,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1718,'SPOT',NULL,122,6,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1719,'SPOT',NULL,122,7,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1720,'SPOT',NULL,122,8,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1721,'SPOT',NULL,122,9,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1722,'SPOT',NULL,122,10,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1723,'SPOT',NULL,122,11,4,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1724,'SPOT',NULL,122,0,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1725,'SPOT',NULL,122,1,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1726,'SPOT',NULL,122,2,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1727,'SPOT',NULL,122,3,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1728,'SPOT',NULL,122,4,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1729,'SPOT',NULL,122,5,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1730,'SPOT',NULL,122,6,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1731,'SPOT',NULL,122,7,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1732,'SPOT',NULL,122,8,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1733,'SPOT',NULL,122,9,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1734,'SPOT',NULL,122,10,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1735,'SPOT',NULL,122,11,5,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1736,'SPOT',NULL,122,0,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1737,'SPOT',NULL,122,1,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1738,'SPOT',NULL,122,2,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1739,'SPOT',NULL,122,3,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1740,'SPOT',NULL,122,4,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1741,'SPOT',NULL,122,5,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1742,'SPOT',NULL,122,6,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1743,'SPOT',NULL,122,7,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1744,'SPOT',NULL,122,8,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1745,'SPOT',NULL,122,9,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1746,'SPOT',NULL,122,10,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1747,'SPOT',NULL,122,11,6,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1748,'SPOT',NULL,122,0,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1749,'SPOT',NULL,122,1,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1750,'SPOT',NULL,122,2,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1751,'SPOT',NULL,122,3,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1752,'SPOT',NULL,122,4,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1753,'SPOT',NULL,122,5,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1754,'SPOT',NULL,122,6,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1755,'SPOT',NULL,122,7,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1756,'SPOT',NULL,122,8,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1757,'SPOT',NULL,122,9,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1758,'SPOT',NULL,122,10,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (1759,'SPOT',NULL,122,11,7,NULL,NULL,'2014-10-24 14:30:21',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (2278,'TUBE','POLR2B_20150527_01',NULL,NULL,NULL,638,NULL,'2015-05-27 13:33:23',0);
INSERT INTO samplecontainer (id,type,name,plateId,locationColumn,locationRow,sampleId,treatmentSampleId,time,banned)
VALUES (2279,'TUBE','POLR2B_20150527_02',NULL,NULL,NULL,639,NULL,'2015-05-27 13:33:31',0);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (1,'IRC20101015_1','FAM119A_band_01',NULL,'GEL',1,'ANALYSED',1,NULL,NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (442,'IRC20111013_2','CAP_20111013_01',NULL,'SOLUTION',2,'DATA_ANALYSIS',32,'1.5 μg',50,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (443,'IRC20111013_3','CAP_20111013_05',NULL,'SOLUTION',3,'TO_APPROVE',33,NULL,NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (444,'CONTROL.1','control_01','NEGATIVE_CONTROL','GEL',4,'TO_APPROVE',NULL,NULL,NULL,'CONTROL',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (445,'IRC20111017_4','CAP_20111017_01',NULL,'SOLUTION',5,'ANALYSED',34,'1.5 μg',50,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (446,'IRC20111109_5','CAP_20111109_01',NULL,'SOLUTION',8,'DATA_ANALYSIS',35,'1.5 μg',50,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (447,'IRC20111116_6','CAP_20111116_01',NULL,'SOLUTION',9,'TO_APPROVE',36,'1.5 μg',50,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (448,'CONTROL.2','control_02','NEGATIVE_CONTROL','GEL',10,'TO_APPROVE',NULL,NULL,NULL,'CONTROL',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (559,'IRC20141008_7','POLR2A_20141008_1',NULL,'SOLUTION',11,'TO_ANALYSE',147,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (560,'IRC20141008_8','POLR2A_20141008_2',NULL,'SOLUTION',12,'TO_ANALYSE',147,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (561,'IRC20141008_9','POLR2A_20140908_01',NULL,'SOLUTION',13,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (562,'IRC20141008_10','POLR2A_20140908_02',NULL,'SOLUTION',14,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (563,'IRC20141008_11','POLR2A_20140908_03',NULL,'SOLUTION',15,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (564,'IRC20141008_12','POLR2A_20140908_04',NULL,'SOLUTION',16,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (565,'IRC20141008_13','POLR2A_20140908_05',NULL,'SOLUTION',17,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (566,'IRC20141008_14','POLR2A_20140908_06',NULL,'SOLUTION',18,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (567,'IRC20141008_15','POLR2A_20140908_07',NULL,'SOLUTION',19,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (568,'IRC20141008_16','POLR2A_20140908_08',NULL,'SOLUTION',20,'TO_ANALYSE',148,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (569,'IRC20141009_17','POLR2A_20140909_01',NULL,'SOLUTION',21,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (570,'IRC20141009_18','POLR2A_20140909_02',NULL,'SOLUTION',22,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (571,'IRC20141009_19','POLR2A_20140909_03',NULL,'SOLUTION',23,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (572,'IRC20141009_20','POLR2A_20140909_04',NULL,'SOLUTION',24,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (573,'IRC20141009_21','POLR2A_20140909_05',NULL,'SOLUTION',25,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (574,'IRC20141009_22','POLR2A_20140909_06',NULL,'SOLUTION',26,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (575,'IRC20141009_23','POLR2A_20140909_07',NULL,'SOLUTION',27,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (576,'IRC20141009_24','POLR2A_20140909_08',NULL,'SOLUTION',28,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (577,'IRC20141009_25','POLR2A_20140909_09',NULL,'SOLUTION',29,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (578,'IRC20141009_26','POLR2A_20140909_10',NULL,'SOLUTION',30,'TO_ANALYSE',149,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (579,'IRC20141014_27','POLR2A_20140914_01',NULL,'SOLUTION',31,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (580,'IRC20141014_28','POLR2A_20140914_02',NULL,'SOLUTION',32,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (581,'IRC20141014_29','POLR2A_20140914_03',NULL,'SOLUTION',33,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (582,'IRC20141014_30','POLR2A_20140914_04',NULL,'SOLUTION',34,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (583,'IRC20141014_31','POLR2A_20140914_05',NULL,'SOLUTION',35,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (584,'IRC20141014_32','POLR2A_20140914_06',NULL,'SOLUTION',36,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (585,'IRC20141014_33','POLR2A_20140914_07',NULL,'SOLUTION',37,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (586,'IRC20141014_34','POLR2A_20140914_08',NULL,'SOLUTION',38,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (587,'IRC20141014_35','POLR2A_20140914_09',NULL,'SOLUTION',39,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (588,'IRC20141014_36','POLR2A_20140914_10',NULL,'SOLUTION',40,'TO_ANALYSE',150,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (589,'IRC20141015_37','POLR2A_20141015_01',NULL,'DRY',41,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (590,'IRC20141015_38','POLR2A_20141015_02',NULL,'DRY',42,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (591,'IRC20141015_39','POLR2A_20141015_03',NULL,'DRY',43,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (592,'IRC20141015_40','POLR2A_20141015_04',NULL,'DRY',44,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (593,'IRC20141015_41','POLR2A_20141015_05',NULL,'DRY',45,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (594,'IRC20141015_42','POLR2A_20141015_06',NULL,'DRY',46,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (595,'IRC20141015_43','POLR2A_20141015_07',NULL,'DRY',47,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (596,'IRC20141015_44','POLR2A_20141015_08',NULL,'DRY',48,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (597,'IRC20141015_45','POLR2A_20141015_09',NULL,'DRY',49,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (598,'IRC20141015_46','POLR2A_20141015_10',NULL,'DRY',50,'TO_ANALYSE',151,'15 μg',NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (599,'IRC20141015_47','POLR2A_20141015_11',NULL,'SOLUTION',51,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (600,'IRC20141015_48','POLR2A_20141015_12',NULL,'SOLUTION',52,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (601,'IRC20141015_49','POLR2A_20141015_13',NULL,'SOLUTION',53,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (602,'IRC20141015_50','POLR2A_20141015_14',NULL,'SOLUTION',54,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (603,'IRC20141015_51','POLR2A_20141015_15',NULL,'SOLUTION',55,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (604,'IRC20141015_52','POLR2A_20141015_16',NULL,'SOLUTION',56,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (605,'IRC20141015_53','POLR2A_20141015_17',NULL,'SOLUTION',57,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (606,'IRC20141015_54','POLR2A_20141015_18',NULL,'SOLUTION',58,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (607,'IRC20141015_55','POLR2A_20141015_19',NULL,'SOLUTION',59,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (608,'IRC20141015_56','POLR2A_20141015_20',NULL,'SOLUTION',60,'TO_ANALYSE',152,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (609,'IRC20141015_57','POLR2A_20141015_21',NULL,'SOLUTION',61,'TO_ANALYSE',153,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (610,'IRC20141015_58','POLR2A_20141015_22',NULL,'SOLUTION',62,'ANALYSED',153,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (611,'IRC20141015_59','POLR2A_20141015_23',NULL,'SOLUTION',63,'TO_ANALYSE',153,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (612,'IRC20141015_60','POLR2A_20141015_24',NULL,'SOLUTION',64,'ANALYSED',153,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (613,'IRC20141015_61','POLR2A_20141015_31',NULL,'SOLUTION',67,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (614,'IRC20141015_62','POLR2A_20141015_32',NULL,'SOLUTION',68,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (615,'IRC20141015_63','POLR2A_20141015_33',NULL,'SOLUTION',69,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (616,'IRC20141015_64','POLR2A_20141015_34',NULL,'SOLUTION',70,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (617,'IRC20141015_65','POLR2A_20141015_35',NULL,'SOLUTION',71,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (618,'IRC20141015_66','POLR2A_20141015_36',NULL,'SOLUTION',72,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (619,'IRC20141015_67','POLR2A_20141015_37',NULL,'SOLUTION',73,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (620,'IRC20141015_68','POLR2A_20141015_38',NULL,'SOLUTION',74,'TO_ANALYSE',154,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (621,'IRC20141017_69','POLR2A_20141017_01',NULL,'SOLUTION',79,'TO_ANALYSE',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (622,'IRC20141017_70','POLR2A_20141017_02',NULL,'SOLUTION',80,'ANALYSED',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (623,'IRC20141017_71','POLR2A_20141017_03',NULL,'SOLUTION',81,'TO_ANALYSE',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (624,'IRC20141017_72','POLR2A_20141017_04',NULL,'SOLUTION',82,'TO_ANALYSE',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (625,'IRC20141017_73','POLR2A_20141017_05',NULL,'SOLUTION',83,'TO_ANALYSE',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (626,'IRC20141017_74','POLR2A_20141017_06',NULL,'SOLUTION',84,'TO_ANALYSE',155,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (627,'IRC20141022_75','POLR2A_20141022_01',NULL,'SOLUTION',85,'ANALYSED',156,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (628,'IRC20141022_76','POLR2A_20141022_02',NULL,'SOLUTION',86,'ANALYSED',156,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (629,'IRC20141022_77','POLR2A_20141022_03',NULL,'SOLUTION',87,'ANALYSED',156,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (630,'IRC20141022_78','POLR2A_20141022_04',NULL,'SOLUTION',88,'ANALYSED',156,'15 μg',20,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (638,'IRCM20150527_6eox','POLR2B_20150527_01',NULL,'DRY',2278,'TO_ANALYSE',161,NULL,NULL,'SUBMISSION',NULL,NULL);
INSERT INTO sample (id,lims,name,controlType,support,containerId,status,submissionId,quantity,volume,sampleType,numberProtein,molecularWeight)
VALUES (639,'IRCM20150527_4mgz','POLR2B_20150527_02',NULL,'DRY',2279,'TO_ANALYSE',162,NULL,NULL,'SUBMISSION',NULL,NULL);
INSERT INTO standard (id,name,quantity,sampleId,comments,deleted)
VALUES (4,'std1','2 μg',445,NULL,0);
INSERT INTO standard (id,name,quantity,sampleId,comments,deleted)
VALUES (5,'cap_standard','3 μg',447,'some_comments',0);
INSERT INTO standard (id,name,quantity,sampleId,comments,deleted)
VALUES (6,'cap_standard','3 μg',448,'some_comments',0);
INSERT INTO contaminant (id,name,quantity,sampleId,comments,deleted)
VALUES (2,'keratin1','1.5 μg',445,NULL,0);
INSERT INTO contaminant (id,name,quantity,sampleId,comments,deleted)
VALUES (3,'cap_contaminant','3 μg',447,'some_comments',0);
INSERT INTO solvent (id,submissionId,solvent,deleted)
VALUES (54,33,'METHANOL',0);
