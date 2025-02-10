package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.UsedBy.HIBERNATE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.UsedBy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.Nullable;

/**
 * MS analysis.
 */
@Entity
@Table(name = MsAnalysis.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = ENTITY_EI_EXPOSE_REP)
public class MsAnalysis implements Data, Serializable {

  public static final String TABLE_NAME = "msanalysis";
  @Serial
  private static final long serialVersionUID = 7334138327920441104L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Mass detection instrument used in this MS analysis.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private MassDetectionInstrument massDetectionInstrument;
  /**
   * Selected Source for mass detection instrument.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private MassDetectionInstrumentSource source;
  /**
   * Time when analysis was inserted.
   */
  @Column(nullable = false)
  private LocalDateTime insertTime;
  /**
   * True if MS analysis was deleted.
   */
  @Column(nullable = false)
  private boolean deleted;
  /**
   * Description of what caused the MS analysis to be deleted.
   */
  @Column
  private String deletionExplanation;
  /**
   * Acquisitions of samples made in this MS analysis.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "msanalysis_id", nullable = false)
  @OrderColumn(name = "listIndex")
  private List<Acquisition> acquisitions;

  public MsAnalysis() {
  }

  public MsAnalysis(long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "MsAnalysis [id=" + id + "]";
  }

  @Override
  public long getId() {
    return id;
  }

  @UsedBy(HIBERNATE)
  public void setId(long id) {
    this.id = id;
  }

  public MassDetectionInstrumentSource getSource() {
    return source;
  }

  @UsedBy(HIBERNATE)
  public void setSource(MassDetectionInstrumentSource source) {
    this.source = source;
  }

  public MassDetectionInstrument getMassDetectionInstrument() {
    return massDetectionInstrument;
  }

  @UsedBy(HIBERNATE)
  public void setMassDetectionInstrument(MassDetectionInstrument massDetectionInstrument) {
    this.massDetectionInstrument = massDetectionInstrument;
  }

  public LocalDateTime getInsertTime() {
    return insertTime;
  }

  @UsedBy(HIBERNATE)
  public void setInsertTime(LocalDateTime insertTime) {
    this.insertTime = insertTime;
  }

  @Nullable
  public String getDeletionExplanation() {
    return deletionExplanation;
  }

  @UsedBy(HIBERNATE)
  public void setDeletionExplanation(@Nullable String deletionExplanation) {
    this.deletionExplanation = deletionExplanation;
  }

  public boolean isDeleted() {
    return deleted;
  }

  @UsedBy(HIBERNATE)
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public List<Acquisition> getAcquisitions() {
    return acquisitions;
  }

  @UsedBy(HIBERNATE)
  public void setAcquisitions(List<Acquisition> acquisitions) {
    this.acquisitions = acquisitions;
  }
}
