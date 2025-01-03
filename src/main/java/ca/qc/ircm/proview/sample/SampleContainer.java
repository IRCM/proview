package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * An object that contains a {@link Sample}.
 */
@Entity
@Table(name = SampleContainer.TABLE_NAME)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public abstract class SampleContainer implements Data, Named, Serializable {
  public static final String TABLE_NAME = "samplecontainer";
  private static final long serialVersionUID = -2976707906426974263L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Sample inside container.
   */
  @ManyToOne
  @JoinColumn
  private Sample sample;
  /**
   * Version number.
   */
  @Version
  @Column(nullable = false)
  private int version;
  /**
   * Timestamp of this container. This property should not be set.
   */
  @Column(nullable = false)
  private LocalDateTime timestamp;
  /**
   * True if container cannot receive a sample.
   */
  @Column(nullable = false)
  private boolean banned;

  public SampleContainer() {
  }

  public SampleContainer(long id) {
    this.id = id;
  }

  /**
   * Returns sample's container type.
   *
   * @return sample's container type
   */
  public abstract SampleContainerType getType();

  @Override
  public long getId() {
    return id;
  }

  public String getFullName() {
    return getName();
  }

  public void setId(long id) {
    this.id = id;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isBanned() {
    return banned;
  }

  public void setBanned(boolean banned) {
    this.banned = banned;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
