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
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS address (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  line varchar(200) NOT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS users (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) DEFAULT NULL,
  signAttempts int(10) DEFAULT NULL,
  lastSignAttempt datetime DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  addressId bigint(20) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  admin tinyint(1) NOT NULL DEFAULT '0',
  registerTime datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY usersEmail (email),
  CONSTRAINT users_ibfk_1 FOREIGN KEY (addressId) REFERENCES address (id) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS phonenumber (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) DEFAULT NULL,
  type varchar(255) NOT NULL,
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT phonenumber_ibfk_1 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratoryuser (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT laboratoryuser_ibfk_1 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratoryuser_ibfk_2 FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratorymanager (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (userId, laboratoryId),
  CONSTRAINT laboratorymanager_ibfk_1 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratorymanager_ibfk_2 FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS forgotpassword (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  requestMoment timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  confirmNumber int(11) NOT NULL,
  used tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT forgotpassword_ibfk_1 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS submission (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  service varchar(50) DEFAULT NULL,
  project varchar(100) DEFAULT NULL,
  experience varchar(100) DEFAULT NULL,
  goal varchar(150) DEFAULT NULL,
  taxonomy varchar(100) DEFAULT NULL,
  massDetectionInstrument varchar(50) DEFAULT NULL,
  source varchar(50) DEFAULT NULL,
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
  quantificationLabels clob DEFAULT NULL,
  comment clob,
  price double DEFAULT NULL,
  additionalPrice double DEFAULT NULL,
  submissionDate timestamp NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  userId bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT submission_ibfk_1 FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON UPDATE CASCADE,
  CONSTRAINT submission_ibfk_2 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
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
  columnCount int NOT NULL,
  rowCount int NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY plateName (name)
);
CREATE TABLE IF NOT EXISTS samplecontainer (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  type varchar(50) NOT NULL,
  name varchar(100) DEFAULT NULL,
  plateId bigint(20) DEFAULT NULL,
  locationColumn int(11) DEFAULT NULL,
  locationRow int(11) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  banned tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY samplecontainerName (name),
  UNIQUE KEY samplecontainerPlateId (plateId,locationColumn,locationRow),
  CONSTRAINT samplecontainer_ibfk_1 FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS sample (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name varchar(150) DEFAULT NULL,
  controlType varchar(50) DEFAULT NULL,
  support varchar(50) DEFAULT NULL,
  containerId bigint(20) DEFAULT NULL,
  status varchar(50),
  submissionId bigint(20) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  volume double DEFAULT NULL,
  sampleType varchar(50) DEFAULT NULL,
  numberProtein int(11) DEFAULT NULL,
  molecularWeight double DEFAULT NULL,
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
  deleted tinyint(4) NOT NULL DEFAULT '0',
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
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType varchar(50) DEFAULT NULL,
  deletionExplanation clob,
  PRIMARY KEY (id),
  CONSTRAINT treatment_ibfk_1 FOREIGN KEY (protocolId) REFERENCES protocol (id) ON UPDATE CASCADE,
  CONSTRAINT treatment_ibfk_2 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS treatmentsample (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  treatmentId bigint(20) DEFAULT NULL,
  treatmentType varchar(100) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
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
  CONSTRAINT treatmentsample_ibfk_1 FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT treatmentsample_ibfk_2 FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT treatmentsample_ibfk_3 FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
  CONSTRAINT treatmentsample_ibfk_4 FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS msanalysis (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  source varchar(50) DEFAULT NULL,
  massDetectionInstrument varchar(50) NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType varchar(50) DEFAULT NULL,
  deletionExplanation clob,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS acquisition (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  msAnalysisId bigint(20) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  numberOfAcquisition int(11) DEFAULT NULL,
  sampleListName varchar(255) DEFAULT NULL,
  acquisitionFile varchar(255) DEFAULT NULL,
  position int(11) NOT NULL,
  listIndex int(11) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (sampleId,position),
  CONSTRAINT acquisition_ibfk_1 FOREIGN KEY (msAnalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT acquisition_ibfk_2 FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT acquisition_ibfk_3 FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS acquisitionFile ON acquisition (acquisitionFile,sampleId);
CREATE TABLE IF NOT EXISTS dataanalysis (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  sampleId bigint(20) NOT NULL,
  protein varchar(255) NOT NULL,
  peptide varchar(255) DEFAULT NULL,
  maxWorkTime double NOT NULL,
  score clob,
  workTime double DEFAULT NULL,
  status varchar(50) NOT NULL,
  analysisType varchar(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT dataanalysis_ibfk_1 FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS activity (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  userId bigint(20) NOT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType varchar(50) NOT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  explanation clob,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS activityRecordIndex ON activity (tableName,recordId,actionType);
CREATE TABLE IF NOT EXISTS activityupdate (
  id bigint(20) NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  activityId bigint(20) DEFAULT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType varchar(50) NOT NULL,
  actionColumn varchar(70) DEFAULT NULL,
  oldValue varchar(255) DEFAULT NULL,
  newValue varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT activityupdate_ibfk_1 FOREIGN KEY (activityId) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE
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
  preferenceId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  value blob DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (preferenceId, userId),
  CONSTRAINT userpreference_ibfk_1 FOREIGN KEY (preferenceId) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT userpreference_ibfk_2 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
