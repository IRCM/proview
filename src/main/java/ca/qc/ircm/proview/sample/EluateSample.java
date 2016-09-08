package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Eluate submitted for proteomic analysis.
 */
@Entity
@DiscriminatorValue("SUBMISSION_ELUATE")
public class EluateSample extends ProteicSample implements Cloneable {
  private static final long serialVersionUID = -3036664998143484088L;

  /**
   * If sample is dry or in solution.
   */
  @Column(name = "support", nullable = false)
  @Enumerated(STRING)
  private Support support;
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
   * Contaminants that are in the same at submission.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "sampleId", updatable = false, nullable = false)
  private List<Contaminant> contaminants;

  public EluateSample() {
  }

  public EluateSample(Long id) {
    super(id);
  }

  public EluateSample(Long id, String name) {
    super(id, name);
  }

  @Override
  public Support getSupport() {
    return support;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public List<Contaminant> getContaminants() {
    return contaminants;
  }

  public void setContaminants(List<Contaminant> contaminants) {
    this.contaminants = contaminants;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public void setSupport(Support support) {
    this.support = support;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
