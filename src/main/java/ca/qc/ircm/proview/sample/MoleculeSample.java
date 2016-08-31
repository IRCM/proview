package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Small molecules sample submitted for MS analysis.
 */
@Entity
@DiscriminatorValue("SUBMISSION_MOLECULE")
public class MoleculeSample extends SubmissionSample {
  /**
   * Available storage temperatures.
   */
  public static enum StorageTemperature {
    MEDIUM(4), LOW(-20);
    StorageTemperature(int temperature) {
      this.temperature = temperature;
    }

    /**
     * Real temperature of this enum.
     */
    private int temperature;

    public int getTemperature() {
      return temperature;
    }
  }

  private static final long serialVersionUID = 7687421145452081574L;

  /**
   * Molecule Formula.
   */
  @Column(name = "formula")
  @Size(max = 50)
  private String formula;
  /**
   * Monoisotopic mass.
   */
  @Column(name = "monoisotopicMass")
  @Min(0)
  private Double monoisotopicMass;
  /**
   * Average mass.
   */
  @Column(name = "averageMass")
  @Min(0)
  private Double averageMass;
  /**
   * Solvent in which the sample is in. (Solution sample only)
   */
  @Column(name = "solutionSolvent")
  @Size(max = 100)
  private String solutionSolvent;
  /**
   * Other solvent value for Solvent.OTHER in solventList.
   */
  @Column(name = "otherSolvent")
  @Size(max = 100)
  private String otherSolvent;
  /**
   * Toxicity of sample.
   */
  @Column(name = "toxicity")
  @Size(max = 100)
  private String toxicity;
  /**
   * Sample is sensitive to light.
   */
  @Column(name = "lightSensitive", nullable = false)
  private boolean lightSensitive = false;
  /**
   * Storage temperature.
   */
  @Column(name = "storageTemperature")
  @Enumerated(STRING)
  private StorageTemperature storageTemperature;
  /**
   * Solvents that our lab uses that can solubilise sample.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "sampleId")
  private List<SampleSolvent> solventList;
  /**
   * Support for this molecule.
   */
  @Column(name = "support")
  @Enumerated(STRING)
  private Support support;
  /**
   * Low resolution mass detection for sample.
   */
  @Column(name = "lowResolution", nullable = false)
  private boolean lowResolution;
  /**
   * High resolution mass detection for sample.
   */
  @Column(name = "highResolution", nullable = false)
  private boolean highResolution;
  /**
   * True if Small molecule should have an msms analysis.
   */
  @Column(name = "msms", nullable = false)
  private boolean msms;
  /**
   * True if Small molecule should have an exact msms analysis. High resolution mass detection ir
   * required for this.
   */
  @Column(name = "exactMsms", nullable = false)
  private boolean exactMsms;
  /**
   * Molecule structure.
   */
  @OneToOne(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "structureId")
  private Structure structure;

  public MoleculeSample() {
    super();
  }

  public MoleculeSample(Long id) {
    super(id);
  }

  public MoleculeSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public Support getSupport() {
    return support;
  }

  @Override
  public ServiceType getServiceType() {
    return ServiceType.SMALL_MOLECULE;
  }

  @Override
  public Type getType() {
    return Type.SUBMISSION;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public boolean isLightSensitive() {
    return lightSensitive;
  }

  public void setLightSensitive(boolean lightSensitive) {
    this.lightSensitive = lightSensitive;
  }

  public String getOtherSolvent() {
    return otherSolvent;
  }

  public void setOtherSolvent(String otherSolvent) {
    this.otherSolvent = otherSolvent;
  }

  public List<SampleSolvent> getSolventList() {
    return solventList;
  }

  public void setSolventList(List<SampleSolvent> solventList) {
    this.solventList = solventList;
  }

  public StorageTemperature getStorageTemperature() {
    return storageTemperature;
  }

  public void setStorageTemperature(StorageTemperature storageTemperature) {
    this.storageTemperature = storageTemperature;
  }

  public String getToxicity() {
    return toxicity;
  }

  public void setToxicity(String toxicity) {
    this.toxicity = toxicity;
  }

  public Double getAverageMass() {
    return averageMass;
  }

  public void setAverageMass(Double averageMass) {
    this.averageMass = averageMass;
  }

  public Double getMonoisotopicMass() {
    return monoisotopicMass;
  }

  public void setMonoisotopicMass(Double monoisotopicMass) {
    this.monoisotopicMass = monoisotopicMass;
  }

  public String getSolutionSolvent() {
    return solutionSolvent;
  }

  public void setSolutionSolvent(String solutionSolvent) {
    this.solutionSolvent = solutionSolvent;
  }

  public void setSupport(Support support) {
    this.support = support;
  }

  public boolean isLowResolution() {
    return lowResolution;
  }

  public void setLowResolution(boolean lowResolution) {
    this.lowResolution = lowResolution;
  }

  public boolean isHighResolution() {
    return highResolution;
  }

  public void setHighResolution(boolean highResolution) {
    this.highResolution = highResolution;
  }

  public boolean isMsms() {
    return msms;
  }

  public void setMsms(boolean msms) {
    this.msms = msms;
  }

  public boolean isExactMsms() {
    return exactMsms;
  }

  public void setExactMsms(boolean exactMsms) {
    this.exactMsms = exactMsms;
  }

  public Structure getStructure() {
    return structure;
  }

  public void setStructure(Structure structure) {
    this.structure = structure;
  }
}
