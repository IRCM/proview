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

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Acquisition of a Sample in a MS analysis.
 */
@Entity
@Table(name = Acquisition.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class Acquisition implements Data, Named, Serializable {
  public static final String TABLE_NAME = "acquisition";
  private static final long serialVersionUID = 4253647399151347110L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * MS Analysis.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private MsAnalysis msAnalysis;
  /**
   * Sample's container type.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private SampleContainer container;
  /**
   * Sample acquired.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private Sample sample;
  /**
   * Number of acquisition on the sample.
   */
  @Column
  private Integer numberOfAcquisition;
  /**
   * Name of Sample list being analysed.
   */
  @Column
  @Size(max = 255)
  private String sampleListName;
  /**
   * File containing acquisition information.
   */
  @Column
  @Size(max = 255)
  private String acquisitionFile;
  /**
   * Acquisition index number that is appended when showing LIMS.
   */
  @Column
  private Integer position;
  /**
   * Any comment on this acquisition.
   */
  @Column
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
