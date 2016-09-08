CREATE TABLE IF NOT EXISTS laboratory (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  organization varchar(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  line varchar(200) NOT NULL,
  town varchar(50) NOT NULL,
  state varchar(50) NOT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) DEFAULT NULL,
  salt varchar(255) DEFAULT NULL,
  passwordVersion int(10) DEFAULT NULL,
  locale varchar(50) DEFAULT NULL,
  addressId bigint(20) DEFAULT NULL,
  active tinyint(1) NOT NULL DEFAULT '0',
  valid tinyint(1) NOT NULL DEFAULT '0',
  admin tinyint(1) NOT NULL DEFAULT '0',
  registerTime datetime DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY usersEmail (email),
  FOREIGN KEY (addressId) REFERENCES address (id) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS phonenumber (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) DEFAULT NULL,
  type varchar(255) NOT NULL,
  number varchar(50) NOT NULL,
  extension varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratoryuser (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS laboratorymanager (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (userId, laboratoryId),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS forgotpassword (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  requestMoment timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  confirmNumber int(11) NOT NULL,
  used tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS submission (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  submissionDate timestamp NOT NULL,
  laboratoryId bigint(20) NOT NULL,
  userId bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (laboratoryId) REFERENCES laboratory (id) ON UPDATE CASCADE,
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS gelimages (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  submissionId bigint(20) DEFAULT NULL,
  filename varchar(255) NOT NULL,
  content longblob,
  PRIMARY KEY (id),
  FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS structure (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  filename varchar(255) NOT NULL,
  content longblob NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS plate (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  type varchar(50) NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY plateName (name),
  KEY plateType (type)
);
CREATE TABLE IF NOT EXISTS samplecontainer (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  type varchar(50) NOT NULL,
  name varchar(100) DEFAULT NULL,
  plateId bigint(20) DEFAULT NULL,
  locationColumn int(11) DEFAULT NULL,
  locationRow int(11) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  treatmentSampleId bigint(20) DEFAULT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  banned tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY samplecontainerName (name),
  UNIQUE KEY samplecontainerPlateId (plateId,locationColumn,locationRow),
  FOREIGN KEY (plateId) REFERENCES plate (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS sample (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  lims varchar(100) DEFAULT NULL,
  name varchar(150) DEFAULT NULL,
  serviceType varchar(50) DEFAULT NULL,
  controlType varchar(50) DEFAULT NULL,
  support varchar(50) DEFAULT NULL,
  containerId bigint(20) DEFAULT NULL,
  comments text,
  status varchar(50),
  project varchar(100) DEFAULT NULL,
  experience varchar(100) DEFAULT NULL,
  goal varchar(150) DEFAULT NULL,
  source varchar(50) DEFAULT NULL,
  sampleNumberProtein int(11) DEFAULT NULL,
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
  submissionId bigint(20) DEFAULT NULL,
  mudPitFraction varchar(50) DEFAULT NULL,
  proteinContent varchar(50) DEFAULT NULL,
  massDetectionInstrument varchar(50) DEFAULT NULL,
  service varchar(50) DEFAULT NULL,
  price double DEFAULT NULL,
  additionalPrice double DEFAULT NULL,
  taxonomy varchar(100) DEFAULT NULL,
  protein varchar(100) DEFAULT NULL,
  molecularWeight double DEFAULT NULL,
  postTranslationModification varchar(150) DEFAULT NULL,
  separation varchar(50) DEFAULT NULL,
  thickness varchar(50) DEFAULT NULL,
  coloration varchar(50) DEFAULT NULL,
  otherColoration varchar(100) DEFAULT NULL,
  developmentTime varchar(100) DEFAULT NULL,
  decoloration tinyint(1) NOT NULL DEFAULT '0',
  weightMarkerQuantity double DEFAULT NULL,
  proteinQuantity varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  volume double DEFAULT NULL,
  formula varchar(50) DEFAULT NULL,
  monoisotopicMass double DEFAULT NULL,
  averageMass double DEFAULT NULL,
  solutionSolvent varchar(100) DEFAULT NULL,
  otherSolvent varchar(100) DEFAULT NULL,
  toxicity varchar(100) DEFAULT NULL,
  lightSensitive tinyint(1) NOT NULL DEFAULT '0',
  storageTemperature varchar(50) DEFAULT NULL,
  structureId bigint(20) DEFAULT NULL,
  sampleType varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY sampleLims (lims),
  KEY sampleName (name),
  FOREIGN KEY (submissionId) REFERENCES submission (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (structureId) REFERENCES structure (id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON DELETE CASCADE ON UPDATE CASCADE
);
ALTER TABLE samplecontainer
ADD FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE TABLE IF NOT EXISTS standard (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comments text,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS contaminant (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  sampleId bigint(20) DEFAULT NULL,
  comments text,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS solvent (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  sampleId bigint(20) DEFAULT NULL,
  solvent varchar(150) NOT NULL,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS protocol (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY protocolName (name)
);
CREATE TABLE IF NOT EXISTS treatment (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  type varchar(100) DEFAULT NULL,
  protocolId bigint(20) DEFAULT NULL,
  fractionationType varchar(50) DEFAULT NULL,
  userId bigint(20) DEFAULT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType enum('ERRONEOUS','FAILED') DEFAULT NULL,
  deletionJustification text,
  PRIMARY KEY (id),
  FOREIGN KEY (protocolId) REFERENCES protocol (id) ON UPDATE CASCADE,
  FOREIGN KEY (userId) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS treatmentsample (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  treatmentId bigint(20) DEFAULT NULL,
  treatmentType varchar(100) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  destinationContainerId bigint(20) DEFAULT NULL,
  comments text,
  sourceVolume double DEFAULT NULL,
  solvent varchar(255) DEFAULT NULL,
  solventVolume double DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  quantity varchar(100) DEFAULT NULL,
  position int(11) DEFAULT NULL,
  number int(11) DEFAULT NULL,
  piInterval varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (treatmentId) REFERENCES treatment (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE,
  FOREIGN KEY (destinationContainerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
ALTER TABLE samplecontainer
ADD FOREIGN KEY (treatmentSampleId) REFERENCES treatmentsample (id) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE TABLE IF NOT EXISTS msanalysis (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  source varchar(50) DEFAULT NULL,
  massDetectionInstrument varchar(50) NOT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(4) NOT NULL DEFAULT '0',
  deletionType enum('ERRONEOUS','FAILED') DEFAULT NULL,
  deletionJustification text,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS msanalysisverification (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  msAnalysisId bigint(20) DEFAULT NULL,
  verificationType varchar(50) NOT NULL,
  verificationName varchar(255) NOT NULL,
  verificationValue tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  FOREIGN KEY (msAnalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS acquisition (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  msAnalysisId bigint(20) DEFAULT NULL,
  sampleId bigint(20) NOT NULL,
  containerId bigint(20) DEFAULT NULL,
  numberOfAcquisition int(11) DEFAULT NULL,
  sampleListName varchar(255) DEFAULT NULL,
  acquisitionFile varchar(255) DEFAULT NULL,
  position int(11) NOT NULL,
  listIndex int(11) NOT NULL,
  comments varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE (sampleId,position),
  KEY acquisitionFile (acquisitionFile,sampleId),
  FOREIGN KEY (msAnalysisId) REFERENCES msanalysis (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON UPDATE CASCADE,
  FOREIGN KEY (containerId) REFERENCES samplecontainer (id) ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS mascotfile (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  server varchar(50) NOT NULL,
  name varchar(50) NOT NULL,
  searchDate datetime NOT NULL,
  location varchar(255) NOT NULL,
  rawFile varchar(255) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  insertTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY mascotfileunique (server,name,searchDate),
  KEY mascotfileRawFile (rawFile)
);
CREATE TABLE IF NOT EXISTS acquisition_to_mascotfile (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  acquisitionId bigint(20) NOT NULL,
  mascotFileId bigint(20) NOT NULL,
  visible tinyint(4) NOT NULL DEFAULT '1',
  comments varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY acquisition_to_mascotfileAcquisitionId (acquisitionId,mascotFileId),
  FOREIGN KEY (acquisitionId) REFERENCES acquisition (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (mascotFileId) REFERENCES mascotfile (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS dataanalysis (
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
  FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS activity (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userId bigint(20) NOT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType enum('INSERT','UPDATE','DELETE') NOT NULL,
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  justification text,
  PRIMARY KEY (id),
  KEY activityRecordIndex (tableName,recordId,actionType)
);
CREATE TABLE IF NOT EXISTS activityupdate (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  activityId bigint(20) DEFAULT NULL,
  tableName varchar(50) NOT NULL,
  recordId bigint(20) NOT NULL,
  actionType enum('INSERT','UPDATE','DELETE') NOT NULL,
  actionColumn varchar(70) DEFAULT NULL,
  oldValue varchar(255) DEFAULT NULL,
  newValue varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (activityId) REFERENCES activity (id) ON DELETE CASCADE ON UPDATE CASCADE
);
