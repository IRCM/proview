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

package ca.qc.ircm.proview.msanalysis;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
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
import javax.validation.constraints.Size;

/**
 * Acquisition of a Sample in a MS analysis.
 */
@Entity
@Table(name = Acquisition.TABLE_NAME)
public class Acquisition implements Data, Named, Serializable {
  public static final String TABLE_NAME = "acquisition";
  private static final long serialVersionUID = 4253647399151347110L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * MS Analysis.
   */
  @ManyToOne
  @JoinColumn(name = "msAnalysisId", nullable = false)
  private MsAnalysis msAnalysis;
  /**
   * Sample's container type.
   */
  @ManyToOne
  @JoinColumn(name = "containerId", nullable = false)
  private SampleContainer container;
  /**
   * Sample acquired.
   */
  @ManyToOne
  @JoinColumn(name = "sampleId", nullable = false)
  private Sample sample;
  /**
   * Number of acquisition on the sample.
   */
  @Column(name = "numberOfAcquisition")
  private Integer numberOfAcquisition;
  /**
   * Name of Sample list being analysed.
   */
  @Column(name = "sampleListName")
  @Size(max = 255)
  private String sampleListName;
  /**
   * File containing acquisition information.
   */
  @Column(name = "acquisitionFile")
  @Size(max = 255)
  private String acquisitionFile;
  /**
   * Acquisition index number that is appended when showing LIMS.
   */
  @Column(name = "position")
  private Integer position;
  /**
   * Any comment on this acquisition.
   */
  @Column(name = "comment")
  @Size(max = 255)
  private String comment;

  public Acquisition() {
  }

  public Acquisition(Long id) {
    this.id = id;
  }

  @Override
  public String getName() {
    if (getSample() != null && getSample().getName() != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(getSample().getName());
      builder.append(".A");
      builder.append(position);
      return builder.toString();
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Acquisition(");
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

  public Integer getNumberOfAcquisition() {
    return numberOfAcquisition;
  }

  public void setNumberOfAcquisition(Integer numberOfAcquisition) {
    this.numberOfAcquisition = numberOfAcquisition;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public Sample getSample() {
    return sample;
  }

  public String getSampleListName() {
    return sampleListName;
  }

  public void setSampleListName(String sampleListName) {
    this.sampleListName = sampleListName;
  }

  public String getAcquisitionFile() {
    return acquisitionFile;
  }

  public void setAcquisitionFile(String acquisitionFile) {
    this.acquisitionFile = acquisitionFile;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }

  public MsAnalysis getMsAnalysis() {
    return msAnalysis;
  }

  public void setMsAnalysis(MsAnalysis msAnalysis) {
    this.msAnalysis = msAnalysis;
  }
}
