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

CREATE TABLE IF NOT EXISTS laboratory (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  director varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS address (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  line varchar(200) NOT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalcode varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  hashedpassword varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordversion int(10) NOT NULL,
  signattempts int(10) DEFAULT NULL,
  lastsignattempt datetime DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  address_id bigint(20) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  admin tinyint(1) NOT NULL DEFAULT '0',
  registertime datetime NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY useremail (email),
  CONSTRAINT user_ibfk_1 FOREIGN KEY (address_id) REFERENCES address (id) ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS phonenumber (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  phonenumbers_id bigint(20) DEFAULT NULL,
  type varchar(255) NOT NULL,
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT phonenumber_ibfk_1 FOREIGN KEY (phonenumbers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratoryuser (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  user_id bigint(20) NOT NULL,
  laboratory_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id,laboratory_id),
  CONSTRAINT laboratoryuser_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratoryuser_ibfk_2 FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratorymanager (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  managers_id bigint(20) NOT NULL,
  laboratory_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (managers_id,laboratory_id),
  CONSTRAINT laboratorymanager_ibfk_1 FOREIGN KEY (managers_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratorymanager_ibfk_2 FOREIGN KEY (laboratory_id) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS forgotpassword (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  user_id bigint(20) NOT NULL,
  requestmoment datetime NOT NULL,
  confirmnumber int(11) NOT NULL,
  used tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT forgotpassword_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS submission (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  service varchar(50) DEFAULT NULL,
  experiment varchar(100) DEFAULT NULL,
  goal varchar(150) DEFAULT NULL,
  taxonomy varchar(100) DEFAULT NULL,
  massDetectionInstrument varchar(100) DEFAULT NULL,
  source varchar(100) DEFAULT NULL,
  injectionType varchar(50) DEFAULT NULL,
  proteolyticDigestionMethod varchar(50) DEFAULT NULL,
  usedProteolyticDigestionMethod varchar(100) DEFAULT NULL,
  otherProteolyticDigestionMethod varchar(100) DEFAULT NULL,
  proteinIdentification varchar(50) DEFAULT NULL,
  proteinIdentificationLink varchar(255) DEFAULT NULL,
  enrichmentType varchar(50) DEFAULT NULL,
  otherEnrichmentType varchar(100) DEFAULT NULL,
  lowResolution tinyint(1) NOT NULL DEFAULT '0',
  highResolution tinyint(1) NOT NULL DEFAULT '0',
  msms tinyint(1) NOT NULL DEFAULT '0',
  exactMsms tinyint(1) NOT NULL DEFAULT '0',
  mudPitFraction varchar(50) DEFAULT NULL,
  proteinContent varchar(50) DEFAULT NULL,
  protein varchar(100) DEFAULT NULL,
  postTranslationModification varchar(150) DEFAULT NULL,
  separation varchar(50) DEFAULT NULL,
  thickness varchar(50) DEFAULT NULL,
  coloration varchar(50) DEFAULT NULL,
  otherColoration varchar(100) DEFAULT NULL,
  developmentTime varchar(100) DEFAULT NULL,
  decoloration tinyint(1) NOT NULL DEFAULT '0',
  weightMarkerQuantity double DEFAULT NULL,
  proteinQuantity varchar(100) DEFAULT NULL,
  formula varchar(50) DEFAULT NULL,
  monoisotopicMass double DEFAULT NULL,
  averageMass double DEFAULT NULL,
  solutionSolvent varchar(100) DEFAULT NULL,
  otherSolvent varchar(100) DEFAULT NULL,
  toxicity varchar(100) DEFAULT NULL,
  lightSensitive tinyint(1) NOT NULL DEFAULT '0',
  storageTemperature varchar(50) DEFAULT NULL,
  quantification varchar(50) DEFAULT NULL,
  quantificationComment clob DEFAULT NULL,
  comment clob,
  price double DEFAULT NULL,
  additionalPrice double DEFAULT NULL,
  submissionDate datetime NOT NULL,
  sampleDeliveryDate date DEFAULT NULL,
  digestionDate date DEFAULT NULL,
  analysisDate date DEFAULT NULL,
  dataAvailableDate date DEFAULT NULL,
  version int(11) DEFAULT 0,
  laboratoryId bigint(20) NOT NULL,
  userId bigint(20) DEFAULT NULL,
  hidden tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT submission_ibfk_1 FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON UPDATE CASCADE,
  CONSTRAINT submission_ibfk_2 FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS submissionfiles (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  submissionId bigint(20) DEFAULT NULL,
  filename varchar(255) NOT NULL,
  content blob,
  PRIMARY KEY (id),
  CONSTRAINT submissionfiles_ibfk_1 FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS plate (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(100) NOT NULL,
  columncount int NOT NULL,
  rowcount int NOT NULL,
  submission tinyint NOT NULL DEFAULT '0',
  inserttime datetime NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS plateName ON plate (name);
CREATE TABLE IF NOT EXISTS samplecontainer (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  type varchar(50) NOT NULL,
  name varchar(100) DEFAULT NULL,
  plateId bigint(20) DEFAULT NULL,
  locationColumn int(11) DEFAULT NULL,
  locationRow int(11) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  version int(11) DEFAULT 0,
  time datetime NOT NULL,
  banned tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY samplecontainerPlateId (plateId,locationColumn,locationRow),
  CONSTRAINT samplecontainer_ibfk_1 FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS samplecontainerName ON samplecontainer (name);
CREATE TABLE IF NOT EXISTS sample (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(150) DEFAULT NULL,
  controlType varchar(50) DEFAULT NULL,
  type varchar(50) DEFAULT NULL,
  containerId bigint(20) DEFAULT NULL,
  status int(5) DEFAULT NULL,
  numberProtein int(11) DEFAULT NULL,
  submissionId bigint(20) DEFAULT NULL,
  listIndex int(10) DEFAULT NULL,
  molecularWeight double DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  volume varchar(100) DEFAULT NULL,
  category varchar(50) DEFAULT NULL,
  version int(11) DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT sample_ibfk_1 FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT sample_ibfk_2 FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS sampleName ON sample (name);
ALTER TABLE samplecontainer
ADD CONSTRAINT IF NOT EXISTS samplecontainer_ibfk_2 FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE TABLE IF NOT EXISTS standard (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comment clob,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT standard_ibfk_1 FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS contaminant (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comment clob,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT contaminant_ibfk_1 FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS solvent (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  submissionId bigint(20) DEFAULT NULL,
  solvent varchar(150) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT solvent_ibfk_1 FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS protocol (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(100) DEFAULT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY protocolName (name)
);
CREATE TABLE IF NOT EXISTS treatment (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  type varchar(100) DEFAULT NULL,
  protocolId bigint(20) DEFAULT NULL,
  fractionationType varchar(50) DEFAULT NULL,
  userId bigint(20) DEFAULT NULL,
  insertTime datetime NOT NULL,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType varchar(50) DEFAULT NULL,
  deletionExplanation clob,
  PRIMARY KEY (id),
  CONSTRAINT treatment_ibfk_1 FOREIGN KEY (protocolId) REFERENCES protocol (id) ON UPDATE CASCADE,
  CONSTRAINT treatment_ibfk_2 FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS treatedsample (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  treatmentId bigint(20) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  listIndex int(10) DEFAULT NULL,
  destinationContainerId bigint(20) DEFAULT NULL,
  comment clob,
  sourceVolume double DEFAULT NULL,
  solvent varchar(255) DEFAULT NULL,
  solventVolume double DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  position int(11) DEFAULT NULL,
  number int(11) DEFAULT NULL,
  piInterval varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT treatedsample_ibfk_1 FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT treatedsample_ibfk_2 FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT treatedsample_ibfk_3 FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
  CONSTRAINT treatedsample_ibfk_4 FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS msanalysis (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  source varchar(100) DEFAULT NULL,
  massdetectioninstrument varchar(100) NOT NULL,
  inserttime datetime NOT NULL,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletiontype varchar(50) DEFAULT NULL,
  deletionexplanation clob,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS acquisition (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  msanalysis_id bigint(20) DEFAULT NULL,
  sample_id bigint(20) NOT NULL,
  container_id bigint(20) DEFAULT NULL,
  numberofacquisition int(11) DEFAULT NULL,
  samplelistname varchar(255) DEFAULT NULL,
  acquisitionfile varchar(255) DEFAULT NULL,
  position int(11) NOT NULL,
  listindex int(11) DEFAULT NULL,
  comment varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (sample_id,position),
  CONSTRAINT acquisition_ibfk_1 FOREIGN KEY (msanalysis_id) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT acquisition_ibfk_2 FOREIGN KEY (sample_id) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT acquisition_ibfk_3 FOREIGN KEY (container_id) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS acquisitionFile ON acquisition (acquisitionfile,sample_id);
CREATE TABLE IF NOT EXISTS dataanalysis (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  sample_id bigint(20) NOT NULL,
  protein varchar(255) NOT NULL,
  peptide varchar(255) DEFAULT NULL,
  maxworktime double NOT NULL,
  score clob,
  worktime double DEFAULT NULL,
  status varchar(50) NOT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT dataanalysis_ibfk_1 FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS activity (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  user_id bigint(20) NOT NULL,
  tablename varchar(50) NOT NULL,
  recordid bigint(20) NOT NULL,
  actiontype varchar(50) NOT NULL,
  timestamp datetime NOT NULL,
  explanation clob,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS activityRecordIndex ON activity (tablename,recordid,actiontype);
CREATE TABLE IF NOT EXISTS activityupdate (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  updates_id bigint(20) DEFAULT NULL,
  tablename varchar(50) NOT NULL,
  recordid bigint(20) NOT NULL,
  actiontype varchar(50) NOT NULL,
  actioncolumn varchar(70) DEFAULT NULL,
  oldvalue varchar(255) DEFAULT NULL,
  newvalue varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT activityupdate_ibfk_1 FOREIGN KEY (updates_id) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS preference (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  referer varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (referer, name)
);
CREATE INDEX IF NOT EXISTS preferenceName ON preference (name);
CREATE TABLE IF NOT EXISTS userpreference (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  preference_id bigint(20) NOT NULL,
  user_id bigint(20) NOT NULL,
  value blob DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (preference_id, user_id),
  CONSTRAINT userpreference_ibfk_1 FOREIGN KEY (preference_id) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT userpreference_ibfk_2 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
