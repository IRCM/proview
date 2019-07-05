/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.submission;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Sample Submission of a User.
 */
@Entity
@Table(name = Submission.TABLE_NAME)
@GeneratePropertyNames
public class Submission implements Data, Named, LaboratoryData, Serializable {
  public static final String TABLE_NAME = "submission";
  private static final long serialVersionUID = 2223809698076034086L;

  /**
   * Database identifier of this Submission.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
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
  @Column
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
  private MassDetectionInstrument massDetectionInstrument;
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
  private ProteolyticDigestion proteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#DIGESTED DIGESTED}.
   *
   * @see ProteolyticDigestion
   */
  @Column
  @Size(max = 100)
  private String usedProteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#OTHER OTHER}.
   *
   * @see ProteolyticDigestion
   */
  @Column
  @Size(max = 100)
  private String otherProteolyticDigestionMethod;
  /**
   * Database on which Protein identification will be made.
   */
  @Column
  @Enumerated(STRING)
  private ProteinIdentification proteinIdentification;
  /**
   * Link to non-conventional Protein identification database.
   */
  @Column
  @Size(max = 255)
  private String proteinIdentificationLink;
  /**
   * Peptide enrichmentType.
   */
  @Column
  @Enumerated(STRING)
  private EnrichmentType enrichmentType;
  /**
   * Other peptide enrichmentType.
   */
  @Column
  @Size(max = 100)
  private String otherEnrichmentType;
  /**
   * Low resolution mass detection for sample.
   */
  @Column(nullable = false)
  private boolean lowResolution;
  /**
   * High resolution mass detection for sample.
   */
  @Column(nullable = false)
  private boolean highResolution;
  /**
   * True if Small molecule should have an msms analysis.
   */
  @Column(nullable = false)
  private boolean msms;
  /**
   * True if Small molecule should have an exact msms analysis. High resolution mass detection ir
   * required for this.
   */
  @Column(nullable = false)
  private boolean exactMsms;
  /**
   * 2 dimension liquid chromatographie with MudPit.
   */
  @Column
  @Enumerated(STRING)
  private MudPitFraction mudPitFraction;
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
   * Price charged when sample was first submitted.
   */
  @Column
  private BigDecimal price;
  /**
   * Additional price charged by proteomic laboratory members due to change in sample's submission.
   */
  @Column
  private BigDecimal additionalPrice;
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

  public Submission(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Submission [id=" + id + ", service=" + service + ", experiment=" + experiment + "]";
  }

