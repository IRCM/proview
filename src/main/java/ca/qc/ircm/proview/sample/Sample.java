package ca.qc.ircm.proview.sample;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.springframework.lang.Nullable;

/**
 * Sample.
 */
@Entity
@Table(name = Sample.TABLE_NAME)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "category")
@GeneratePropertyNames
public abstract class Sample implements Data, Named, Serializable {
  /**
   * Sample category.
   */
  public static enum Category {
    /**
     * Submission of sample to analyse.
     */
    SUBMISSION,
    /**
     * Control.
     */
    CONTROL
  }

  public static final String TABLE_NAME = "sample";
  private static final long serialVersionUID = -3637467720218236079L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Version number.
   */
  @Version
  @Column(nullable = false)
  private int version;
  /**
   * Sample's name.
   */
  @Column(nullable = false)
  @Size(min = 2, max = 150)
  private String name;
  /**
   * Type of sample.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private SampleType type;
  /**
   * Volume of Sample (generally in ul).
   */
  @Column
  @Size(max = 100)
  private String volume;
  /**
   * Quantity of Sample (generally in ug or pmol).
   */
  @Column
  @Size(max = 100)
  private String quantity;

  public Sample() {
  }

  public Sample(long id) {
    this.id = id;
  }

  public Sample(long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Returns sample's category.
   *
   * @return sample's category
   */
  public abstract Category getCategory();

  @Override
  public String toString() {
    return "Sample [id=" + id + ", name=" + name + "]";
  }

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
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
  public String getVolume() {
    return volume;
  }

  public void setVolume(@Nullable String volume) {
    this.volume = volume;
  }

  public SampleType getType() {
    return type;
  }

  public void setType(SampleType type) {
    this.type = type;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
