package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;
import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.lang.Nullable;

/**
 * User of this program.
 */
@Entity
@Table(name = User.TABLE_NAME)
@GeneratePropertyNames
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = ENTITY_EI_EXPOSE_REP)
public class User implements Data, Named, Serializable {

  public static final String TABLE_NAME = "users";
  public static final String LOCALE_PREFERENCE = "locale";
  public static final long ROBOT_ID = 1L;
  @Serial
  private static final long serialVersionUID = 4251923438573972499L;

  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Email of User. This is also a unique id.
   */
  @Column(unique = true, nullable = false)
  @Size(max = 255)
  private String email;
  /**
   * First name.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String name;
  /**
   * True if User is active. An active user can log into program.
   */
  @Column(nullable = false)
  private boolean active = false;
  /**
   * True if User is an admin.
   */
  @Column(nullable = false)
  private boolean admin = false;
  /**
   * True if User is a manager.
   */
  @Column(nullable = false)
  private boolean manager = false;
  /**
   * Hashed password.
   */
  @Column
  @Size(max = 255)
  private String hashedPassword;
  /**
   * Password's salt.
   */
  @Column
  @Size(max = 255)
  private String salt;
  /**
   * Password's version.
   */
  @Column
  private Integer passwordVersion;
  /**
   * Number of sign attempts since last successful sign.
   */
  @Column(nullable = false)
  private int signAttempts;
  /**
   * Last sign attempts (success or fail).
   */
  @Column
  private LocalDateTime lastSignAttempt;
  /**
   * Register time.
   */
  @Column(nullable = false)
  private LocalDateTime registerTime;
  /**
   * User's prefered locale.
   */
  @Column
  private Locale locale;
  /**
   * User's laboratory.
   */
  @ManyToOne(optional = false)
  @JoinColumn
  private Laboratory laboratory;
  /**
   * Address.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn
  private Address address;
  /**
   * Phone numbers.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn
  private List<PhoneNumber> phoneNumbers;

  public User() {
  }

  public User(long id) {
    this.id = id;
  }

  public User(String email) {
    this.email = email;
  }

  public User(long id, String email) {
    this.id = id;
    this.email = email;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", email=" + email + "]";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Laboratory getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
  }

  @Nullable
  public String getHashedPassword() {
    return hashedPassword;
  }

  public void setHashedPassword(@Nullable String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }

  @Nullable
  public String getSalt() {
    return salt;
  }

  public void setSalt(@Nullable String salt) {
    this.salt = salt;
  }

  @Nullable
  public Integer getPasswordVersion() {
    return passwordVersion;
  }

  public void setPasswordVersion(@Nullable Integer passwordVersion) {
    this.passwordVersion = passwordVersion;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  @Nullable
  public Address getAddress() {
    return address;
  }

  public void setAddress(@Nullable Address address) {
    this.address = address;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  @Nullable
  public Locale getLocale() {
    return locale;
  }

  public void setLocale(@Nullable Locale locale) {
    this.locale = locale;
  }

  public int getSignAttempts() {
    return signAttempts;
  }

  public void setSignAttempts(int signAttempts) {
    this.signAttempts = signAttempts;
  }

  @Nullable
  public LocalDateTime getLastSignAttempt() {
    return lastSignAttempt;
  }

  public void setLastSignAttempt(@Nullable LocalDateTime lastSignAttempt) {
    this.lastSignAttempt = lastSignAttempt;
  }

  public LocalDateTime getRegisterTime() {
    return registerTime;
  }

  public void setRegisterTime(LocalDateTime registerTime) {
    this.registerTime = registerTime;
  }

  public boolean isManager() {
    return manager;
  }

  public void setManager(boolean manager) {
    this.manager = manager;
  }
}
