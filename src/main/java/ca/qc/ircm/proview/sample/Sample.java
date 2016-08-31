package ca.qc.ircm.proview.sample;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A Sample.
 */
@Entity
@Table(name = "sample")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "sampleType")
public abstract class Sample implements Data, Named, Comparable<Sample>, Serializable {
  /**
   * Sample type.
   */
  public static enum Type {
    /**
     * Submission of sample to analyse.
     */
    SUBMISSION, /**
                 * Control.
                 */
    CONTROL
  }

  /**
   * Sample support.
   */
  public static enum Support {
    /**
     * Sample is dry.
     */
    DRY, /**
          * Sample is in solution.
          */
    SOLUTION, /**
               * Sample is in a Gel.
               */
    GEL
  }

  /**
   * Units for sample quantities.
   */
  public static enum QuantityUnit {
    MICRO_GRAMS, PICO_MOL;
  }

  private static final long serialVersionUID = -3637467720218236079L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Unique identifier given by LIMS.
   */
  @Column(name = "lims", unique = true, nullable = false)
  @Size(max = 100)
  private String lims;
  /**
   * Sample's name.
   */
  @Column(name = "name")
  @Size(max = 150)
  private String name;
  /**
   * Any comments on this sample.
   */
  @Column(name = "comments")
  private String comments;
  /**
   * Container where sample was originally located.
   */
  @ManyToOne
  @JoinColumn(name = "containerId")
  private SampleContainer originalContainer;
  /**
   * Standards that are in the sample.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "sampleId", updatable = false, nullable = false)
  private List<Standard> standards;

  public Sample() {
  }

  public Sample(Long id) {
    this.id = id;
  }

  public Sample(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Returns type of Sample.
   *
   * @return Sample type.
   */
  public abstract Type getType();

  /**
   * Returns sample's support.
   *
   * @return sample's support
   */
  public abstract Support getSupport();

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Sample) {
      Sample other = (Sample) obj;
      return lims != null && lims.equalsIgnoreCase(other.getLims());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lims == null ? 0 : lims.toUpperCase().hashCode();
  }

  @Override
  public String toString() {
    return "Sample [id=" + id + ", lims=" + lims + ", name=" + name + "]";
  }

  @Override
  public int compareTo(Sample other) {
    return lims.compareToIgnoreCase(other.getLims());
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getLims() {
    return lims;
  }

  public void setLims(String lims) {
    this.lims = lims;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String tag) {
    this.name = tag;
  }

  public SampleContainer getOriginalContainer() {
    return originalContainer;
  }

  public void setOriginalContainer(SampleContainer originalContainer) {
    this.originalContainer = originalContainer;
  }

  public List<Standard> getStandards() {
    return standards;
  }

  public void setStandards(List<Standard> standards) {
    this.standards = standards;
  }
}
