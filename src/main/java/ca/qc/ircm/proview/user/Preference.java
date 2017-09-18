package ca.qc.ircm.proview.user;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A preference that can be set for user.
 */
@Entity
@Table(name = "preference")
public class Preference {
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Object getting and setting the preference.
   */
  @Column(name = "referer", nullable = false)
  @Size(max = 255)
  private String referer;
  /**
   * Name of the preference.
   */
  @Column(name = "name", nullable = false)
  @Size(max = 255)
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReferer() {
    return referer;
  }

  public void setReferer(String referer) {
    this.referer = referer;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
