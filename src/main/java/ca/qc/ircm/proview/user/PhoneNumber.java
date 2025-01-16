package ca.qc.ircm.proview.user;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.springframework.lang.Nullable;

/**
 * Phone number.
 */
@Entity
@Table(name = PhoneNumber.TABLE_NAME)
@GeneratePropertyNames
public class PhoneNumber implements Serializable {
  public static final String TABLE_NAME = "phonenumber";
  private static final long serialVersionUID = 5548943595609304757L;
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Phone number type.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private PhoneNumberType type;
  /**
   * Phone number.
   */
  @Column(nullable = false)
  @Size(max = 50)
  private String number;
  /**
   * Extension.
   */
  @Column
  @Size(max = 20)
  private String extension;

  public PhoneNumber() {
  }

  public PhoneNumber(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "PhoneNumber [id=" + id + ", type=" + type + ", number=" + number + ", extension="
        + extension + "]";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PhoneNumberType getType() {
    return type;
  }

  public void setType(PhoneNumberType type) {
    this.type = type;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  @Nullable
  public String getExtension() {
    return extension;
  }

  public void setExtension(@Nullable String extension) {
    this.extension = extension;
  }
}
