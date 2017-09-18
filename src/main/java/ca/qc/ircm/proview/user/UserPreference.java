package ca.qc.ircm.proview.user;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A user's preference.
 */
@Entity
@Table(name = "userpreference")
public class UserPreference {
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * User.
   */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  /**
   * Preference.
   */
  @ManyToOne
  @JoinColumn(name = "preferenceId")
  private Preference preference;
  /**
   * Preference's value.
   */
  @Column(name = "value")
  private byte[] value;

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

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }
}
