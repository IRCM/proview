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
  /**
   * Type of data analysis.
   */
  public enum Type {
    /**
     * Analysis made only to confirm very probable protein presence.
     */
    PROTEIN, /**
              * Analysis made to confirm correctiveness of specific peptides.
              */
    PEPTIDE, /**
              * Analyse both protein and and peptides.
              */
    PROTEIN_PEPTIDE;
  }

  /**
   * Completed status.
   */
  public enum Status {
    /**
     * Data has to be analysed.
     */
    TO_DO, /**
            * Data is analysed.
            */
    ANALYSED, /**
               * Data analysis was cancelled.
               */
    CANCELLED;
  }

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
  private Type type;
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
  private Status status;

  public DataAnalysis() {
  }

  public DataAnalysis(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DataAnalysis) {
      DataAnalysis other = (DataAnalysis) obj;
      return this.id != null && this.id.equals(other.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id == null ? 0 : id.intValue();
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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}