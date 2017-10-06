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

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * An object that contains a {@link Sample}.
 */
@Entity
@Table(name = "samplecontainer")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = -2976707906426974263L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Sample inside container.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId")
  private Sample sample;
  /**
   * Timestamp of this container. This property should not be set.
   */
  @Column(name = "time", nullable = false)
  private Instant timestamp;
  /**
   * True if container cannot receive a sample.
   */
  @Column(name = "banned", nullable = false)
  private boolean banned;

  public SampleContainer() {
  }

  public SampleContainer(Long id) {
    this.id = id;
  }

  /**
   * Returns sample's container type.
   *
   * @return sample's container type
   */
  public abstract SampleContainerType getType();

  @Override
  public Long getId() {
    return id;
  }

  public String getFullName() {
    return getName();
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isBanned() {
    return banned;
  }

  public void setBanned(boolean banned) {
    this.banned = banned;
  }
}
