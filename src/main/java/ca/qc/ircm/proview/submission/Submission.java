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

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryData;
import ca.qc.ircm.proview.user.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Sample Submission of a User.
 */
@Entity
@Table(name = Submission.TABLE_NAME)
public class Submission implements Data, LaboratoryData, Serializable {
  public static final String TABLE_NAME = "submission";
  private static final long serialVersionUID = 2223809698076034086L;

  /**
   * Database identifier of this Submission.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Service chosen.
   */
  @Column(name = "service", nullable = false)
  @Enumerated(STRING)
  private Service service;
  /**
   * Taxonomy of Sample content.
   */
  @Column(name = "taxonomy")
  @Size(max = 100)
  private String taxonomy;
  /**
   * User's project.
   */
  @Column(name = "project")
  @Size(max = 100)
  private String project;
  /**
   * User's experience.
   */
  @Column(name = "experience")
  @Size(max = 100)
  private String experience;
  /**
   * Experience's goal.
   */
  @Column(name = "goal")
  @Size(max = 150)
  private String goal;
  /**
   * Mass detection instrument.
   */
  @Column(name = "massDetectionInstrument")
  @Enumerated(STRING)
  private MassDetectionInstrument massDetectionInstrument;
  /**
   * Intact protein ionization source for mass detection instrument.
   */
  @Column(name = "source")
  @Enumerated(STRING)
  private MassDetectionInstrumentSource source;
  /**
   * Injection type.
   */
  @Column(name = "injectionType")
  @Enumerated(STRING)
  private InjectionType injectionType;
  /**
   * Proteolytic digestion method.
   */
  @Column(name = "proteolyticDigestionMethod")
  @Enumerated(STRING)
  private ProteolyticDigestion proteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#DIGESTED DIGESTED}.
   *
   * @see ProteolyticDigestion
   */
  @Column(name = "usedProteolyticDigestionMethod")
  @Size(max = 100)
  private String usedProteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteolyticDigestion#OTHER OTHER}.
   *
   * @see ProteolyticDigestion
   */
  @Column(name = "otherProteolyticDigestionMethod")
  @Size(max = 100)
  private String otherProteolyticDigestionMethod;
  /**
   * Database on which Protein identification will be made.
   */
  @Column(name = "proteinIdentification")
  @Enumerated(STRING)
  private ProteinIdentification proteinIdentification;
  /**
   * Link to non-conventional Protein identification database.
   */
  @Column(name = "proteinIdentificationLink")
  @Size(max = 255)
  private String proteinIdentificationLink;
  /**
   * Peptide enrichmentType.
   */
  @Column(name = "enrichmentType")
  @Enumerated(STRING)
  private EnrichmentType enrichmentType;
  /**
   * Other peptide enrichmentType.
   */
  @Column(name = "otherEnrichmentType")
  @Size(max = 100)
  private String otherEnrichmentType;
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
   * 2 dimension liquid chromatographie with MudPit.
   */
  @Column(name = "mudPitFraction")
  @Enumerated(STRING)
  private MudPitFraction mudPitFraction;
  /**
   * Number of Proteins in samples.
   */
  @Column(name = "proteinContent")
  @Enumerated(STRING)
  private ProteinContent proteinContent;
  /**
   * Protein that should be found in Sample.
   */
  @Column(name = "protein")
  @Size(max = 100)
  private String protein;
  /**
   * Post-translationnal modification to Protein.
   */
  @Column(name = "postTranslationModification")
  @Size(max = 150)
  private String postTranslationModification;
  /**
   * Gel separation.
   */
  @Column(name = "separation")
  @Enumerated(STRING)
  private GelSeparation separation;
  /**
   * Gel thickness.
   */
  @Column(name = "thickness")
  @Enumerated(STRING)
  private GelThickness thickness;
  /**
   * Gel coloration.
   */
  @Column(name = "coloration")
  @Enumerated(STRING)
  private GelColoration coloration;
  /**
   * Other gel coloration (if any).
   */
  @Column(name = "otherColoration")
  @Size(max = 100)
  private String otherColoration;
  /**
   * Gel development time (for coloration).
   */
  @Column(name = "developmentTime")
  @Size(max = 100)
  private String developmentTime;
  /**
   * Gel decoloration.
   */
  @Column(name = "decoloration", nullable = false)
  private boolean decoloration = false;
  /**
   * Quantity of weight marker.
   */
  @Column(name = "weightMarkerQuantity")
  private Double weightMarkerQuantity;
  /**
   * Quantity of proteins in gel (total).
   */
  @Column(name = "proteinQuantity")
  @Size(max = 100)
  private String proteinQuantity;
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
   * Solvents that our lab uses that can solubilise sample.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "submissionId")
  private List<SampleSolvent> solvents;
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
   * Quantification.
   */
  @Column(name = "quantification")
  @Enumerated(STRING)
  private Quantification quantification;
  /**
   * Quantification labels.
   */
  @Column(name = "quantificationLabels")
  private String quantificationLabels;
  /**
   * Any comment on this sample.
   */
  @Column(name = "comment")
  private String comment;
  /**
   * Date of Submission submission.
   */
  @Column(name = "submissionDate")
  private Instant submissionDate;
  /**
   * Price charged when sample was first submitted.
   */
  @Column(name = "price")
  private BigDecimal price;
  /**
   * Additional price charged by proteomic laboratory members due to change in sample's submission.
   */
  @Column(name = "additionalPrice")
  private BigDecimal additionalPrice;
  /**
   * User who made the Submission.
   */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  /**
   * Laboratory who made the Submission.
   */
  @ManyToOne
  @JoinColumn(name = "laboratoryId")
  private Laboratory laboratory;
  /**
   * Samples that are part of this submission.
   */
  @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SubmissionSample> samples;
  /**
   * Additional files related to submission.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "submissionId")
  private List<SubmissionFile> files;

  public Submission() {
  }

  public Submission(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Submission [id=" + id + ", service=" + service + ", experience=" + experience + "]";
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

  public Instant getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(Instant submissionDate) {
    this.submissionDate = submissionDate;
  }

  public List<SubmissionSample> getSamples() {
    return samples;
  }

  public void setSamples(List<SubmissionSample> samples) {
    this.samples = samples;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getExperience() {
    return experience;
  }

  public void setExperience(String experience) {
    this.experience = experience;
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

  public List<SampleSolvent> getSolvents() {
    return solvents;
  }

  public void setSolvents(List<SampleSolvent> solvents) {
    this.solvents = solvents;
  }

  public Quantification getQuantification() {
    return quantification;
  }

  public void setQuantification(Quantification quantification) {
    this.quantification = quantification;
  }

  public String getQuantificationLabels() {
    return quantificationLabels;
  }

  public void setQuantificationLabels(String quantificationLabels) {
    this.quantificationLabels = quantificationLabels;
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
}
