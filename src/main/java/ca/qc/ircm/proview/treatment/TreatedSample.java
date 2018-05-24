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

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Treatment information that is specific to a sample.
 */
@Entity
@Table(name = TreatedSample.TABLE_NAME)
public class TreatedSample implements Data, Serializable {
  public static final String TABLE_NAME = "treatedsample";
  private static final long serialVersionUID = -1654046284723997439L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Treatment.
   */
  @ManyToOne
  @JoinColumn(name = "treatmentId", nullable = false)
  private Treatment treatment;
  /**
   * Sample that received treatment.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId", nullable = false)
  private Sample sample;
  /**
   * Sample's container.
   */
  @ManyToOne
  @JoinColumn(name = "containerId", nullable = false)
  private SampleContainer container;
  /**
   * Sample's destination container, if any.
   */
  @ManyToOne
  @JoinColumn(name = "destinationContainerId")
  private SampleContainer destinationContainer;
  /**
   * Volume of source transfered.
   */
  @Column(name = "sourceVolume", nullable = false)
  @Min(0)
  private Double sourceVolume;
  /**
   * Solvent used for dilution.
   */
  @Column(name = "solvent", nullable = false)
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column(name = "solventVolume", nullable = false)
  @Min(0)
  private Double solventVolume;
  /**
   * Name of standard added.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Quantity of standard added.
   */
  @Column(name = "quantity", nullable = false)
  @Size(max = 100)
  private String quantity;
  /**
   * Fraction index number that is appended when showing LIMS number of treatedSample.
   */
  @Column(name = "position", nullable = false)
  private Integer position;
  /**
   * Fraction number. Used with {@link FractionationType#MUDPIT}.
   */
  @Column(name = "number")
  private Integer number;
  /**
   * PI interval. Used with {@link FractionationType#PI}
   */
  @Column(name = "piInterval")
  @Size(max = 255)
  private String piInterval;
  /**
   * Comment about sample's treatment.
   */
  @Column(name = "comment")
  private String comment;

  public String getFractionName() {
    if (getSample() != null && getSample().getName() != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(getSample().getName());
      builder.append(".F");
      builder.append(position);
      return builder.toString();
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return "TreatedSample [id=" + id + "]";
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }

  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }

  public Double getSourceVolume() {
    return sourceVolume;
  }

  public void setSourceVolume(Double sourceVolume) {
    this.sourceVolume = sourceVolume;
  }

  public String getSolvent() {
    return solvent;
  }

  public void setSolvent(String solvent) {
    this.solvent = solvent;
  }

  public Double getSolventVolume() {
    return solventVolume;
  }

  public void setSolventVolume(Double solventVolume) {
    this.solventVolume = solventVolume;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getPiInterval() {
    return piInterval;
  }

  public void setPiInterval(String piInterval) {
    this.piInterval = piInterval;
  }

  public Treatment getTreatment() {
    return treatment;
  }

  public void setTreatment(Treatment treatment) {
    this.treatment = treatment;
  }
}
