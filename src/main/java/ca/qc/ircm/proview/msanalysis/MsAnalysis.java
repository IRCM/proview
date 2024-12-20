package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.DataNullableId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MS analysis.
 */
@Entity
@Table(name = MsAnalysis.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class MsAnalysis implements DataNullableId, Serializable {
  public static final String TABLE_NAME = "msanalysis";
  private static final long serialVersionUID = 7334138327920441104L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Mass detection instrument used in this MS analysis.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private MassDetectionInstrument massDetectionInstrument;
  /**
   * Selected Source for mass detection instrument.
   */
  @Column
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
  @OneToMany(mappedBy = "msAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderColumn(name = "listIndex")
  private List<Acquisition> acquisitions;

  public MsAnalysis() {
  }

  public MsAnalysis(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "MsAnalysis [id=" + id + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MassDetectionInstrumentSource getSource() {
    return source;
  }

  public void setSource(MassDetectionInstrumentSource source) {
    this.source = source;
  }

  public MassDetectionInstrument getMassDetectionInstrument() {
    return massDetectionInstrument;
  }

  public void setMassDetectionInstrument(MassDetectionInstrument massDetectionInstrument) {
    this.massDetectionInstrument = massDetectionInstrument;
  }

  public LocalDateTime getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(LocalDateTime insertTime) {
    this.insertTime = insertTime;
  }

  public String getDeletionExplanation() {
    return deletionExplanation;
  }

  public void setDeletionExplanation(String deletionExplanation) {
    this.deletionExplanation = deletionExplanation;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public List<Acquisition> getAcquisitions() {
    return acquisitions;
  }

  public void setAcquisitions(List<Acquisition> acquisitions) {
    this.acquisitions = acquisitions;
  }
}
