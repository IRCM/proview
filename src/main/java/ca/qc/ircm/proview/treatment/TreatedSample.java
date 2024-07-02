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
import java.io.Serializable;

/**
 * Treatment information that is specific to a sample.
 */
@Entity
@Table(name = TreatedSample.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class TreatedSample implements Data, Serializable {
  public static final String TABLE_NAME = "treatedsample";
  private static final long serialVersionUID = -1654046284723997439L;
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Treatment.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private Treatment treatment;
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
  @Column(nullable = false)
  @Min(0)
  private Double sourceVolume;
  /**
   * Solvent used for dilution.
   */
  @Column(nullable = false)
  private String solvent;
  /**
   * Volume of solvent used.
   */
  @Column(nullable = false)
  @Min(0)
  private Double solventVolume;
  /**
   * Name of standard added.
   */
  @Column(nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Quantity of standard added.
   */
  @Column(nullable = false)
  @Size(max = 100)
  private String quantity;
  /**
   * Fraction index number that is appended when showing LIMS number of treatedSample.
   */
  @Column(nullable = false)
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
