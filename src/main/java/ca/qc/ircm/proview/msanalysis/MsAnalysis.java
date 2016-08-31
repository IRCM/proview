package ca.qc.ircm.proview.msanalysis;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

import ca.qc.ircm.proview.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * MS analysis.
 */
@Entity
@Table(name = "msanalysis")
public class MsAnalysis implements Data, Serializable {
  /**
   * Instruments available for protein mass detection.
   */
  public static enum MassDetectionInstrument {
    VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION, LTQ_ORBI_TRAP, Q_TOF, TOF;
  }

  /**
   * Source for mass spectrometer.
   */
  public static enum Source {
    NSI, ESI, LDTD;
  }

  /**
   * Source for mass spectrometer.
   */
  public static enum VerificationType {
    INSTRUMENT, SAMPLE;
  }

  /**
   * Type of errors that forces Digestion to be deleted.
   */
  public static enum DeletionType {
    /**
     * Digestion information was not entered correctly.
     */
    ERRONEOUS, /**
                * Digestion failed due to an experimental problem. An attempt was made to do the
                * digestion but something went wrong.
                */
    FAILED;
  }

  /**
   * Verifications to do before launching MS analysis.
   */
  @SuppressWarnings("checkstyle:linelength")
  private static final Map<MassDetectionInstrument, Map<Source, Map<VerificationType, Set<String>>>> VERIFICATION_LIST;

