package ca.qc.ircm.proview.fractionation;

import static javax.persistence.EnumType.STRING;

import ca.qc.ircm.proview.treatment.Treatment;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

/**
 * Fractionation of sample.
 */
@Entity
@DiscriminatorValue("FRACTIONATION")
public class Fractionation extends Treatment<FractionationDetail> {
  /**
   * Method used to split sample into fractions.
   */
  public static enum FractionationType {
    /**
     * MudPit fraction.
     */
    MUDPIT, /**
             * Split by pI.
             */
    PI;
  }

  /**
   * How samples where split.
   */
  @Column(name = "fractionationType", nullable = false)
  @Enumerated(STRING)
  private FractionationType fractionationType;

  public Fractionation() {
    super();
  }

  public Fractionation(Long id) {
    super(id);
  }

  @Override
  public Type getType() {
    return Type.FRACTIONATION;
  }

  public FractionationType getFractionationType() {
    return fractionationType;
  }

  public void setFractionationType(FractionationType fractionationType) {
    this.fractionationType = fractionationType;
  }
}
