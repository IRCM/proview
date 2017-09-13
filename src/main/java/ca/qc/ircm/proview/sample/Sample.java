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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * A Sample.
 */
@Entity
@Table(name = Sample.TABLE_NAME)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "sampleType")
public abstract class Sample implements Data, Named, Serializable {
  /**
   * Sample type.
   */
  public static enum Type {
    /**
     * Submission of sample to analyse.
     */
    SUBMISSION,
    /**
     * Control.
     */
    CONTROL
  }

  public static final String TABLE_NAME = "sample";
  private static final long serialVersionUID = -3637467720218236079L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Unique identifier given by LIMS.
   */
  @Column(name = "lims", unique = true, nullable = false)
  @Size(max = 100)
  private String lims;
  /**
   * Sample's name.
   */
  @Column(name = "name")
  @Size(min = 3, max = 150)
  private String name;
  /**
   * Support for this molecule.
   */
  @Column(name = "support")
  @Enumerated(STRING)
  private SampleSupport support;
  /**
   * Volume of Sample (in ul).
   */
  @Column(name = "volume")
  @Min(0)
  private Double volume;
  /**
   * Quantity of Sample (in ug or pmol).
   */
  @Column(name = "quantity", nullable = false)
  @Size(max = 100)
  private String quantity;
  /**
   * Container where sample was originally located.
   */
  @ManyToOne
  @JoinColumn(name = "containerId")
  private SampleContainer originalContainer;
  /**
   * Standards that are in the sample.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "sampleId", updatable = false, nullable = false)
  private List<Standard> standards;

  public Sample() {
  }

  public Sample(Long id) {
    this.id = id;
  }

  public Sample(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Returns type of Sample.
   *
   * @return Sample type.
   */
  public abstract Type getType();

  @Override
  public String toString() {
    return "Sample [id=" + id + ", lims=" + lims + ", name=" + name + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLims() {
    return lims;
  }

  public void setLims(String lims) {
    this.lims = lims;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String tag) {
    this.name = tag;
  }

  public SampleContainer getOriginalContainer() {
    return originalContainer;
  }

  public void setOriginalContainer(SampleContainer originalContainer) {
    this.originalContainer = originalContainer;
  }

  public List<Standard> getStandards() {
    return standards;
  }

  public void setStandards(List<Standard> standards) {
    this.standards = standards;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public SampleSupport getSupport() {
    return support;
  }

  public void setSupport(SampleSupport support) {
    this.support = support;
  }

}
