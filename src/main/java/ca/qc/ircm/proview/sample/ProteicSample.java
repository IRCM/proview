package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Sample submitted for proteomic analysis.
 */
@Entity
public abstract class ProteicSample extends SubmissionSample {
  /**
   * Available type of digestions.
   */
  public static enum ProteolyticDigestion {
    TRYPSINE, DIGESTED, OTHER;
  }

  /**
   * Available protein identifications.
   */
  public static enum ProteinIdentification {
    NCBINR, MSDB_ID, OTHER;
  }

  /**
   * Available MudPit fractions.
   */
  public static enum MudPitFraction {
    EIGHT(8), TWELVE(12), SIXTEEN(16);
    MudPitFraction(int number) {
      this.number = number;
    }

    /**
     * Number of fractions for this enum.
     */
    private int number;

    public int getNumber() {
      return number;
    }
  }

  /**
   * Protein content of samples.
   */
  public static enum ProteinContent {
    SMALL(1, 4), MEDIUM(5, 10), LARGE(10, 20), XLARGE(20, Integer.MAX_VALUE);
    ProteinContent(int start, int end) {
      this.start = start;
      this.end = end;
    }

    /**
     * Start of interval.
     */
    private int start;
    /**
     * End of interval.
     */
    private int end;

    public int getStart() {
      return start;
    }

    public void setStart(int start) {
      this.start = start;
    }

    public int getEnd() {
      return end;
    }

    public void setEnd(int end) {
      this.end = end;
    }
  }

  /**
   * Available type of enrichments.
   */
  public static enum EnrichmentType {
    PHOSPHOPEPTIDES, OTHER;
  }

  private static final long serialVersionUID = -4255179147934758033L;

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
   * Taxonomy of Sample content.
   */
  @Column(name = "taxonomy")
  @Size(max = 100)
  private String taxonomy;
  /**
   * Protein that should be found in Sample.
   */
  @Column(name = "protein")
  @Size(max = 100)
  private String protein;
  /**
   * Molecular weight of Protein in Sample.
   */
  @Column(name = "molecularWeight")
  @Min(0)
  private Double molecularWeight;
  /**
   * Post-translationnal modification to Protein.
   */
  @Column(name = "postTranslationModification")
  @Size(max = 150)
  private String postTranslationModification;
  /**
   * Number of Proteins in Sample.
   */
  @Column(name = "sampleNumberProtein")
  @Min(0)
  private Integer sampleNumberProtein;
  /**
   * Proteolytic digestion method.
   */
  @Column(name = "proteolyticDigestionMethod")
  @Enumerated(STRING)
  private ProteolyticDigestion proteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteicSample.ProteolyticDigestion#DIGESTED DIGESTED}.
   *
   * @see ProteicSample.ProteolyticDigestion
   */
  @Column(name = "usedProteolyticDigestionMethod")
  @Size(max = 100)
  private String usedProteolyticDigestionMethod;
  /**
   * Proteolytic digestion method supplied by User if ProteolyticDigestion is
   * {@link ProteicSample.ProteolyticDigestion#OTHER OTHER}.
   *
   * @see ProteicSample.ProteolyticDigestion
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

  public ProteicSample() {
  }

  public ProteicSample(Long id) {
    super(id);
  }

  public ProteicSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public ServiceType getServiceType() {
    return ServiceType.PROTEIC;
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

  public Double getMolecularWeight() {
    return molecularWeight;
  }

  public void setMolecularWeight(Double molecularWeight) {
    this.molecularWeight = molecularWeight;
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

  public Integer getSampleNumberProtein() {
    return sampleNumberProtein;
  }

  public void setSampleNumberProtein(Integer sampleNumberProtein) {
    this.sampleNumberProtein = sampleNumberProtein;
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
}
