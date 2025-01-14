package ca.qc.ircm.proview.user;

import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A user's preference.
 */
@Entity
@Table(name = "userpreference")
@GeneratePropertyNames
class UserPreference {
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * User.
   */
  @ManyToOne(optional = false)
  @JoinColumn
  private User user;
  /**
   * Preference.
   */
  @ManyToOne(optional = false)
  @JoinColumn
  private Preference preference;
  /**
   * Preference's value.
   */
  @Column(nullable = false)
  private byte[] content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Preference getPreference() {
    return preference;
  }

  public void setPreference(Preference preference) {
    this.preference = preference;
  }

  public byte[] getContent() {
    return content.clone();
  }

  public void setContent(byte[] content) {
    this.content = content.clone();
  }
}
