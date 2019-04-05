-- // remove unused dataanalysis table
-- Migration SQL that makes the change goes here.

DROP TABLE dataanalysis;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE dataanalysis (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  sample_id bigint(20) NOT NULL,
  protein varchar(255) NOT NULL,
  peptide varchar(255) DEFAULT NULL,
  maxworktime double NOT NULL,
  score text,
  worktime double DEFAULT NULL,
  status varchar(50) NOT NULL,
  type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  KEY sample (sample_id),
  CONSTRAINT dataanalysis_sample_ibfk FOREIGN KEY (sample_id) REFERENCES sample (id) ON DELETE CASCADE ON UPDATE CASCADE
);
