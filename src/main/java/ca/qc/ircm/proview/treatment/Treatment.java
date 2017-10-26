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
@Table(name = Treatment.TABLE_NAME)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Treatment<S extends TreatmentSample> implements Data {
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
  @Column(name = "deletionExplanation")
  private String deletionExplanation;
  /**
   * List of all treatments done on samples.
   */
  @OneToMany(cascade = CascadeType.ALL, targetEntity = TreatmentSample.class)
  @JoinColumn(name = "treatmentId", nullable = false)
  private List<S> treatmentSamples;

  public Treatment() {
  }

  public Treatment(Long id) {
    this.id = id;
  }

  public abstract TreatmentType getType();

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
