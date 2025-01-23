package ca.qc.ircm.proview.user;

import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * Laboratory where a user works.
 */
@Entity
@Table(name = Laboratory.TABLE_NAME)
@GeneratePropertyNames
public class Laboratory implements Data, Named, Serializable {
  public static final String TABLE_NAME = "laboratory";
  @Serial
  private static final long serialVersionUID = 8294913257061846746L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Name.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String name;
  /**
   * Director.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String director;

  public Laboratory() {
  }

  public Laboratory(Long id) {
    this.id = id;
  }

  public Laboratory(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Laboratory [id=" + id + ", name=" + name + "]";
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

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }
}
