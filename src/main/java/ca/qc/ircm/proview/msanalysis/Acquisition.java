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
import java.io.Serial;
import java.io.Serializable;
import org.springframework.lang.Nullable;

/**
 * Acquisition of a Sample in a MS analysis.
 */
@Entity
@Table(name = Acquisition.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = ENTITY_EI_EXPOSE_REP)
public class Acquisition implements Data, Named, Serializable {

  public static final String TABLE_NAME = "acquisition";
  @Serial
  private static final long serialVersionUID = 4253647399151347110L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
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
  @Column(nullable = false)
  private int numberOfAcquisition;
  /**
   * Name of Sample list being analysed.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String sampleListName;
  /**
   * File containing acquisition information.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String acquisitionFile;
  /**
   * Acquisition index number that is appended when showing LIMS.
   */
  @Column(nullable = false)
  private int position;
  /**
   * Any comment on this acquisition.
   */
  @Column
  @Size(max = 255)
  private String comment;

  public Acquisition() {
  }

  public Acquisition(long id) {
    this.id = id;
  }

  @Override
  public String getName() {
    String builder = getSample().getName() + ".A" + position;
    return builder;
  }

  @Override
  public String toString() {
    String buff = "Acquisition(" + id + ")";
    return buff;
  }

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getNumberOfAcquisition() {
    return numberOfAcquisition;
  }

  public void setNumberOfAcquisition(int numberOfAcquisition) {
    this.numberOfAcquisition = numberOfAcquisition;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
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

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  @Nullable
  public String getComment() {
    return comment;
  }

  public void setComment(@Nullable String comment) {
    this.comment = comment;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }
}
