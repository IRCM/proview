package ca.qc.ircm.proview.sample;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Internal standard in a Sample.
 */
@Entity
@Table(name = "standard")
public class Standard implements Data, Cloneable, Serializable, Named {

  /**
   * Quantity units.
   */
  public enum QuantityUnit {
    MICRO_GRAMS, PICO_MOL;
  }

  private static final long serialVersionUID = 1027734850465332430L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Standard name.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Quantity of Standard.
   */
  @Column(name = "quantity")
  @Size(max = 50)
  private String quantity;
  /**
   * Unit of Standard quantity.
   */
  @Column(name = "quantityUnit")
  @Enumerated(STRING)
  private QuantityUnit quantityUnit;
  /**
   * Comments about standard.
   */
  @Column(name = "comments")
  private String comments;
  /**
   * True if standard was deleted.
   */
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Standard) {
      Standard other = (Standard) obj;
      return this.name != null && this.name.equalsIgnoreCase(other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.name != null ? this.name.toUpperCase().hashCode() : 0;
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder("Standard(");
    buff.append(id);
    buff.append(",");
    buff.append(name);
    buff.append(")");
    return buff.toString();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public QuantityUnit getQuantityUnit() {
    return quantityUnit;
  }

  public void setQuantityUnit(QuantityUnit quantityUnit) {
    this.quantityUnit = quantityUnit;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
