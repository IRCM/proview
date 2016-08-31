package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.Solvent;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Link between a sample and a solvent.
 */
@Entity
@Table(name = "solvent")
public class SampleSolvent implements Data, Serializable {
  private static final long serialVersionUID = 3304432592040869501L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Solvent.
   */
  @Column(name = "solvent", nullable = false)
  @Enumerated(STRING)
  private Solvent solvent;
  /**
   * Solvent was removed from sample's solvents.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  public SampleSolvent() {
  }

  public SampleSolvent(Long id) {
    this.id = id;
  }

  public SampleSolvent(Solvent solvent) {
    this.solvent = solvent;
  }

  public SampleSolvent(Long id, Solvent solvent) {
    this.id = id;
    this.solvent = solvent;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Solvent getSolvent() {
    return solvent;
  }

  public void setSolvent(Solvent solvent) {
    this.solvent = solvent;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
