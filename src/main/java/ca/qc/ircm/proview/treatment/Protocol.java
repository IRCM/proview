package ca.qc.ircm.proview.treatment;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A Protocol.
 */
@Entity
@Table(name = "protocol")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Protocol implements Data, Serializable, Named, Comparable<Protocol> {
  /**
   * Protocol types.
   */
  public static enum Type {
    DIGESTION, ENRICHMENT;
  }

  private static final long serialVersionUID = -7624493017948317986L;

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Name of the Protocol.
   */
  @Column(name = "name", unique = true, nullable = false)
  @Size(max = 100)
  private String name;

  public Protocol() {
  }

  public Protocol(Long id) {
    this.id = id;
  }

  public Protocol(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Type of Protocol.
   *
   * @return type of protocol
   */
  public abstract Type getType();

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Protocol) {
      Protocol other = (Protocol) obj;
      return this.name != null && this.name.equalsIgnoreCase(other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.name == null ? 0 : this.name.toUpperCase().hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder(getClass().getSimpleName());
    buff.append("(");
    buff.append(id);
    buff.append(",");
    buff.append(name);
    buff.append(",");
    buff.append(getType());
    buff.append(")");
    return buff.toString();
  }

  @Override
  public int compareTo(Protocol other) {
    return name.compareToIgnoreCase(other.getName());
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
