package ca.qc.ircm.proview.treatment;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Treatment information that is specific to a sample.
 */
@Entity
@Table(name = "treatmentsample")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "treatmentType")
public abstract class TreatmentSample implements Data {
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Sample that received treatment.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId", nullable = false)
  private Sample sample;
  /**
   * Sample's container.
   */
  @ManyToOne
  @JoinColumn(name = "containerId", nullable = false)
  private SampleContainer container;
  /**
   * Comments about sample's treatment.
   */
  @Column(name = "comments")
  private String comments;

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }
}
