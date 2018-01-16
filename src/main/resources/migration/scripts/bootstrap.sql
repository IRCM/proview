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

CREATE TABLE laboratory (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  line varchar(200) NOT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) NOT NULL,
  signAttempts int(10) DEFAULT NULL,
  lastSignAttempt datetime DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  addressId bigint(20) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  admin tinyint(1) NOT NULL DEFAULT '0',
  registerTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY email (email),
  KEY address (addressId),
  CONSTRAINT userAddress_ibfk FOREIGN KEY (addressId) REFERENCES address (id) ON UPDATE CASCADE
);
CREATE TABLE phonenumber (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) DEFAULT NULL,
  type varchar(255) NOT NULL,
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY user (userId),
  CONSTRAINT phonenumberUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratoryuser (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY member (userId,laboratoryId),
  KEY user (userId),
  KEY laboratory (laboratoryId),
  CONSTRAINT laboratoryuserUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratoryuserLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE laboratorymanager (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY manager (userId,laboratoryId),
  KEY user (userId),
  KEY laboratory (laboratoryId),
  CONSTRAINT laboratorymanagerUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT laboratorymanagerLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE forgotpassword (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  requestMoment timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  confirmNumber int(11) NOT NULL,
  used tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY user (userId),
  CONSTRAINT forgotpasswordUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE submission (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  service varchar(50) DEFAULT NULL,
  experience varchar(100) DEFAULT NULL,
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
  quantificationLabels text,
  comment text,
  price double DEFAULT NULL,
  additionalPrice double DEFAULT NULL,
  submissionDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  laboratoryId bigint(20) NOT NULL,
  userId bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY laboratory (laboratoryId),
  KEY user (userId),
  CONSTRAINT submissionUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT submissionLaboratory_ibfk FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON UPDATE CASCADE
);
CREATE TABLE submissionfiles (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  submissionId bigint(20) DEFAULT NULL,
  filename varchar(255) NOT NULL,
  content longblob,
  PRIMARY KEY (id),
  KEY submission (submissionId),
  CONSTRAINT submissionfilesSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE plate (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  columnCount int(11) NOT NULL,
  rowCount int(11) NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
);
CREATE TABLE samplecontainer (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  type varchar(50) NOT NULL,
  name varchar(100) DEFAULT NULL,
  plateId bigint(20) DEFAULT NULL,
  locationColumn int(11) DEFAULT NULL,
  locationRow int(11) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  version int(11) DEFAULT '0',
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  banned tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY name (name),
  UNIQUE KEY plateLocation (plateId,locationColumn,locationRow),
  KEY plate (plateId),
  KEY sample (sampleId),
  CONSTRAINT samplecontainerPlate_ibfk FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE sample (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(150) DEFAULT NULL,
  controlType varchar(50) DEFAULT NULL,
  support varchar(50) DEFAULT NULL,
  containerId bigint(20) DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  numberProtein int(11) DEFAULT NULL,
  submissionId bigint(20) DEFAULT NULL,
  molecularWeight double DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  volume double DEFAULT NULL,
  sampleType varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY name (name),
  KEY submission (submissionId),
  KEY container (containerId),
  CONSTRAINT sampleSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT sampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON DELETE CASCADE ON UPDATE CASCADE
);
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainerSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE TABLE standard (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comment text,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY sample (sampleId),
  CONSTRAINT standardSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE contaminant (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comment text,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY sample (sampleId),
  CONSTRAINT contaminantSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE solvent (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  submissionId bigint(20) DEFAULT NULL,
  solvent varchar(150) NOT NULL,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY submission (submissionId),
  CONSTRAINT solventSubmission_ibfk FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE protocol (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
);
CREATE TABLE treatment (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  type varchar(100) DEFAULT NULL,
  protocolId bigint(20) DEFAULT NULL,
  fractionationType varchar(50) DEFAULT NULL,
  userId bigint(20) DEFAULT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType varchar(50) DEFAULT NULL,
  deletionExplanation text,
  PRIMARY KEY (id),
  KEY protocol (protocolId),
  KEY user (userId),
  CONSTRAINT treatmentProtocol_ibfk FOREIGN KEY (protocolId) REFERENCES protocol (id) ON UPDATE CASCADE,
  CONSTRAINT treatmentUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE treatmentsample (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  treatmentId bigint(20) DEFAULT NULL,
  treatmentType varchar(100) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  destinationContainerId bigint(20) DEFAULT NULL,
  comment text,
  sourceVolume double DEFAULT NULL,
  solvent varchar(255) DEFAULT NULL,
  solventVolume double DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  position int(11) DEFAULT NULL,
  number int(11) DEFAULT NULL,
  piInterval varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY treatment (treatmentId),
  KEY sample (sampleId),
  KEY container (containerId),
  KEY destinationContainer (destinationContainerId),
  CONSTRAINT treatmentsampleTreatment_ibfk FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT treatmentsampleSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT treatmentsampleContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
  CONSTRAINT treatmentsampleDestinationContainer_ibfk FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE TABLE msanalysis (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  source varchar(100) DEFAULT NULL,
  massDetectionInstrument varchar(100) NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType varchar(50) DEFAULT NULL,
  deletionExplanation text,
  PRIMARY KEY (id)
);
CREATE TABLE acquisition (
  id bigint(20) NOT NULL AUTO_INCREMENT,
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
  UNIQUE KEY samplePosition (sampleId,position),
  KEY file (acquisitionFile,sampleId),
  KEY msAnalysis (msAnalysisId),
  KEY sample (sampleId),
  KEY container (containerId),
  CONSTRAINT acquisitionMsAnalysis_ibfk FOREIGN KEY (msAnalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT acquisitionSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  CONSTRAINT acquisitionContainer_ibfk FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE TABLE dataanalysis (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  sampleId bigint(20) NOT NULL,
  protein varchar(255) NOT NULL,
  peptide varchar(255) DEFAULT NULL,
  maxWorkTime double NOT NULL,
  score text,
  workTime double DEFAULT NULL,
  status varchar(50) NOT NULL,
  analysisType varchar(50) NOT NULL,
  PRIMARY KEY (id),
  KEY sample (sampleId),
  CONSTRAINT dataanalysisSample_ibfk FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE activity (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType varchar(50) NOT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  explanation text,
  PRIMARY KEY (id),
  KEY tableName (tableName,recordId,actionType)
);
CREATE TABLE activityupdate (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  activityId bigint(20) DEFAULT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType varchar(50) NOT NULL,
  actionColumn varchar(70) DEFAULT NULL,
  oldValue varchar(255) DEFAULT NULL,
  newValue varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY activity (activityId),
  CONSTRAINT activityupdateActivity_ibfk FOREIGN KEY (activityId) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE preference (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  referer varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY referer (referer,name),
  KEY name (name)
);
CREATE TABLE userpreference (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  preferenceId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  value longblob,
  PRIMARY KEY (id),
  UNIQUE KEY userpreference (preferenceId,userId),
  KEY preference (preferenceId),
  KEY user (userId),
  CONSTRAINT userpreferencePreference_ibfk FOREIGN KEY (preferenceId) REFERENCES preference (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT userpreferenceUser_ibfk FOREIGN KEY (userId) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
);
