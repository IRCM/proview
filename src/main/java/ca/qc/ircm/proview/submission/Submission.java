package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.UsedBy.HIBERNATE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.UsedBy;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryData;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.Nullable;

/**
 * Sample Submission of a User.
 */
@Entity
@Table(name = Submission.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = ENTITY_EI_EXPOSE_REP)
public class Submission implements Data, Named, LaboratoryData, Serializable {

  public static final String TABLE_NAME = "submission";
  @Serial
  private static final long serialVersionUID = 2223809698076034086L;

  /**
   * Database identifier of this Submission.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Version number.
   */
  @Version
  @Column(nullable = false)
  private int version;
  /**
   * Service chosen.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private Service service;
  /**
   * Taxonomy of Sample content.
   */
  @Column
  @Size(max = 100)
  private String taxonomy;
  /**
   * User's experiment.
   */
  @Column(nullable = false)
  @Size(max = 100)
  private String experiment;
  /**
   * Experiment's goal.
   */
  @Column
  @Size(max = 150)
  private String goal;
  /**
   * Mass detection instrument.
   */
  @Column
  @Enumerated(STRING)
  private MassDetectionInstrument instrument;
  /**
   * Intact protein ionization source for mass detection instrument.
   */
  @Column
  @Enumerated(STRING)
  private MassDetectionInstrumentSource source;
  /**
   * Injection type.
   */
  @Column
  @Enumerated(STRING)
  private InjectionType injectionType;
  /**
   * Proteolytic digestion method.
   */
  @Column
  @Enumerated(STRING)
  private ProteolyticDigestion digestion;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#DIGESTED DIGESTED}.
   *
   * @see ProteolyticDigestion
   */
  @Column
  @Size(max = 100)
  private String usedDigestion;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#OTHER OTHER}.
   *
   * @see ProteolyticDigestion
   */
  @Column
  @Size(max = 100)
  private String otherDigestion;
  /**
   * Database on which Protein identification will be made.
   */
  @Column
  @Enumerated(STRING)
  private ProteinIdentification identification;
  /**
   * Link to non-conventional Protein identification database.
   */
  @Column
  @Size(max = 255)
  private String identificationLink;
  /**
   * High resolution mass detection for sample.
   */
  @Column(nullable = false)
  private boolean highResolution;
  /**
   * Number of Proteins in samples.
   */
  @Column
  @Enumerated(STRING)
  private ProteinContent proteinContent;
  /**
   * Protein that should be found in Sample.
   */
  @Column
  @Size(max = 100)
  private String protein;
  /**
   * Post-translationnal modification to Protein.
   */
  @Column
  @Size(max = 150)
  private String postTranslationModification;
  /**
   * Gel separation.
   */
  @Column
  @Enumerated(STRING)
  private GelSeparation separation;
  /**
   * Gel thickness.
   */
  @Column
  @Enumerated(STRING)
  private GelThickness thickness;
  /**
   * Gel coloration.
   */
  @Column
  @Enumerated(STRING)
  private GelColoration coloration;
  /**
   * Other gel coloration (if any).
   */
  @Column
  @Size(max = 100)
  private String otherColoration;
  /**
   * Gel development time (for coloration).
   */
  @Column
  @Size(max = 100)
  private String developmentTime;
  /**
   * Gel decoloration.
   */
  @Column(nullable = false)
  private boolean decoloration = false;
  /**
   * Quantity of weight marker.
   */
  @Column
  private Double weightMarkerQuantity;
  /**
   * Quantity of proteins in gel (total).
   */
  @Column
  @Size(max = 100)
  private String proteinQuantity;
  /**
   * Molecule Formula.
   */
  @Column
  @Size(max = 50)
  private String formula;
  /**
   * Monoisotopic mass.
   */
  @Column
  @Min(0)
  private Double monoisotopicMass;
  /**
   * Average mass.
   */
  @Column
  @Min(0)
  private Double averageMass;
  /**
   * Solvent in which the sample is in. (Solution sample only)
   */
  @Column
  @Size(max = 100)
  private String solutionSolvent;
  /**
   * Solvents that our lab uses that can solubilise sample.
   */
  @ElementCollection(targetClass = Solvent.class)
  @CollectionTable(name = "solvent")
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private List<Solvent> solvents;
  /**
   * Other solvent value for Solvent.OTHER in solventList.
   */
  @Column
  @Size(max = 100)
  private String otherSolvent;
  /**
   * Toxicity of sample.
   */
  @Column
  @Size(max = 100)
  private String toxicity;
  /**
   * Sample is sensitive to light.
   */
  @Column(nullable = false)
  private boolean lightSensitive = false;
  /**
   * Storage temperature.
   */
  @Column
  @Enumerated(STRING)
  private StorageTemperature storageTemperature;
  /**
   * Quantification.
   */
  @Column
  @Enumerated(STRING)
  private Quantification quantification;
  /**
   * Quantification comments (labels for silac).
   */
  @Column
  private String quantificationComment;
  /**
   * Contaminants.
   */
  @Column
  private String contaminants;
  /**
   * Standards.
   */
  @Column
  private String standards;
  /**
   * Any comment on this sample.
   */
  @Column
  private String comment;
  /**
   * Date of submission.
   */
  @Column
  private LocalDateTime submissionDate;
  /**
   * Sample delivery date.
   */
  @Column
  private LocalDate sampleDeliveryDate;
  /**
   * Digestion date.
   */
  @Column
  private LocalDate digestionDate;
  /**
   * Analysis date.
   */
  @Column
  private LocalDate analysisDate;
  /**
   * Data available date.
   */
  @Column
  private LocalDate dataAvailableDate;
  /**
   * True if submission is hidden.
   */
  @Column
  private boolean hidden;
  /**
   * User who made the Submission.
   */
  @ManyToOne
  @JoinColumn
  private User user;
  /**
   * Laboratory who made the Submission.
   */
  @ManyToOne
  @JoinColumn
  private Laboratory laboratory;
  /**
   * Samples that are part of this submission.
   */
  @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderColumn(name = "listIndex")
  private List<SubmissionSample> samples;
  /**
   * Additional files related to submission.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn
  private List<SubmissionFile> files;

  public Submission() {
  }

  public Submission(long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Submission [id=" + id + ", service=" + service + ", experiment=" + experiment + "]";
  }

  @Override
  public String getName() {
    return experiment;
  }

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public Laboratory getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(LocalDateTime submissionDate) {
    this.submissionDate = submissionDate;
  }

  public List<SubmissionSample> getSamples() {
    return samples;
  }

  public void setSamples(List<SubmissionSample> samples) {
    this.samples = samples;
  }

  public String getExperiment() {
    return experiment;
  }

  public void setExperiment(String experiment) {
    this.experiment = experiment;
  }

  @Nullable
  public String getGoal() {
    return goal;
  }

  public void setGoal(@Nullable String goal) {
    this.goal = goal;
  }

  @Nullable
  public String getComment() {
    return comment;
  }

  public void setComment(@Nullable String comment) {
    this.comment = comment;
  }

  @Nullable
  public MassDetectionInstrumentSource getSource() {
    return source;
  }

  public void setSource(@Nullable MassDetectionInstrumentSource source) {
    this.source = source;
  }

  public boolean isHighResolution() {
    return highResolution;
  }

  public void setHighResolution(boolean highResolution) {
    this.highResolution = highResolution;
  }

  @Nullable
  public ProteolyticDigestion getDigestion() {
    return digestion;
  }

  public void setDigestion(@Nullable ProteolyticDigestion digestion) {
    this.digestion = digestion;
  }

  @Nullable
  public String getUsedDigestion() {
    return usedDigestion;
  }

  public void setUsedDigestion(@Nullable String usedDigestion) {
    this.usedDigestion = usedDigestion;
  }

  @Nullable
  public String getOtherDigestion() {
    return otherDigestion;
  }

  public void setOtherDigestion(@Nullable String otherDigestion) {
    this.otherDigestion = otherDigestion;
  }

  @Nullable
  public ProteinIdentification getIdentification() {
    return identification;
  }

  public void setIdentification(@Nullable ProteinIdentification identification) {
    this.identification = identification;
  }

  @Nullable
  public String getIdentificationLink() {
    return identificationLink;
  }

  public void setIdentificationLink(@Nullable String identificationLink) {
    this.identificationLink = identificationLink;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  @Nullable
  public MassDetectionInstrument getInstrument() {
    return instrument;
  }

  public void setInstrument(@Nullable MassDetectionInstrument instrument) {
    this.instrument = instrument;
  }

  @Nullable
  public ProteinContent getProteinContent() {
    return proteinContent;
  }

  public void setProteinContent(@Nullable ProteinContent proteinContent) {
    this.proteinContent = proteinContent;
  }

  @Nullable
  public String getPostTranslationModification() {
    return postTranslationModification;
  }

  public void setPostTranslationModification(@Nullable String postTranslationModification) {
    this.postTranslationModification = postTranslationModification;
  }

  @Nullable
  public String getProtein() {
    return protein;
  }

  public void setProtein(@Nullable String protein) {
    this.protein = protein;
  }

  @Nullable
  public String getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(@Nullable String taxonomy) {
    this.taxonomy = taxonomy;
  }

  @Nullable
  public String getProteinQuantity() {
    return proteinQuantity;
  }

  public void setProteinQuantity(@Nullable String proteinQuantity) {
    this.proteinQuantity = proteinQuantity;
  }

  @Nullable
  public GelSeparation getSeparation() {
    return separation;
  }

  public void setSeparation(@Nullable GelSeparation separation) {
    this.separation = separation;
  }

  @Nullable
  public GelThickness getThickness() {
    return thickness;
  }

  public void setThickness(@Nullable GelThickness thickness) {
    this.thickness = thickness;
  }

  @Nullable
  public GelColoration getColoration() {
    return coloration;
  }

  public void setColoration(@Nullable GelColoration coloration) {
    this.coloration = coloration;
  }

  @Nullable
  public String getOtherColoration() {
    return otherColoration;
  }

  public void setOtherColoration(@Nullable String otherColoration) {
    this.otherColoration = otherColoration;
  }

  public boolean isDecoloration() {
    return decoloration;
  }

  public void setDecoloration(boolean decoloration) {
    this.decoloration = decoloration;
  }

  @Nullable
  public Double getWeightMarkerQuantity() {
    return weightMarkerQuantity;
  }

  public void setWeightMarkerQuantity(@Nullable Double weightMarkerQuantity) {
    this.weightMarkerQuantity = weightMarkerQuantity;
  }

  @Nullable
  public String getDevelopmentTime() {
    return developmentTime;
  }

  public void setDevelopmentTime(@Nullable String developmentTime) {
    this.developmentTime = developmentTime;
  }

  @Nullable
  public String getFormula() {
    return formula;
  }

  public void setFormula(@Nullable String formula) {
    this.formula = formula;
  }

  public boolean isLightSensitive() {
    return lightSensitive;
  }

  public void setLightSensitive(boolean lightSensitive) {
    this.lightSensitive = lightSensitive;
  }

  @Nullable
  public String getOtherSolvent() {
    return otherSolvent;
  }

  public void setOtherSolvent(@Nullable String otherSolvent) {
    this.otherSolvent = otherSolvent;
  }

  @Nullable
  public StorageTemperature getStorageTemperature() {
    return storageTemperature;
  }

  public void setStorageTemperature(@Nullable StorageTemperature storageTemperature) {
    this.storageTemperature = storageTemperature;
  }

  @Nullable
  public String getToxicity() {
    return toxicity;
  }

  public void setToxicity(@Nullable String toxicity) {
    this.toxicity = toxicity;
  }

  @Nullable
  public Double getAverageMass() {
    return averageMass;
  }

  public void setAverageMass(@Nullable Double averageMass) {
    this.averageMass = averageMass;
  }

  @Nullable
  public Double getMonoisotopicMass() {
    return monoisotopicMass;
  }

  public void setMonoisotopicMass(@Nullable Double monoisotopicMass) {
    this.monoisotopicMass = monoisotopicMass;
  }

  @Nullable
  public String getSolutionSolvent() {
    return solutionSolvent;
  }

  public void setSolutionSolvent(@Nullable String solutionSolvent) {
    this.solutionSolvent = solutionSolvent;
  }

  public List<Solvent> getSolvents() {
    return solvents;
  }

  public void setSolvents(List<Solvent> solvents) {
    this.solvents = solvents;
  }

  @Nullable
  public Quantification getQuantification() {
    return quantification;
  }

  public void setQuantification(@Nullable Quantification quantification) {
    this.quantification = quantification;
  }

  @Nullable
  public String getQuantificationComment() {
    return quantificationComment;
  }

  public void setQuantificationComment(@Nullable String quantificationComment) {
    this.quantificationComment = quantificationComment;
  }

  @Nullable
  public InjectionType getInjectionType() {
    return injectionType;
  }

  public void setInjectionType(@Nullable InjectionType injectionType) {
    this.injectionType = injectionType;
  }

  public List<SubmissionFile> getFiles() {
    return files;
  }

  public void setFiles(List<SubmissionFile> files) {
    this.files = files;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  @Nullable
  public LocalDate getDigestionDate() {
    return digestionDate;
  }

  public void setDigestionDate(@Nullable LocalDate digestionDate) {
    this.digestionDate = digestionDate;
  }

  @Nullable
  public LocalDate getAnalysisDate() {
    return analysisDate;
  }

  public void setAnalysisDate(@Nullable LocalDate analysisDate) {
    this.analysisDate = analysisDate;
  }

  @Nullable
  public LocalDate getDataAvailableDate() {
    return dataAvailableDate;
  }

  public void setDataAvailableDate(@Nullable LocalDate dataAvailableDate) {
    this.dataAvailableDate = dataAvailableDate;
  }

  @Nullable
  public LocalDate getSampleDeliveryDate() {
    return sampleDeliveryDate;
  }

  public void setSampleDeliveryDate(@Nullable LocalDate sampleDeliveryDate) {
    this.sampleDeliveryDate = sampleDeliveryDate;
  }

  public int getVersion() {
    return version;
  }

  @UsedBy(HIBERNATE)
  public void setVersion(int version) {
    this.version = version;
  }

  @Nullable
  public String getContaminants() {
    return contaminants;
  }

  public void setContaminants(@Nullable String contaminants) {
    this.contaminants = contaminants;
  }

  @Nullable
  public String getStandards() {
    return standards;
  }

  public void setStandards(@Nullable String standards) {
    this.standards = standards;
  }
}
