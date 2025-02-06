package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.lang.Nullable;

/**
 * Treatment information that is specific to a sample.
 */
@Entity
@Table(name = TreatedSample.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = ENTITY_EI_EXPOSE_REP)
public class TreatedSample implements Data, Serializable {

  public static final String TABLE_NAME = "treatedsample";
  @Serial
  private static final long serialVersionUID = -1654046284723997439L;
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Sample that received treatment.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private Sample sample;
  /**
   * Sample's container.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private SampleContainer container;
  /**
   * Sample's destination container, if any.
   */
  @ManyToOne
  @JoinColumn
  private SampleContainer destinationContainer;
  /**
   * Volume of source transfered.
   */
  @Column
  @Min(0)
  private Double sourceVolume;
  /**
   * Solvent used for dilution.
   */
  @Column
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column
  @Min(0)
  private Double solventVolume;
  /**
   * Name of standard added.
   */
  @Column
  @Size(max = 100)
  private String name;
  /**
   * Quantity of standard added.
   */
  @Column
  @Size(max = 100)
  private String quantity;
  /**
   * Fraction index number that is appended when showing LIMS number of treatedSample.
   */
  @Column
  private Integer position;
  /**
   * Fraction number. Used with {@link FractionationType#MUDPIT}.
   */
  @Column
  private Integer number;
  /**
   * PI interval. Used with {@link FractionationType#PI}
   */
  @Column
  @Size(max = 255)
  private String piInterval;
  /**
   * Comment about sample's treatment.
   */
  @Column
  private String comment;

  /**
   * Returns fration's name based on sample's name.
   *
   * @return fration's name based on sample's name
   */
  public String getFractionName() {
    String builder = getSample().getName() + ".F" + position;
    return builder;
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

  @Nullable
  public String getComment() {
    return comment;
  }

  public void setComment(@Nullable String comment) {
    this.comment = comment;
  }

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public SampleContainer getContainer() {
    return container;
  }

  public void setContainer(SampleContainer container) {
    this.container = container;
  }

  @Nullable
  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(@Nullable SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }

  @Nullable
  public Double getSourceVolume() {
    return sourceVolume;
  }

  public void setSourceVolume(@Nullable Double sourceVolume) {
    this.sourceVolume = sourceVolume;
  }

  @Nullable
  public String getSolvent() {
    return solvent;
  }

  public void setSolvent(@Nullable String solvent) {
    this.solvent = solvent;
  }

  @Nullable
  public Double getSolventVolume() {
    return solventVolume;
  }

  public void setSolventVolume(@Nullable Double solventVolume) {
    this.solventVolume = solventVolume;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(@Nullable String quantity) {
    this.quantity = quantity;
  }

  @Nullable
  public Integer getPosition() {
    return position;
  }

  public void setPosition(@Nullable Integer position) {
    this.position = position;
  }

  @Nullable
  public Integer getNumber() {
    return number;
  }

  public void setNumber(@Nullable Integer number) {
    this.number = number;
  }

  @Nullable
  public String getPiInterval() {
    return piInterval;
  }

  public void setPiInterval(@Nullable String piInterval) {
    this.piInterval = piInterval;
  }
}