  static {
    VERIFICATION_LIST =
        new HashMap<MassDetectionInstrument, Map<Source, Map<VerificationType, Set<String>>>>();

    // VELOS.
    Map<Source, Map<VerificationType, Set<String>>> velosList =
        new HashMap<Source, Map<VerificationType, Set<String>>>();
    // VELOS with NSI source.
    {
      Map<VerificationType, Set<String>> velosNsi = new HashMap<VerificationType, Set<String>>();
      velosNsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      velosNsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      velosNsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      velosNsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      velosNsi.get(VerificationType.INSTRUMENT).add("calibration");
      velosNsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      velosNsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      velosNsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      velosNsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      velosNsi.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      velosNsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      velosNsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      velosNsi.get(VerificationType.INSTRUMENT).add("vacuum");
      velosNsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      velosNsi.get(VerificationType.INSTRUMENT).add("spray");
      velosNsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      velosNsi.get(VerificationType.SAMPLE).add("acquisitionFile");
      velosNsi.get(VerificationType.SAMPLE).add("volume");
      velosList.put(Source.NSI, velosNsi);
    }
    // VELOS with ESI source.
    {
      Map<VerificationType, Set<String>> velosEsi = new HashMap<VerificationType, Set<String>>();
      velosEsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      velosEsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      velosEsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      velosEsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      velosEsi.get(VerificationType.INSTRUMENT).add("calibration");
      velosEsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      velosEsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      velosEsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      velosEsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      velosEsi.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      velosEsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      velosEsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      velosEsi.get(VerificationType.INSTRUMENT).add("vacuum");
      velosEsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      velosEsi.get(VerificationType.INSTRUMENT).add("spray");
      velosEsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      velosEsi.get(VerificationType.SAMPLE).add("acquisitionFile");
      velosEsi.get(VerificationType.SAMPLE).add("volume");
      velosList.put(Source.ESI, velosEsi);
    }
    // VELOS with LDTD source.
    {
      Map<VerificationType, Set<String>> velosLdtd = new HashMap<VerificationType, Set<String>>();
      velosLdtd.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      velosLdtd.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      velosLdtd.get(VerificationType.INSTRUMENT).add("diskSpace");
      velosLdtd.get(VerificationType.INSTRUMENT).add("qcPassed");
      velosLdtd.get(VerificationType.INSTRUMENT).add("calibration");
      velosLdtd.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      velosLdtd.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      velosLdtd.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      velosLdtd.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      velosLdtd.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      velosLdtd.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      velosLdtd.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      velosLdtd.get(VerificationType.INSTRUMENT).add("vacuum");
      velosLdtd.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      velosLdtd.get(VerificationType.SAMPLE).add("sampleVsSpot");
      velosLdtd.get(VerificationType.SAMPLE).add("acquisitionFile");
      velosList.put(Source.LDTD, velosLdtd);
    }
    VERIFICATION_LIST.put(MassDetectionInstrument.VELOS, velosList);

    // LTQ_ORBI_TRAP.
    Map<Source, Map<VerificationType, Set<String>>> ltqorbitrapList =
        new HashMap<Source, Map<VerificationType, Set<String>>>();
    // LTQ_ORBI_TRAP with NSI source.
    {
      Map<VerificationType, Set<String>> ltqorbitrapNsi =
          new HashMap<VerificationType, Set<String>>();
      ltqorbitrapNsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      ltqorbitrapNsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("calibration");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("vacuum");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      ltqorbitrapNsi.get(VerificationType.INSTRUMENT).add("spray");
      ltqorbitrapNsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      ltqorbitrapNsi.get(VerificationType.SAMPLE).add("acquisitionFile");
      ltqorbitrapNsi.get(VerificationType.SAMPLE).add("volume");
      ltqorbitrapList.put(Source.NSI, ltqorbitrapNsi);
    }
    // LTQ_ORBI_TRAP with ESI source.
    {
      Map<VerificationType, Set<String>> ltqorbitrapEsi =
          new HashMap<VerificationType, Set<String>>();
      ltqorbitrapEsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      ltqorbitrapEsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("calibration");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("vacuum");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      ltqorbitrapEsi.get(VerificationType.INSTRUMENT).add("spray");
      ltqorbitrapEsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      ltqorbitrapEsi.get(VerificationType.SAMPLE).add("acquisitionFile");
      ltqorbitrapEsi.get(VerificationType.SAMPLE).add("volume");
      ltqorbitrapList.put(Source.ESI, ltqorbitrapEsi);
    }
    // LTQ_ORBI_TRAP with LDTD source.
    {
      Map<VerificationType, Set<String>> ltqorbitrapLdtd =
          new HashMap<VerificationType, Set<String>>();
      ltqorbitrapLdtd.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      ltqorbitrapLdtd.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("diskSpace");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("qcPassed");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("calibration");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("heliumQuantity");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("vacuum");
      ltqorbitrapLdtd.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      ltqorbitrapLdtd.get(VerificationType.SAMPLE).add("sampleVsSpot");
      ltqorbitrapLdtd.get(VerificationType.SAMPLE).add("acquisitionFile");
      ltqorbitrapList.put(Source.LDTD, ltqorbitrapLdtd);
    }
    VERIFICATION_LIST.put(MassDetectionInstrument.LTQ_ORBI_TRAP, ltqorbitrapList);

    // Q_TOF.
    Map<Source, Map<VerificationType, Set<String>>> qtofList =
        new HashMap<Source, Map<VerificationType, Set<String>>>();
    // Q_TOF with NSI source.
    {
      Map<VerificationType, Set<String>> qtofNsi = new HashMap<VerificationType, Set<String>>();
      qtofNsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      qtofNsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      qtofNsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      qtofNsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      qtofNsi.get(VerificationType.INSTRUMENT).add("calibration");
      qtofNsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      qtofNsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      qtofNsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      qtofNsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      qtofNsi.get(VerificationType.INSTRUMENT).add("argonQuantity");
      qtofNsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      qtofNsi.get(VerificationType.INSTRUMENT).add("openGaz");
      qtofNsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      qtofNsi.get(VerificationType.INSTRUMENT).add("vacuum");
      qtofNsi.get(VerificationType.INSTRUMENT).add("collisionEnergy");
      qtofNsi.get(VerificationType.INSTRUMENT).add("mcp");
      qtofNsi.get(VerificationType.INSTRUMENT).add("uncheckedAutoCid");
      qtofNsi.get(VerificationType.INSTRUMENT).add("checkedGhz");
      qtofNsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      qtofNsi.get(VerificationType.INSTRUMENT).add("spray");
      qtofNsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      qtofNsi.get(VerificationType.SAMPLE).add("clMethod");
      qtofNsi.get(VerificationType.SAMPLE).add("msMethod");
      qtofNsi.get(VerificationType.SAMPLE).add("volume");
      qtofList.put(Source.NSI, qtofNsi);
    }
    // Q_TOF with ESI source.
    {
      Map<VerificationType, Set<String>> qtofEsi = new HashMap<VerificationType, Set<String>>();
      qtofEsi.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      qtofEsi.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      qtofEsi.get(VerificationType.INSTRUMENT).add("diskSpace");
      qtofEsi.get(VerificationType.INSTRUMENT).add("qcPassed");
      qtofEsi.get(VerificationType.INSTRUMENT).add("calibration");
      qtofEsi.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      qtofEsi.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      qtofEsi.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      qtofEsi.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      qtofEsi.get(VerificationType.INSTRUMENT).add("argonQuantity");
      qtofEsi.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      qtofEsi.get(VerificationType.INSTRUMENT).add("openGaz");
      qtofEsi.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      qtofEsi.get(VerificationType.INSTRUMENT).add("vacuum");
      qtofEsi.get(VerificationType.INSTRUMENT).add("collisionEnergy");
      qtofEsi.get(VerificationType.INSTRUMENT).add("mcp");
      qtofEsi.get(VerificationType.INSTRUMENT).add("uncheckedAutoCid");
      qtofEsi.get(VerificationType.INSTRUMENT).add("checkedGhz");
      qtofEsi.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      qtofEsi.get(VerificationType.INSTRUMENT).add("spray");
      qtofEsi.get(VerificationType.SAMPLE).add("sampleVsSpot");
      qtofEsi.get(VerificationType.SAMPLE).add("clMethod");
      qtofEsi.get(VerificationType.SAMPLE).add("msMethod");
      qtofEsi.get(VerificationType.SAMPLE).add("volume");
      qtofList.put(Source.ESI, qtofEsi);
    }
    // Q_TOF with LDTD source.
    {
      Map<VerificationType, Set<String>> qtofLdtd = new HashMap<VerificationType, Set<String>>();
      qtofLdtd.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      qtofLdtd.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      qtofLdtd.get(VerificationType.INSTRUMENT).add("diskSpace");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("qcPassed");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("calibration");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("coolerTemperature");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("argonQuantity");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("mobilePhaseQuantity");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("openGaz");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("lcPumpPressure");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("vacuum");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("collisionEnergy");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("mcp");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("uncheckedAutoCid");
      qtofLdtd.get(VerificationType.INSTRUMENT).add("drainingVialVolume");
      qtofLdtd.get(VerificationType.SAMPLE).add("sampleVsSpot");
      qtofLdtd.get(VerificationType.SAMPLE).add("clMethod");
      qtofLdtd.get(VerificationType.SAMPLE).add("msMethod");
      qtofList.put(Source.LDTD, qtofLdtd);
    }
    VERIFICATION_LIST.put(MassDetectionInstrument.Q_TOF, qtofList);

    // TOF.
    Map<Source, Map<VerificationType, Set<String>>> tofList =
        new HashMap<Source, Map<VerificationType, Set<String>>>();
    // TOF with no source.
    {
      Map<VerificationType, Set<String>> tofNull = new HashMap<VerificationType, Set<String>>();
      tofNull.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      tofNull.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
      tofNull.get(VerificationType.INSTRUMENT).add("diskSpace");
      tofNull.get(VerificationType.INSTRUMENT).add("qcPassed");
      tofNull.get(VerificationType.INSTRUMENT).add("calibration");
      tofNull.get(VerificationType.INSTRUMENT).add("sonicatedPlaque");
      tofNull.get(VerificationType.INSTRUMENT).add("plaquePositionOnAutoSampler");
      tofNull.get(VerificationType.INSTRUMENT).add("nitrogenQuantity");
      tofNull.get(VerificationType.INSTRUMENT).add("vacuum");
      tofNull.get(VerificationType.INSTRUMENT).add("mcp");
      tofNull.get(VerificationType.INSTRUMENT).add("uncheckedAutoCid");
      tofNull.get(VerificationType.INSTRUMENT).add("checkedGhz");
      tofNull.get(VerificationType.INSTRUMENT).add("spray");
      tofNull.get(VerificationType.SAMPLE).add("sampleVsSpot");
      tofNull.get(VerificationType.SAMPLE).add("msMethod");
      tofList.put(null, tofNull);
    }
    VERIFICATION_LIST.put(MassDetectionInstrument.TOF, tofList);
  }

