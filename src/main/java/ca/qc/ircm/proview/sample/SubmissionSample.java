package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.EnumType.ORDINAL;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryData;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;

/**
 * Sample submitted for MS analysis.
 */
@Entity
@DiscriminatorValue("SUBMISSION")
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class SubmissionSample extends Sample implements LaboratoryData, Named {
  private static final long serialVersionUID = -7652364189294805763L;

  /**
   * Sample status.
   */
  @Column(nullable = false)
  @Enumerated(ORDINAL)
  private SampleStatus status;
  /**
   * Number of Proteins in Sample.
   */
  @Column
  @Min(0)
  private Integer numberProtein;
  /**
   * Molecular weight of Protein in Sample.
   */
  @Column
  @Min(0)
  private Double molecularWeight;
  /**
   * Used by Hibernate.
   */
  @Column
  private int listIndex;
  /**
   * Submission of this sample.
   */
  @ManyToOne
  @JoinColumn
  private Submission submission;

  public SubmissionSample() {
  }

  public SubmissionSample(Long id) {
    super(id);
  }

  public SubmissionSample(String name) {
    super(null, name);
  }

  public SubmissionSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public Laboratory getLaboratory() {
    return getSubmission() != null ? getSubmission().getLaboratory() : null;
  }

  public User getUser() {
    return getSubmission() != null ? getSubmission().getUser() : null;
  }

  @Override
  public Category getCategory() {
    return Category.SUBMISSION;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public SampleStatus getStatus() {
    return status;
  }

  public void setStatus(SampleStatus status) {
    this.status = status;
  }

  public Integer getNumberProtein() {
    return numberProtein;
  }

  public void setNumberProtein(Integer numberProtein) {
    this.numberProtein = numberProtein;
  }

  public Double getMolecularWeight() {
    return molecularWeight;
  }

  public void setMolecularWeight(Double molecularWeight) {
    this.molecularWeight = molecularWeight;
  }

  public int getListIndex() {
    return listIndex;
  }

  public void setListIndex(int listIndex) {
    this.listIndex = listIndex;
  }
}
