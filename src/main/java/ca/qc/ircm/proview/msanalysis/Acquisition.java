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
@Table(name = "acquisition")
public class Acquisition implements Data, Named, Serializable {
  private static final long serialVersionUID = 4253647399151347110L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
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
   * Index of acquisition inside MS analysis.
   */
  @Column(name = "listIndex")
  private Integer listIndex;
  /**
   * Any comments on this acquisition.
   */
  @Column(name = "comments")
  @Size(max = 255)
  private String comments;

  public Acquisition() {
  }

  public Acquisition(Long id) {
    this.id = id;
  }

  /**
   * Returns acquisition LIMS.
   * 
   * @return acquisition LIMS
   */
  public String getLims() {
    if (getSample() != null && getSample().getLims() != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(getSample().getLims());
      builder.append(".A");
      builder.append(position);
      return builder.toString();
    } else {
      return null;
    }
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

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Integer getListIndex() {
    return listIndex;
  }

  public void setListIndex(Integer listIndex) {
    this.listIndex = listIndex;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }
}