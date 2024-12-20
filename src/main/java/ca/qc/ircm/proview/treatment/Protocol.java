package ca.qc.ircm.proview.treatment;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.DataNullableId;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Protocol.
 */
@Entity
@Table(name = Protocol.TABLE_NAME)
@GeneratePropertyNames
public class Protocol implements DataNullableId, Serializable, Named {
  /**
   * Protocol types.
   */
  public static enum Type {
    DIGESTION, ENRICHMENT;
  }

  public static final String TABLE_NAME = "protocol";
  private static final long serialVersionUID = -7624493017948317986L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Name of the Protocol.
   */
  @Column(unique = true, nullable = false)
  @Size(max = 100)
  private String name;
  /**
   * Protocol type.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private Type type;

  public Protocol() {
  }

  public Protocol(Long id) {
    this.id = id;
  }

  public Protocol(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return "Protocol [id=" + id + ", name=" + name + "]";
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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
