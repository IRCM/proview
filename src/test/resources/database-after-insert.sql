ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainer_ibfk_2 FOREIGN KEY (sampleId) REFERENCES sample (id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE samplecontainer
ADD CONSTRAINT samplecontainer_ibfk_3 FOREIGN KEY (treatmentSampleId) REFERENCES treatmentsample (id) ON DELETE SET NULL ON UPDATE CASCADE;
