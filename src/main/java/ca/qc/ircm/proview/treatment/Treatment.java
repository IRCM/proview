package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.DataNullableId;
import ca.qc.ircm.proview.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Treatment done on some samples.
 */
@Entity
@Table(name = Treatment.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class Treatment implements DataNullableId, Serializable {
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

  public static final String TABLE_NAME = "treatment";
  private static final long serialVersionUID = 3942922473290365646L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Type of treatment.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private TreatmentType type;
  /**
   * Protocol used for treatment, if any.
   */
  @ManyToOne
  @JoinColumn
  private Protocol protocol;
  /**
   * How samples where split.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private FractionationType fractionationType;
  /**
   * User who made the treatment.
   */
  @ManyToOne
  @JoinColumn
  private User user;
  /**
   * Time when treatment took plate.
   */
  @Column(nullable = false)
  private LocalDateTime insertTime;
  /**
   * True if treatment was deleted.
   */
  @Column(nullable = false)
  private boolean deleted;
  /**
   * Description of what caused the treatment to be deleted.
   */
  @Column
  private String deletionExplanation;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderColumn(name = "listIndex")
  private List<TreatedSample> treatedSamples;

  public Treatment() {
  }

  public Treatment(Long id) {
    this.id = id;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TreatmentType getType() {
    return type;
  }

  public void setType(TreatmentType type) {
    this.type = type;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public String getDeletionExplanation() {
    return deletionExplanation;
  }

  public void setDeletionExplanation(String deletionExplanation) {
    this.deletionExplanation = deletionExplanation;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<TreatedSample> getTreatedSamples() {
    return treatedSamples;
  }

  public void setTreatedSamples(List<TreatedSample> treatedSamples) {
    this.treatedSamples = treatedSamples;
  }

  public LocalDateTime getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(LocalDateTime insertTime) {
    this.insertTime = insertTime;
  }

  public Protocol getProtocol() {
    return protocol;
  }

  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }

  public FractionationType getFractionationType() {
    return fractionationType;
  }

  public void setFractionationType(FractionationType fractionationType) {
    this.fractionationType = fractionationType;
  }
}
