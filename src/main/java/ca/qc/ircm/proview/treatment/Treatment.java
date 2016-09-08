package ca.qc.ircm.proview.treatment;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Treatment done on some samples.
 */
@Entity
@Table(name = "treatment")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Treatment<S extends TreatmentSample> implements Data {
  /**
   * Type of treatment.
   */
  public static enum Type {
    DIGESTION, DILUTION, ENRICHMENT, FRACTIONATION, SOLUBILISATION, STANDARD_ADDITION, TRANSFER
  }

  /**
   * Type of errors that forces Digestion to be deleted.
   */
  public static enum DeletionType {
    /**
     * Digestion information was not entered correctly.
     */
    ERRONEOUS,
    /**
     * Digestion failed due to an experimental problem. An attempt was made to do the digestion but
     * something went wrong.
     */
    FAILED;
  }

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * User who made the treatment.
   */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  /**
   * Time when treatment took plate.
   */
  @Column(name = "insertTime", nullable = false)
  private Instant insertTime;
  /**
   * True if treatment was deleted.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;
  /**
   * Type of error that forces treatment to be deleted.
   */
  @Column(name = "deletionType")
  @Enumerated(STRING)
  private DeletionType deletionType;
  /**
   * Description of what caused the treatment to be deleted.
   */
  @Column(name = "deletionJustification")
  private String deletionJustification;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.PERSIST, targetEntity = TreatmentSample.class)
  @JoinColumn(name = "treatmentId", nullable = false)
  private List<S> treatmentSamples;

  public Treatment() {
  }

  public Treatment(Long id) {
    this.id = id;
  }

  public abstract Type getType();

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public DeletionType getDeletionType() {
    return deletionType;
  }

  public void setDeletionType(DeletionType deletionType) {
    this.deletionType = deletionType;
  }

  public String getDeletionJustification() {
    return deletionJustification;
  }

  public void setDeletionJustification(String deletionJustification) {
    this.deletionJustification = deletionJustification;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<S> getTreatmentSamples() {
    return treatmentSamples;
  }

  public void setTreatmentSamples(List<S> treatmentSamples) {
    this.treatmentSamples = treatmentSamples;
  }

  public Instant getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(Instant insertTime) {
    this.insertTime = insertTime;
  }
}