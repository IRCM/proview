package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

/**
 * Gel sample submitted for proteomic analysis.
 */
@Entity
@DiscriminatorValue("SUBMISSION_GEL")
public class GelSample extends ProteicSample implements Cloneable {
  /**
   * Gel separation.
   */
  public static enum Separation {
    ONE_DIMENSION, TWO_DIMENSION;
  }

  /**
   * Gel thickness.
   */
  public static enum Thickness {
    ONE, ONE_HALF, TWO;
  }

  /**
   * Gel thickness.
   */
  public static enum Coloration {
    COOMASSIE, SYPRO, SILVER, SILVER_INVITROGEN, OTHER;
  }

  private static final long serialVersionUID = -8931210404937061204L;

  /**
   * Gel separation.
   */
  @Column(name = "separation")
  @Enumerated(STRING)
  private Separation separation;
  /**
   * Gel thickness.
   */
  @Column(name = "thickness")
  @Enumerated(STRING)
  private Thickness thickness;
  /**
   * Gel coloration.
   */
  @Column(name = "coloration")
  @Enumerated(STRING)
  private Coloration coloration;
  /**
   * Other gel coloration (if any).
   */
  @Column(name = "otherColoration")
  @Size(max = 100)
  private String otherColoration;
  /**
   * Gel development time (for coloration).
   */
  @Column(name = "developmentTime")
  @Size(max = 100)
  private String developmentTime;
  /**
   * Gel decoloration.
   */
  @Column(name = "decoloration", nullable = false)
  private boolean decoloration = false;
  /**
   * Quantity of weight marker.
   */
  @Column(name = "weightMarkerQuantity")
  private Double weightMarkerQuantity;
  /**
   * Quantity of proteins in gel (total).
   */
  @Column(name = "proteinQuantity")
  @Size(max = 100)
  private String proteinQuantity;

  public GelSample() {
  }

  public GelSample(Long id) {
    super(id);
  }

  public GelSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public Support getSupport() {
    return Support.GEL;
  }

  @Override
  public Type getType() {
    return Type.SUBMISSION;
  }

  public String getProteinQuantity() {
    return proteinQuantity;
  }

  public void setProteinQuantity(String proteinQuantity) {
    this.proteinQuantity = proteinQuantity;
  }

  public Separation getSeparation() {
    return separation;
  }

  public void setSeparation(Separation separation) {
    this.separation = separation;
  }

  public Thickness getThickness() {
    return thickness;
  }

  public void setThickness(Thickness thickness) {
    this.thickness = thickness;
  }

  public Coloration getColoration() {
    return coloration;
  }

  public void setColoration(Coloration coloration) {
    this.coloration = coloration;
  }

  public String getOtherColoration() {
    return otherColoration;
  }

  public void setOtherColoration(String otherColoration) {
    this.otherColoration = otherColoration;
  }

  public boolean isDecoloration() {
    return decoloration;
  }

  public void setDecoloration(boolean decoloration) {
    this.decoloration = decoloration;
  }

  public Double getWeightMarkerQuantity() {
    return weightMarkerQuantity;
  }

  public void setWeightMarkerQuantity(Double weightMarkerQuantity) {
    this.weightMarkerQuantity = weightMarkerQuantity;
  }

  public String getDevelopmentTime() {
    return developmentTime;
  }

  public void setDevelopmentTime(String developmentTime) {
    this.developmentTime = developmentTime;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
