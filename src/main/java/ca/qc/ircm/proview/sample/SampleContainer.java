package ca.qc.ircm.proview.sample;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import java.io.Serializable;
import java.time.Instant;

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
 * An object that contains a {@link Sample}.
 */
@Entity
@Table(name = "samplecontainer")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = -2976707906426974263L;

  /**
   * Type of {@link SampleContainer}.
   */
  public static enum Type {
    TUBE, SPOT;
  }

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Sample inside container.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId")
  private Sample sample;
  /**
   * Treatment done on sample, if any.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "treatmentSampleId")
  private TreatmentSample treatmentSample;
  /**
   * Timestamp of this PlateSpot. This property should not be set.
   */
  @Column(name = "time", nullable = false)
  private Instant timestamp;
  /**
   * True if spot cannot receive a sample.
   */
  @Column(name = "banned", nullable = false)
  private boolean banned;

  public SampleContainer() {
  }

  public SampleContainer(Long id) {
    this.id = id;
  }

  /**
   * Returns sample's container type.
   *
   * @return sample's container type
   */
  public abstract Type getType();

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public TreatmentSample getTreatmentSample() {
    return treatmentSample;
  }

  public void setTreatmentSample(TreatmentSample treatmentSample) {
    this.treatmentSample = treatmentSample;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isBanned() {
    return banned;
  }

  public void setBanned(boolean banned) {
    this.banned = banned;
  }
}
