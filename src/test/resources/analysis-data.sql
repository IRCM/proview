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

INSERT INTO protocol (id,name,type)
VALUES (1,'digestion_protocol_1','DIGESTION');
INSERT INTO protocol (id,name,type)
VALUES (2,'enrichment_protocol_1','ENRICHMENT');
INSERT INTO protocol (id,name,type)
VALUES (3,'digestion_protocol_2','DIGESTION');
INSERT INTO protocol (id,name,type)
VALUES (4,'enrichment_protocol_2','ENRICHMENT');
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (1,'SOLUBILISATION',NULL,NULL,4,'2011-10-13 11:45:00',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (2,'FRACTIONATION',NULL,'MUDPIT',4,'2011-10-19 12:20:33',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (3,'TRANSFER',NULL,NULL,4,'2011-10-19 15:01:00',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (4,'DILUTION',NULL,NULL,2,'2011-11-09 15:03:54',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (5,'STANDARD_ADDITION',NULL,NULL,2,'2011-11-09 15:12:02',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (6,'DIGESTION',1,NULL,2,'2011-11-09 15:15:20',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (7,'ENRICHMENT',2,NULL,2,'2011-11-09 15:20:21',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (8,'FRACTIONATION',NULL,'MUDPIT',2,'2011-11-16 13:31:12',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (9,'TRANSFER',NULL,NULL,4,'2011-11-16 15:07:34',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (194,'TRANSFER',NULL,NULL,4,'2014-10-08 10:41:52',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (195,'DIGESTION',1,NULL,4,'2014-10-08 10:42:26',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (196,'DIGESTION',1,NULL,4,'2014-10-08 11:16:34',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (197,'DIGESTION',1,NULL,4,'2014-10-08 11:17:13',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (198,'DIGESTION',1,NULL,4,'2014-10-08 11:17:22',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (199,'DIGESTION',1,NULL,4,'2014-10-08 11:17:30',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (201,'TRANSFER',NULL,NULL,4,'2014-10-08 11:19:10',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (203,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-08 11:23:05',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (204,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-08 11:25:39',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (205,'TRANSFER',NULL,NULL,4,'2014-10-08 11:26:32',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (206,'TRANSFER',NULL,NULL,4,'2014-10-08 11:43:31',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (207,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-08 11:44:38',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (209,'TRANSFER',NULL,NULL,4,'2014-10-09 12:20:31',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (210,'DILUTION',NULL,NULL,4,'2014-10-09 12:20:50',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (211,'DILUTION',NULL,NULL,4,'2014-10-09 12:21:46',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (212,'TRANSFER',NULL,NULL,4,'2014-10-09 12:22:08',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (213,'DILUTION',NULL,NULL,4,'2014-10-09 13:10:37',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (215,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-09 13:17:15',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (216,'DILUTION',NULL,NULL,4,'2014-10-09 13:28:11',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (217,'TRANSFER',NULL,NULL,4,'2014-10-09 13:28:42',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (218,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-09 13:29:59',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (219,'DILUTION',NULL,NULL,4,'2014-10-09 13:30:21',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (220,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-09 13:31:02',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (221,'TRANSFER',NULL,NULL,4,'2014-10-09 13:31:32',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (222,'TRANSFER',NULL,NULL,4,'2014-10-14 14:06:54',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (223,'ENRICHMENT',2,NULL,4,'2014-10-14 14:07:16',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (224,'TRANSFER',NULL,NULL,4,'2014-10-14 14:08:31',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (225,'ENRICHMENT',2,NULL,4,'2014-10-14 14:09:02',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (226,'ENRICHMENT',2,NULL,4,'2014-10-14 14:09:11',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (227,'ENRICHMENT',2,NULL,4,'2014-10-14 14:09:24',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (228,'ENRICHMENT',2,NULL,4,'2014-10-14 14:09:29',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (229,'TRANSFER',NULL,NULL,4,'2014-10-14 14:10:03',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (230,'TRANSFER',NULL,NULL,4,'2014-10-14 14:10:23',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (231,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-14 14:11:50',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (232,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-14 14:13:51',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (233,'TRANSFER',NULL,NULL,4,'2014-10-14 14:15:33',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (234,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-14 14:16:47',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (235,'TRANSFER',NULL,NULL,4,'2014-10-15 09:57:34',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (236,'SOLUBILISATION',NULL,NULL,4,'2014-10-15 09:57:51',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (237,'SOLUBILISATION',NULL,NULL,4,'2014-10-15 10:29:55',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (238,'SOLUBILISATION',NULL,NULL,4,'2014-10-15 10:30:09',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (239,'SOLUBILISATION',NULL,NULL,4,'2014-10-15 10:30:19',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (240,'SOLUBILISATION',NULL,NULL,4,'2014-10-15 10:30:29',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (241,'TRANSFER',NULL,NULL,4,'2014-10-15 10:31:23',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (242,'TRANSFER',NULL,NULL,4,'2014-10-15 10:31:47',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (243,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 10:33:55',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (244,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 10:34:39',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (245,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 10:35:27',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (246,'TRANSFER',NULL,NULL,4,'2014-10-15 10:35:59',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (247,'TRANSFER',NULL,NULL,4,'2014-10-15 13:44:44',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (248,'STANDARD_ADDITION',NULL,NULL,4,'2014-10-15 13:45:42',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (249,'STANDARD_ADDITION',NULL,NULL,4,'2014-10-15 13:46:07',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (250,'STANDARD_ADDITION',NULL,NULL,4,'2014-10-15 13:46:27',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (251,'STANDARD_ADDITION',NULL,NULL,4,'2014-10-15 13:46:45',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (252,'STANDARD_ADDITION',NULL,NULL,4,'2014-10-15 13:47:04',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (253,'TRANSFER',NULL,NULL,4,'2014-10-15 13:48:08',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (254,'TRANSFER',NULL,NULL,4,'2014-10-15 13:48:37',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (255,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 13:49:57',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (256,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 13:50:43',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (257,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 13:51:30',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (258,'TRANSFER',NULL,NULL,4,'2014-10-15 13:52:13',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (259,'TRANSFER',NULL,NULL,4,'2014-10-15 15:44:43',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (260,'TRANSFER',NULL,NULL,4,'2014-10-15 15:45:02',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (261,'TRANSFER',NULL,NULL,4,'2014-10-15 15:45:51',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (262,'TRANSFER',NULL,NULL,4,'2014-10-15 15:46:11',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (263,'DILUTION',NULL,NULL,4,'2014-10-15 15:46:38',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (264,'ENRICHMENT',2,NULL,4,'2014-10-15 15:47:30',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (265,'TRANSFER',NULL,NULL,4,'2014-10-15 16:02:21',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (266,'TRANSFER',NULL,NULL,4,'2014-10-15 16:02:40',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (267,'TRANSFER',NULL,NULL,4,'2014-10-15 16:03:02',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (268,'TRANSFER',NULL,NULL,4,'2014-10-15 16:03:22',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (269,'TRANSFER',NULL,NULL,4,'2014-10-15 16:03:46',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (270,'TRANSFER',NULL,NULL,4,'2014-10-15 16:04:11',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (271,'TRANSFER',NULL,NULL,4,'2014-10-15 16:04:27',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (272,'TRANSFER',NULL,NULL,4,'2014-10-15 16:04:41',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (273,'TRANSFER',NULL,NULL,4,'2014-10-15 16:05:18',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (274,'TRANSFER',NULL,NULL,4,'2014-10-15 16:05:47',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (275,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:06:40',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (276,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:07:34',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (277,'TRANSFER',NULL,NULL,4,'2014-10-15 16:27:51',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (278,'TRANSFER',NULL,NULL,4,'2014-10-15 16:28:12',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (279,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:29:06',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (280,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:30:08',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (281,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:30:43',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (282,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-15 16:31:56',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (283,'TRANSFER',NULL,NULL,4,'2014-10-15 16:33:25',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (284,'TRANSFER',NULL,NULL,4,'2014-10-15 16:34:07',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (285,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:32:50',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (286,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:33:20',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (287,'ENRICHMENT',2,NULL,4,'2014-10-17 11:34:06',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (288,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:47:57',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (289,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:48:28',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (290,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:48:51',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (291,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:49:14',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (292,'TRANSFER',NULL,NULL,4,'2014-10-17 11:50:19',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (293,'TRANSFER',NULL,NULL,4,'2014-10-17 11:51:01',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (294,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:51:50',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (295,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:52:40',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (296,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-17 11:53:43',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (297,'TRANSFER',NULL,NULL,4,'2014-10-17 11:54:22',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (298,'TRANSFER',NULL,NULL,4,'2014-10-22 09:51:27',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (299,'TRANSFER',NULL,NULL,4,'2014-10-22 09:51:43',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (300,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-22 09:55:29',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (301,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-22 09:55:59',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (302,'FRACTIONATION',NULL,'MUDPIT',4,'2014-10-22 09:56:54',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (303,'TRANSFER',NULL,NULL,4,'2014-10-22 09:57:18',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (321,'DIGESTION',1,NULL,2,'2015-05-27 13:45:22',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (322,'DILUTION',NULL,NULL,2,'2015-05-27 13:45:56',0,NULL,NULL);
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (323,'TRANSFER',NULL,NULL,2,'2015-05-27 13:47:51',1,'ERRONEOUS','Erroneous transfer test');
INSERT INTO treatment (id,type,protocolId,fractionationType,userId,insertTime,deleted,deletionType,deletionExplanation)
VALUES (324,'FRACTIONATION',NULL,'MUDPIT',2,'2015-05-27 14:06:27',1,'ERRONEOUS','Erroneous fractionation test');
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (1,1,'SOLUBILISATION',1,1,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (2,2,'FRACTIONATION',1,1,0,6,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (3,3,'TRANSFER',1,1,0,7,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (4,4,'DILUTION',442,2,0,NULL,NULL,10,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (5,5,'STANDARD_ADDITION',444,4,0,NULL,NULL,NULL,NULL,NULL,'unit_test_added_standard','20.0 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (6,6,'DIGESTION',444,4,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (7,7,'ENRICHMENT',444,4,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (8,8,'FRACTIONATION',1,1,0,128,NULL,NULL,NULL,NULL,NULL,NULL,2,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (9,9,'TRANSFER',1,1,0,129,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (193,194,'TRANSFER',444,4,0,248,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (194,194,'TRANSFER',559,11,1,224,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (195,194,'TRANSFER',560,12,2,236,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (196,195,'DIGESTION',559,224,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (197,195,'DIGESTION',560,236,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (198,196,'DIGESTION',562,14,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (199,196,'DIGESTION',561,13,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (200,197,'DIGESTION',564,16,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (201,197,'DIGESTION',563,15,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (202,198,'DIGESTION',566,18,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (203,198,'DIGESTION',565,17,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (204,199,'DIGESTION',567,19,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (205,199,'DIGESTION',568,20,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (208,201,'TRANSFER',565,17,0,321,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (209,201,'TRANSFER',566,18,1,333,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (214,203,'FRACTIONATION',567,19,0,323,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (215,203,'FRACTIONATION',567,19,1,335,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (216,203,'FRACTIONATION',568,20,2,347,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (217,203,'FRACTIONATION',568,20,3,359,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (218,204,'FRACTIONATION',565,321,0,416,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (219,204,'FRACTIONATION',565,321,1,428,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (220,204,'FRACTIONATION',566,333,2,440,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (221,204,'FRACTIONATION',566,333,3,452,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (222,205,'TRANSFER',567,323,0,417,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (223,205,'TRANSFER',567,335,1,429,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (224,205,'TRANSFER',568,347,2,441,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (225,205,'TRANSFER',568,359,3,453,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (226,206,'TRANSFER',561,13,0,320,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (227,206,'TRANSFER',562,14,1,332,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (228,207,'FRACTIONATION',564,16,0,322,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (229,207,'FRACTIONATION',564,16,1,334,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (230,207,'FRACTIONATION',563,15,2,346,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (231,207,'FRACTIONATION',563,15,3,358,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (234,209,'TRANSFER',569,21,0,608,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (235,209,'TRANSFER',570,22,1,620,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (236,210,'DILUTION',569,608,0,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (237,210,'DILUTION',570,620,1,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (238,211,'DILUTION',572,24,0,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (239,211,'DILUTION',571,23,1,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (240,212,'TRANSFER',571,23,0,513,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (241,212,'TRANSFER',572,24,1,525,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (242,213,'DILUTION',574,26,0,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (243,213,'DILUTION',573,25,1,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (248,215,'FRACTIONATION',574,26,0,515,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (249,215,'FRACTIONATION',574,26,1,527,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (250,215,'FRACTIONATION',573,25,2,539,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (251,215,'FRACTIONATION',573,25,3,551,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (252,216,'DILUTION',576,28,0,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (253,216,'DILUTION',575,27,1,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (254,217,'TRANSFER',575,27,0,516,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (255,217,'TRANSFER',576,28,1,528,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (256,218,'FRACTIONATION',575,516,0,704,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (257,218,'FRACTIONATION',575,516,1,716,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (258,218,'FRACTIONATION',576,528,2,728,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (259,218,'FRACTIONATION',576,528,3,740,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (260,219,'DILUTION',577,29,0,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (261,219,'DILUTION',578,30,1,NULL,NULL,2,'Methanol',5,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (262,220,'FRACTIONATION',577,29,0,517,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (263,220,'FRACTIONATION',577,29,1,529,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (264,220,'FRACTIONATION',578,30,2,541,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (265,220,'FRACTIONATION',578,30,3,553,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (266,221,'TRANSFER',577,517,0,705,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (267,221,'TRANSFER',577,529,1,717,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (268,221,'TRANSFER',578,541,2,729,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (269,221,'TRANSFER',578,553,3,741,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (270,222,'TRANSFER',579,31,0,800,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (271,222,'TRANSFER',580,32,1,812,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (272,223,'ENRICHMENT',579,800,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (273,223,'ENRICHMENT',580,812,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (274,224,'TRANSFER',581,33,0,801,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (275,224,'TRANSFER',582,34,1,813,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (276,224,'TRANSFER',583,35,2,825,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (277,224,'TRANSFER',584,36,3,837,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (278,224,'TRANSFER',585,37,4,849,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (279,224,'TRANSFER',586,38,5,861,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (280,224,'TRANSFER',587,39,6,873,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (281,224,'TRANSFER',588,40,7,885,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (282,225,'ENRICHMENT',581,801,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (283,225,'ENRICHMENT',582,813,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (284,226,'ENRICHMENT',583,825,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (285,226,'ENRICHMENT',584,837,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (286,227,'ENRICHMENT',585,849,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (287,227,'ENRICHMENT',586,861,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (288,228,'ENRICHMENT',587,873,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (289,228,'ENRICHMENT',588,885,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (290,229,'TRANSFER',581,801,0,896,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (291,229,'TRANSFER',582,813,1,908,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (292,230,'TRANSFER',585,849,0,898,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (293,230,'TRANSFER',586,861,1,910,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (294,231,'FRACTIONATION',583,825,0,897,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (295,231,'FRACTIONATION',583,825,1,909,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (296,231,'FRACTIONATION',584,837,2,921,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (297,231,'FRACTIONATION',584,837,3,933,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (298,232,'FRACTIONATION',587,873,0,899,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (299,232,'FRACTIONATION',587,873,1,911,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (300,232,'FRACTIONATION',588,885,2,923,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (301,232,'FRACTIONATION',588,885,3,935,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (302,233,'TRANSFER',587,899,0,802,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (303,233,'TRANSFER',587,911,1,814,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (304,233,'TRANSFER',588,923,2,826,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (305,233,'TRANSFER',588,935,3,838,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (306,234,'FRACTIONATION',585,898,0,803,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (307,234,'FRACTIONATION',585,898,1,815,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (308,234,'FRACTIONATION',586,910,2,827,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (309,234,'FRACTIONATION',586,910,3,839,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (310,235,'TRANSFER',589,41,0,992,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (311,235,'TRANSFER',590,42,1,1004,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (312,236,'SOLUBILISATION',589,992,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (313,236,'SOLUBILISATION',590,1004,1,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (314,237,'SOLUBILISATION',592,44,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (315,237,'SOLUBILISATION',591,43,1,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (316,238,'SOLUBILISATION',593,45,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (317,238,'SOLUBILISATION',594,46,1,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (318,239,'SOLUBILISATION',595,47,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (319,239,'SOLUBILISATION',596,48,1,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (320,240,'SOLUBILISATION',597,49,0,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (321,240,'SOLUBILISATION',598,50,1,NULL,NULL,NULL,'Methanol',20,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (322,241,'TRANSFER',591,43,0,993,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (323,241,'TRANSFER',592,44,1,1005,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (324,242,'TRANSFER',595,47,0,995,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (325,242,'TRANSFER',596,48,1,1007,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (326,243,'FRACTIONATION',593,45,0,994,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (327,243,'FRACTIONATION',593,45,1,1006,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (328,243,'FRACTIONATION',594,46,2,1018,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (329,243,'FRACTIONATION',594,46,3,1030,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (330,244,'FRACTIONATION',597,49,0,996,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (331,244,'FRACTIONATION',597,49,1,1008,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (332,244,'FRACTIONATION',598,50,2,1020,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (333,244,'FRACTIONATION',598,50,3,1032,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (334,245,'FRACTIONATION',595,995,0,1088,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (335,245,'FRACTIONATION',595,995,1,1100,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (336,245,'FRACTIONATION',596,1007,2,1112,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (337,245,'FRACTIONATION',596,1007,3,1124,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (338,246,'TRANSFER',597,996,0,1089,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (339,246,'TRANSFER',597,1008,1,1101,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (340,246,'TRANSFER',598,1020,2,1113,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (341,246,'TRANSFER',598,1032,3,1125,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (342,247,'TRANSFER',599,51,0,997,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (343,247,'TRANSFER',600,52,1,1009,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (344,248,'STANDARD_ADDITION',599,997,0,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (345,248,'STANDARD_ADDITION',600,1009,1,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (346,249,'STANDARD_ADDITION',601,53,0,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (347,249,'STANDARD_ADDITION',602,54,1,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (348,250,'STANDARD_ADDITION',603,55,0,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (349,250,'STANDARD_ADDITION',604,56,1,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (350,251,'STANDARD_ADDITION',605,57,0,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (351,251,'STANDARD_ADDITION',606,58,1,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (352,252,'STANDARD_ADDITION',608,60,0,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (353,252,'STANDARD_ADDITION',607,59,1,NULL,NULL,NULL,NULL,NULL,'adh','2 μg',NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (354,253,'TRANSFER',601,53,0,998,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (355,253,'TRANSFER',602,54,1,1010,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (356,254,'TRANSFER',605,57,0,1000,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (357,254,'TRANSFER',606,58,1,1012,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (358,255,'FRACTIONATION',603,55,0,999,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (359,255,'FRACTIONATION',603,55,1,1011,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (360,255,'FRACTIONATION',604,56,2,1023,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (361,255,'FRACTIONATION',604,56,3,1035,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (362,256,'FRACTIONATION',608,60,0,1001,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (363,256,'FRACTIONATION',608,60,1,1013,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (364,256,'FRACTIONATION',607,59,2,1025,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (365,256,'FRACTIONATION',607,59,3,1037,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (366,257,'FRACTIONATION',605,1000,0,1090,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (367,257,'FRACTIONATION',605,1000,1,1102,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (368,257,'FRACTIONATION',606,1012,2,1114,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (369,257,'FRACTIONATION',606,1012,3,1126,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (370,258,'TRANSFER',608,1001,0,1091,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (371,258,'TRANSFER',608,1013,1,1103,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (372,258,'TRANSFER',607,1025,2,1115,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (373,258,'TRANSFER',607,1037,3,1127,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (374,259,'TRANSFER',609,61,0,65,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (375,260,'TRANSFER',610,62,0,66,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (376,261,'TRANSFER',611,63,0,1076,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (377,262,'TRANSFER',612,64,0,1077,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (378,263,'DILUTION',609,65,0,NULL,NULL,2,'Methanol',10,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (379,264,'ENRICHMENT',611,1076,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (380,265,'TRANSFER',613,67,0,75,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (381,266,'TRANSFER',615,69,0,76,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (382,267,'TRANSFER',617,71,0,77,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (383,268,'TRANSFER',619,73,0,78,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (384,269,'TRANSFER',614,68,0,1184,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (385,270,'TRANSFER',616,70,0,1185,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (386,271,'TRANSFER',618,72,0,1186,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (387,272,'TRANSFER',620,74,0,1187,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (388,273,'TRANSFER',613,75,0,1208,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (389,274,'TRANSFER',614,1184,0,1160,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (390,275,'FRACTIONATION',615,76,0,1188,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (391,275,'FRACTIONATION',615,76,1,1200,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (392,276,'FRACTIONATION',616,1185,0,1161,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (393,276,'FRACTIONATION',616,1185,1,1173,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (394,277,'TRANSFER',617,77,0,1189,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (395,278,'TRANSFER',618,1186,0,1162,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (396,279,'FRACTIONATION',617,1189,0,1163,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (397,279,'FRACTIONATION',617,1189,1,1175,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (398,280,'FRACTIONATION',618,1162,0,1190,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (399,280,'FRACTIONATION',618,1162,1,1202,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (400,281,'FRACTIONATION',619,78,0,1191,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (401,281,'FRACTIONATION',619,78,1,1203,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (402,282,'FRACTIONATION',620,1187,0,1164,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (403,282,'FRACTIONATION',620,1187,1,1176,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (404,283,'TRANSFER',619,1191,0,1165,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (405,283,'TRANSFER',619,1203,1,1177,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (406,284,'TRANSFER',620,1164,0,1192,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (407,284,'TRANSFER',620,1176,1,1204,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (408,285,'FRACTIONATION',621,79,0,1280,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (409,285,'FRACTIONATION',621,79,1,1292,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (410,286,'FRACTIONATION',622,80,0,1281,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (411,286,'FRACTIONATION',622,80,1,1293,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (412,287,'ENRICHMENT',621,1280,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (413,287,'ENRICHMENT',621,1292,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (414,288,'FRACTIONATION',623,81,0,1282,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (415,288,'FRACTIONATION',623,81,1,1294,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (416,289,'FRACTIONATION',624,82,0,1283,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (417,289,'FRACTIONATION',624,82,1,1295,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (418,290,'FRACTIONATION',625,83,0,1284,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (419,290,'FRACTIONATION',625,83,1,1296,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (420,291,'FRACTIONATION',626,84,0,1285,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (421,291,'FRACTIONATION',626,84,1,1297,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (422,292,'TRANSFER',623,1282,0,1376,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (423,292,'TRANSFER',623,1294,1,1388,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (424,293,'TRANSFER',625,1284,0,1377,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (425,293,'TRANSFER',625,1296,1,1389,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (426,294,'FRACTIONATION',624,1283,0,1378,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (427,294,'FRACTIONATION',624,1283,1,1390,NULL,NULL,NULL,NULL,NULL,NULL,4,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (428,294,'FRACTIONATION',624,1295,2,1402,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (429,294,'FRACTIONATION',624,1295,3,1414,NULL,NULL,NULL,NULL,NULL,NULL,6,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (430,295,'FRACTIONATION',626,1285,0,1379,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (431,295,'FRACTIONATION',626,1285,1,1391,NULL,NULL,NULL,NULL,NULL,NULL,4,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (432,295,'FRACTIONATION',626,1297,2,1403,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (433,295,'FRACTIONATION',626,1297,3,1415,NULL,NULL,NULL,NULL,NULL,NULL,6,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (434,296,'FRACTIONATION',625,1377,0,1328,NULL,NULL,NULL,NULL,NULL,NULL,3,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (435,296,'FRACTIONATION',625,1377,1,1340,NULL,NULL,NULL,NULL,NULL,NULL,4,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (436,296,'FRACTIONATION',625,1389,2,1352,NULL,NULL,NULL,NULL,NULL,NULL,5,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (437,296,'FRACTIONATION',625,1389,3,1364,NULL,NULL,NULL,NULL,NULL,NULL,6,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (438,297,'TRANSFER',626,1379,0,1329,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (439,297,'TRANSFER',626,1391,1,1341,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (440,297,'TRANSFER',626,1403,2,1353,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (441,297,'TRANSFER',626,1415,3,1365,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (442,298,'TRANSFER',627,85,0,1472,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (443,299,'TRANSFER',629,87,0,1474,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (444,300,'FRACTIONATION',628,86,0,1473,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (445,300,'FRACTIONATION',628,86,1,1485,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (446,301,'FRACTIONATION',630,88,0,1475,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (447,301,'FRACTIONATION',630,88,1,1487,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (448,302,'FRACTIONATION',629,1474,0,1568,NULL,NULL,NULL,NULL,NULL,NULL,1,1,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (449,302,'FRACTIONATION',629,1474,1,1580,NULL,NULL,NULL,NULL,NULL,NULL,2,2,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (450,303,'TRANSFER',630,1475,0,1569,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (451,303,'TRANSFER',630,1487,1,1581,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (470,321,'DIGESTION',639,2279,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (471,322,'DILUTION',638,2278,0,NULL,NULL,2,'Methanol',10,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (472,323,'TRANSFER',639,2279,0,1570,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (473,324,'FRACTIONATION',638,2278,0,1583,NULL,NULL,NULL,NULL,NULL,NULL,2,4,NULL);
INSERT INTO treatmentsample (id,treatmentId,treatmentType,sampleId,containerId,listIndex,destinationContainerId,comment,sourceVolume,solvent,solventVolume,name,quantity,position,number,piInterval)
VALUES (474,324,'FRACTIONATION',638,2278,1,1571,NULL,NULL,NULL,NULL,NULL,NULL,1,2,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (1,'NSI','LTQ_ORBI_TRAP','2010-12-13 14:10:27',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (12,'LDTD','LTQ_ORBI_TRAP','2011-10-14 14:14:25',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (13,'LDTD','LTQ_ORBI_TRAP','2011-11-09 15:37:09',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (14,'LDTD','LTQ_ORBI_TRAP','2011-11-15 10:09:11',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (19,'LDTD','VELOS','2014-10-15 15:52:55',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (20,'LDTD','VELOS','2014-10-15 15:53:34',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (21,'NSI','VELOS','2014-10-17 11:34:47',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (22,'NSI','VELOS','2014-10-22 09:49:08',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (23,'NSI','VELOS','2014-10-22 09:49:39',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (24,'NSI','VELOS','2014-10-22 09:50:16',0,NULL,NULL);
INSERT INTO msanalysis (id,source,massDetectionInstrument,insertTime,deleted,deletionType,deletionExplanation)
VALUES (25,'NSI','VELOS','2014-10-22 09:50:43',0,NULL,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (1,1,1,1,1,'XL_20100614_02','XL_20100614_COU_09',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (409,12,442,2,1,'XL_20111014_01','XL_20111014_COU_01',1,0,'after ADH');
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (410,13,446,8,1,'XL_20111014_02','XL_20111014_COU_02',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (411,14,444,4,1,'XL_20111115_01','XL_20111115_COU_01',2,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (412,14,445,5,1,'XL_20111115_01','XL_20111115_COU_02',1,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (413,13,444,4,1,'XL_20111014_03','XL_20111014_COU_03',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (418,19,610,66,1,'XL_20141015_01','XL_20141015_01_COU_01',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (419,20,612,1077,1,'XL_20141015_02','XL_20141015_02_COU_02',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (420,21,622,1281,1,'XL_20141017_01','QE_20150519_ADH_04',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (421,21,622,1293,1,'XL_20141017_01','VL_20150519_ADH_32',2,1,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (422,22,627,85,1,'XL_20141022_01','VL_20150519_ADH_33',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (423,23,628,86,1,'XL_20141022_02','VL_20150514_SMI_06',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (424,24,629,87,1,'XL_20141022_03','XL_20141022_03_COU_01',1,0,NULL);
INSERT INTO acquisition (id,msAnalysisId,sampleId,containerId,numberOfAcquisition,sampleListName,acquisitionFile,position,listIndex,comment)
VALUES (425,25,630,88,1,'XL_20141022_04','XL_20141022_04_COU_01',1,0,NULL);
INSERT INTO dataanalysis (id,sampleId,protein,peptide,maxWorkTime,score,workTime,status,analysisType)
VALUES (3,1,'123456',NULL,2,'123456: 95%',1.75,'ANALYSED','PROTEIN');
INSERT INTO dataanalysis (id,sampleId,protein,peptide,maxWorkTime,score,workTime,status,analysisType)
VALUES (4,442,'123456, 58774','3, 4',4,NULL,NULL,'TO_DO','PROTEIN_PEPTIDE');
INSERT INTO dataanalysis (id,sampleId,protein,peptide,maxWorkTime,score,workTime,status,analysisType)
VALUES (5,446,'85574','54, 62',2.3,NULL,NULL,'TO_DO','PROTEIN_PEPTIDE');
