package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

/**
 * A sample that was fractionated.
 */
@Entity
@DiscriminatorValue("FRACTIONATION")
public class FractionationDetail extends TreatmentSample implements Named {
  /**
   * Sample's destination container.
   */
  @ManyToOne
  @JoinColumn(name = "destinationContainerId", nullable = false)
  private SampleContainer destinationContainer;
  /**
   * Fraction index number that is appended when showing LIMS number of fraction.
   */
  @Column(name = "position", nullable = false)
  private Integer position;
  /**
   * Fraction number. Used with {@link Fractionation.Type#MUDPIT}.
   */
  @Column(name = "number")
  private Integer number;
  /**
   * PI interval. Used with {@link Fractionation.Type#PI}
   */
  @Column(name = "piInterval")
  @Size(max = 255)
  private String piInterval;

  /**
   * Returns fraction's LIMS number.
   *
   * @return fraction's LIMS number
   */
  public String getLims() {
    if (getSample() != null && getSample().getLims() != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(getSample().getLims());
      builder.append(".F");
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
      builder.append(".F");
      builder.append(position);
      return builder.toString();
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("FractionationDetail(");
    buff.append(getId());
    buff.append(")");
    return buff.toString();
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

  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }
}
