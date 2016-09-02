package ca.qc.ircm.proview.submission;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.LaboratoryData;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.user.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Sample Submission of a User.
 */
@Entity
@Table(name = "submission")
public class Submission implements Data, LaboratoryData, Serializable {
  private static final long serialVersionUID = 2223809698076034086L;

  /**
   * Database identifier of this Submission.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Date of Submission submission.
   */
  @Column(name = "submissionDate")
  private Instant submissionDate;
  /**
   * User who made the Submission.
   */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  /**
   * Laboratory who made the Submission.
   */
  @ManyToOne
  @JoinColumn(name = "laboratoryId")
  private Laboratory laboratory;
  /**
   * Samples that are part of this submission.
   */
  @OneToMany(mappedBy = "submission")
  private List<SubmissionSample> samples;
  /**
   * Samples that are part of this submission.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
  @JoinColumn(name = "submissionId")
  private List<GelImage> gelImages;

  public Submission() {
  }

  public Submission(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Submission) {
      Submission other = (Submission) obj;
      return this.id != null && this.id.equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id == null ? 0 : this.id.intValue();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Submission(");
    buff.append(id);
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public Laboratory getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Instant getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(Instant submissionDate) {
    this.submissionDate = submissionDate;
  }

  public List<SubmissionSample> getSamples() {
    return samples;
  }

  public void setSamples(List<SubmissionSample> samples) {
    this.samples = samples;
  }

  public List<GelImage> getGelImages() {
    return gelImages;
  }

  public void setGelImages(List<GelImage> gelImages) {
    this.gelImages = gelImages;
  }
}