  private static final long serialVersionUID = 7334138327920441104L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Mass detection instrument used in this MS analysis.
   */
  @Column(name = "massDetectionInstrument", nullable = false)
  @Enumerated(STRING)
  private MassDetectionInstrument massDetectionInstrument;
  /**
   * Selected Source for mass detection instrument.
   */
  @Column(name = "source")
  @Enumerated(STRING)
  private Source source;
  /**
   * Time when analysis was inserted.
   */
  @Column(name = "insertTime", nullable = false)
  @Temporal(TIMESTAMP)
  private Date insertTime;
  /**
   * True if MS analysis was deleted.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;
  /**
   * Type of error that forces MS analysis to be deleted.
   */
  @Column(name = "deletionType")
  @Enumerated(STRING)
  private DeletionType deletionType;
  /**
   * Description of what caused the MS analysis to be deleted.
   */
  @Column(name = "deletionJustification")
  private String deletionJustification;
  /**
   * Acquisitions of samples made in this MS analysis.
   */
  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "msAnalysisId", nullable = false)
  private List<Acquisition> acquisitions;
  /**
   * Description of what caused the MS analysis to be deleted.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "msAnalysisId", nullable = false)
  private List<MsAnalysisVerification> verifications;

  public MsAnalysis() {
  }

  public MsAnalysis(Long id) {
    this.id = id;
  }

  /**
   * Returns a list of all checks that must be performed in order to be a valid MS analysis.
   *
   * @param instrument
   *          mass detection instrument to use
   * @param source
   *          source to use
   * @return all checks that must be performed in order to be a valid MS analysis
   */
  @Deprecated
  public static Map<VerificationType, Set<String>> verifications(MassDetectionInstrument instrument,
      Source source) {
    if (instrument == MassDetectionInstrument.TOF) {
      // No source for TOF.
      source = null;
    }
    Map<Source, Map<VerificationType, Set<String>>> instrumentChecks =
        VERIFICATION_LIST.get(instrument);
    Map<VerificationType, Set<String>> sourceChecks =
        instrumentChecks != null ? instrumentChecks.get(source) : null;
    if (sourceChecks == null) {
      sourceChecks = new HashMap<VerificationType, Set<String>>();
      sourceChecks.put(VerificationType.INSTRUMENT, new LinkedHashSet<String>());
      sourceChecks.put(VerificationType.SAMPLE, new LinkedHashSet<String>());
    }
    return sourceChecks;
  }

  /**
   * Returns a list of all checks that must be performed in order to be a valid MS analysis.
   *
   * @return all checks that must be performed in order to be a valid MS analysis
   */
  @Deprecated
  public Map<VerificationType, Set<String>> verifications() {
    return verifications(this.getMassDetectionInstrument(), this.getSource());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof MsAnalysis) {
      MsAnalysis other = (MsAnalysis) obj;
      return this.id != null && this.id.equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id == null ? 0 : this.id.intValue();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("MSAnalysis(");
    buff.append(id);
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public MassDetectionInstrument getMassDetectionInstrument() {
    return massDetectionInstrument;
  }

  public void setMassDetectionInstrument(MassDetectionInstrument massDetectionInstrument) {
    this.massDetectionInstrument = massDetectionInstrument;
  }

  public Date getInsertTime() {
    return insertTime != null ? (Date) insertTime.clone() : null;
  }

  public void setInsertTime(Date insertTime) {
    this.insertTime = insertTime != null ? (Date) insertTime.clone() : null;
  }

  public DeletionType getDeletionType() {
    return deletionType;
  }

  public void setDeletionType(DeletionType deletionType) {
    this.deletionType = deletionType;
  }

  public String getDeletionJustification() {
    return deletionJustification;
  }

  public void setDeletionJustification(String deletionJustification) {
    this.deletionJustification = deletionJustification;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public List<Acquisition> getAcquisitions() {
    return acquisitions;
  }

  public void setAcquisitions(List<Acquisition> acquisitions) {
    this.acquisitions = acquisitions;
  }

  public List<MsAnalysisVerification> getVerifications() {
    return verifications;
  }

  public void setVerifications(List<MsAnalysisVerification> verifications) {
    this.verifications = verifications;
  }
}
