package ca.qc.ircm.proview.msanalysis;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Link between an acquisition and a Mascot file.
 */
@Entity
@Table(name = "acquisition_to_mascotfile")
public class AcquisitionMascotFile implements Data, Serializable {
  private static final long serialVersionUID = -709975720899956115L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Acquisition linked to Mascot file.
   */
  @ManyToOne
  @JoinColumn(name = "acquisitionId")
  private Acquisition acquisition;
  /**
   * Mascot file.
   */
  @ManyToOne
  @JoinColumn(name = "mascotFileId")
  private MascotFile mascotFile;
  /**
   * True if Mascot file should be visible to users.
   */
  @Column(name = "visible", nullable = false)
  private boolean visible;
  /**
   * Comments of mascot file.
   */
  @Column(name = "comments")
  @Size(max = 255)
  private String comments;

  public AcquisitionMascotFile() {
  }

  public AcquisitionMascotFile(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "AcquisitionMascotFile [id=" + id + ", acquisition=" + acquisition + ", mascotFile="
        + mascotFile + "]";
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

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public Acquisition getAcquisition() {
    return acquisition;
  }

  public void setAcquisition(Acquisition acquisition) {
    this.acquisition = acquisition;
  }

  public MascotFile getMascotFile() {
    return mascotFile;
  }

  public void setMascotFile(MascotFile mascotFile) {
    this.mascotFile = mascotFile;
  }
}
