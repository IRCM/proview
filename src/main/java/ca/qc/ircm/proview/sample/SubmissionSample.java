/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.ORDINAL;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryData;
import ca.qc.ircm.proview.user.User;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

/**
 * Sample submitted for MS analysis.
 */
@Entity
@DiscriminatorValue("SUBMISSION")
@GeneratePropertyNames
public class SubmissionSample extends Sample implements LaboratoryData, Named {
  private static final long serialVersionUID = -7652364189294805763L;

  /**
   * Sample status.
   */
  @Column(nullable = false)
  @Enumerated(ORDINAL)
  private SampleStatus status;
  /**
   * Number of Proteins in Sample.
   */
  @Column
  @Min(0)
  private Integer numberProtein;
  /**
   * Molecular weight of Protein in Sample.
   */
  @Column
  @Min(0)
  private Double molecularWeight;
  /**
   * Used by Hibernate.
   */
  @Column
  private int listIndex;
  /**
   * Submission of this sample.
   */
  @ManyToOne
  @JoinColumn
  private Submission submission;

  public SubmissionSample() {
  }

  public SubmissionSample(Long id) {
    super(id);
  }

  public SubmissionSample(String name) {
    super(null, name);
  }

  public SubmissionSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public Laboratory getLaboratory() {
    return getSubmission() != null ? getSubmission().getLaboratory() : null;
  }

  public User getUser() {
    return getSubmission() != null ? getSubmission().getUser() : null;
  }

  @Override
  public Category getCategory() {
    return Category.SUBMISSION;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public SampleStatus getStatus() {
    return status;
  }

  public void setStatus(SampleStatus status) {
    this.status = status;
  }

  public Integer getNumberProtein() {
    return numberProtein;
  }

  public void setNumberProtein(Integer numberProtein) {
    this.numberProtein = numberProtein;
  }

  public Double getMolecularWeight() {
    return molecularWeight;
  }

  public void setMolecularWeight(Double molecularWeight) {
    this.molecularWeight = molecularWeight;
  }

  public int getListIndex() {
    return listIndex;
  }

  public void setListIndex(int listIndex) {
    this.listIndex = listIndex;
  }
}