  @Override
  public String getName() {
    if (experiment != null) {
      return experiment;
    } else {
      return !samples.isEmpty() ? samples.get(0).getName() : "";
    }
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public MassDetectionInstrumentSource getSource() {
    return source;
  }

  public void setSource(MassDetectionInstrumentSource source) {
    this.source = source;
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

  public ProteolyticDigestion getProteolyticDigestionMethod() {
    return proteolyticDigestionMethod;
  }

  public void setProteolyticDigestionMethod(ProteolyticDigestion proteolyticDigestionMethod) {
    this.proteolyticDigestionMethod = proteolyticDigestionMethod;
  }

  public String getUsedProteolyticDigestionMethod() {
    return usedProteolyticDigestionMethod;
  }

  public void setUsedProteolyticDigestionMethod(String usedProteolyticDigestionMethod) {
    this.usedProteolyticDigestionMethod = usedProteolyticDigestionMethod;
  }

  public String getOtherProteolyticDigestionMethod() {
    return otherProteolyticDigestionMethod;
  }

  public void setOtherProteolyticDigestionMethod(String otherProteolyticDigestionMethod) {
    this.otherProteolyticDigestionMethod = otherProteolyticDigestionMethod;
  }

  public ProteinIdentification getProteinIdentification() {
    return proteinIdentification;
  }

  public void setProteinIdentification(ProteinIdentification proteinIdentification) {
    this.proteinIdentification = proteinIdentification;
  }

  public String getProteinIdentificationLink() {
    return proteinIdentificationLink;
  }

  public void setProteinIdentificationLink(String proteinIdentificationLink) {
    this.proteinIdentificationLink = proteinIdentificationLink;
  }

  public EnrichmentType getEnrichmentType() {
    return enrichmentType;
  }

  public void setEnrichmentType(EnrichmentType enrichmentType) {
    this.enrichmentType = enrichmentType;
  }

  public String getOtherEnrichmentType() {
    return otherEnrichmentType;
  }

  public void setOtherEnrichmentType(String otherEnrichmentType) {
    this.otherEnrichmentType = otherEnrichmentType;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public MassDetectionInstrument getMassDetectionInstrument() {
    return massDetectionInstrument;
  }

  public void setMassDetectionInstrument(MassDetectionInstrument massDetectionInstrument) {
    this.massDetectionInstrument = massDetectionInstrument;
  }

  public MudPitFraction getMudPitFraction() {
    return mudPitFraction;
  }

  public void setMudPitFraction(MudPitFraction mudPitFraction) {
    this.mudPitFraction = mudPitFraction;
  }

  public ProteinContent getProteinContent() {
    return proteinContent;
  }

  public void setProteinContent(ProteinContent proteinContent) {
    this.proteinContent = proteinContent;
  }

  public String getPostTranslationModification() {
    return postTranslationModification;
  }

  public void setPostTranslationModification(String postTranslationModification) {
    this.postTranslationModification = postTranslationModification;
  }

  public String getProtein() {
    return protein;
  }

  public void setProtein(String protein) {
    this.protein = protein;
  }

  public String getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(String taxonomy) {
    this.taxonomy = taxonomy;
  }

  public String getProteinQuantity() {
    return proteinQuantity;
  }

  public void setProteinQuantity(String proteinQuantity) {
    this.proteinQuantity = proteinQuantity;
  }

  public GelSeparation getSeparation() {
    return separation;
  }

  public void setSeparation(GelSeparation separation) {
    this.separation = separation;
  }

  public GelThickness getThickness() {
    return thickness;
  }

  public void setThickness(GelThickness thickness) {
    this.thickness = thickness;
  }

  public GelColoration getColoration() {
    return coloration;
  }

  public void setColoration(GelColoration coloration) {
    this.coloration = coloration;
  }

  public String getOtherColoration() {
    return otherColoration;
  }

  public void setOtherColoration(String otherColoration) {
    this.otherColoration = otherColoration;
  }

  public boolean isDecoloration() {
    return decoloration;
  }

  public void setDecoloration(boolean decoloration) {
    this.decoloration = decoloration;
  }

  public Double getWeightMarkerQuantity() {
    return weightMarkerQuantity;
  }

  public void setWeightMarkerQuantity(Double weightMarkerQuantity) {
    this.weightMarkerQuantity = weightMarkerQuantity;
  }

  public String getDevelopmentTime() {
    return developmentTime;
  }

  public void setDevelopmentTime(String developmentTime) {
    this.developmentTime = developmentTime;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getAdditionalPrice() {
    return additionalPrice;
  }

  public void setAdditionalPrice(BigDecimal additionalPrice) {
    this.additionalPrice = additionalPrice;
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

  public List<Solvent> getSolvents() {
    return solvents;
  }

  public void setSolvents(List<Solvent> solvents) {
    this.solvents = solvents;
  }

  public Quantification getQuantification() {
    return quantification;
  }

  public void setQuantification(Quantification quantification) {
    this.quantification = quantification;
  }

  public String getQuantificationComment() {
    return quantificationComment;
  }

  public void setQuantificationComment(String quantificationComment) {
    this.quantificationComment = quantificationComment;
  }

  public InjectionType getInjectionType() {
    return injectionType;
  }

  public void setInjectionType(InjectionType injectionType) {
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

  public LocalDate getDigestionDate() {
    return digestionDate;
  }

  public void setDigestionDate(LocalDate digestionDate) {
    this.digestionDate = digestionDate;
  }

  public LocalDate getAnalysisDate() {
    return analysisDate;
  }

  public void setAnalysisDate(LocalDate analysisDate) {
    this.analysisDate = analysisDate;
  }

  public LocalDate getDataAvailableDate() {
    return dataAvailableDate;
  }

  public void setDataAvailableDate(LocalDate dataAvailableDate) {
    this.dataAvailableDate = dataAvailableDate;
  }

  public LocalDate getSampleDeliveryDate() {
    return sampleDeliveryDate;
  }

  public void setSampleDeliveryDate(LocalDate sampleDeliveryDate) {
    this.sampleDeliveryDate = sampleDeliveryDate;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
