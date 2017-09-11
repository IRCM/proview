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

package ca.qc.ircm.proview.dataanalysis;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.SubmissionSample;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Analysis of data.
 */
@Entity
@Table(name = "dataanalysis")
public class DataAnalysis implements Data, Serializable {
  private static final long serialVersionUID = 7855087016781621873L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Sample link to data to analyse.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId", updatable = false)
  private SubmissionSample sample;
  /**
   * Proteins to analyse.
   */
  @Column(name = "protein", nullable = false)
  @Size(max = 255)
  private String protein;
  /**
   * Specific peptides to analyse.
   */
  @Column(name = "peptide")
  @Size(max = 255)
  private String peptide;
  /**
   * Type of analysis.
   */
  @Column(name = "analysisType", nullable = false)
  @Enumerated(STRING)
  private DataAnalysisType type;
  /**
   * Maximum amount of time to work.
   */
  @Column(name = "maxWorkTime", nullable = false)
  private Double maxWorkTime;
  /**
   * Score given to elements that were analysed.
   */
  @Column(name = "score")
  private String score;
  /**
   * Work time spent on analysis.
   */
  @Column(name = "workTime")
  private Double workTime;
  /**
   * Analyse status.
   */
  @Column(name = "status", nullable = false)
  @Enumerated(STRING)
  private DataAnalysisStatus status;

  public DataAnalysis() {
  }

  public DataAnalysis(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "DataAnalysis [id=" + id + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SubmissionSample getSample() {
    return sample;
  }

  public void setSample(SubmissionSample sample) {
    this.sample = sample;
  }

  public String getProtein() {
    return protein;
  }

  public void setProtein(String protein) {
    this.protein = protein;
  }

  public String getPeptide() {
    return peptide;
  }

  public void setPeptide(String peptide) {
    this.peptide = peptide;
  }

  public DataAnalysisType getType() {
    return type;
  }

  public void setType(DataAnalysisType type) {
    this.type = type;
  }

  public Double getMaxWorkTime() {
    return maxWorkTime;
  }

  public void setMaxWorkTime(Double maxWorkTime) {
    this.maxWorkTime = maxWorkTime;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public Double getWorkTime() {
    return workTime;
  }

  public void setWorkTime(Double workTime) {
    this.workTime = workTime;
  }

  public DataAnalysisStatus getStatus() {
    return status;
  }

  public void setStatus(DataAnalysisStatus status) {
    this.status = status;
  }
}
