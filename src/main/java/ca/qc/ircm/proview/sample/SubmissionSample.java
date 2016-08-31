package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.LaboratoryData;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Sample submitted for MS analysis.
 */
@Entity
public abstract class SubmissionSample extends Sample implements LaboratoryData, Named {
  /**
   * Service demanded for submitted sample.
   */
  public static enum ServiceType {
    /**
     * Sample contains multiple proteins to be analysed by MS.
     */
    PROTEIC, /**
              * Sample contains a single small molecule to be analyse by MS.
              * {@link ServiceType#SMALL_MOLECULE} is valid only if
              * {@link SubmissionSample#getService()} is {@link Service#SMALL_MOLECULE}.
              */
    SMALL_MOLECULE
  }

  /**
   * All statuses of a sample.
   */
  public static enum Status {
    /**
     * Sample price must be approved by manager.
     */
    TO_APPROVE, /**
                 * Sample is not received yet.
                 */
    TO_RECEIVE, /**
                 * Sample is in solution.
                 */
    RECEIVED, /**
               * Sample must be digest.
               */
    TO_DIGEST, /**
                * Sample must be enriched.
                */
    TO_ENRICH, /**
                * Sample must be analysed.
                */
    TO_ANALYSE, /**
                 * Result data must be manually analysed.
                 */
    DATA_ANALYSIS, /**
                    * Sample is analysed and have results.
                    */
    ANALYSED, /**
               * Sample analyse was cancelled.
               */
    CANCELLED;
  }

  private static final long serialVersionUID = -7652364189294805763L;

  /**
   * Sample status.
   */
  @Column(name = "status", nullable = false)
  @Enumerated(STRING)
  private Status status;
  /**
   * Submission of this sample.
   */
  @ManyToOne
  @JoinColumn(name = "submissionId")
  private Submission submission;
  /**
   * Service chosen.
   */
  @Column(name = "service", nullable = false)
  @Enumerated(STRING)
  private Service service;
  /**
   * Mass detection instrument.
   */
  @Column(name = "massDetectionInstrument")
  @Enumerated(STRING)
  private MsAnalysis.MassDetectionInstrument massDetectionInstrument;
  /**
   * Intact protein ionization source for mass detection instrument.
   */
  @Column(name = "source")
  @Enumerated(STRING)
  private Source source;
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

  public SubmissionSample() {
  }

  public SubmissionSample(Long id) {
    super(id);
  }

  public SubmissionSample(Long id, String name) {
    super(id, name);
  }

  /**
   * Returns service demanded for submitted sample.
   *
   * @return service demanded for submitted sample
   */
  public abstract ServiceType getServiceType();

  @Override
  public Laboratory getLaboratory() {
    return getSubmission() != null ? getSubmission().getLaboratory() : null;
  }

  public User getUser() {
    return getSubmission() != null ? getSubmission().getUser() : null;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public Status getStatus() {
    return status;
  }

  @Override
  public Type getType() {
    return Type.SUBMISSION;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public MsAnalysis.MassDetectionInstrument getMassDetectionInstrument() {
    return massDetectionInstrument;
  }

  public void
      setMassDetectionInstrument(MsAnalysis.MassDetectionInstrument massDetectionInstrument) {
    this.massDetectionInstrument = massDetectionInstrument;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
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
}
