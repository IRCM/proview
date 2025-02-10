package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.UsedBy.HIBERNATE;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.UsedBy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * User's address.
 */
@Entity
@Table(name = Address.TABLE_NAME)
@GeneratePropertyNames
public class Address implements Serializable {

  public static final String TABLE_NAME = "address";
  @Serial
  private static final long serialVersionUID = 6036866850504348215L;
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Address line.
   */
  @Column(nullable = false)
  @Size(max = 200)
  private String line;
  /**
   * Town.
   */
  @Column(nullable = false)
  @Size(max = 50)
  private String town;
  /**
   * State / province.
   */
  @Column(nullable = false)
  @Size(max = 50)
  private String state;
  /**
   * Country.
   */
  @Column(nullable = false)
  @Size(max = 50)
  private String country;
  /**
   * Postal code.
   */
  @Column(nullable = false)
  @Size(max = 50)
  private String postalCode;

  @Override
  public String toString() {
    return "Address [id=" + id + ", address=" + line + "]";
  }

  public Long getId() {
    return id;
  }

  @UsedBy(HIBERNATE)
  public void setId(Long id) {
    this.id = id;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }
}
