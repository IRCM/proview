package ca.qc.ircm.proview.user;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * For forgotten password information.
 */
@Entity
@Table(name = "forgotpassword")
public class ForgotPassword implements Data, Serializable {
  private static final long serialVersionUID = -2805056622482303376L;
  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Moment where User requested a forgot password.
   */
  @Column(name = "requestMoment")
  private Instant requestMoment;
  /**
   * Confirm number for the forgot password request.
   */
  @Column(name = "confirmNumber")
  private int confirmNumber;
  /**
   * Forgot password request was used.
   */
  @Column(name = "used")
  private boolean used;
  /**
   * User that created this forgot password request.
   */
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ForgotPassword) {
      ForgotPassword other = (ForgotPassword) obj;
      boolean equals = confirmNumber == other.getConfirmNumber();
      equals &= requestMoment.equals(other.getRequestMoment());
      return equals;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return confirmNumber;
  }

  @Override
  public String toString() {
    return "ForgotPassword [id=" + id + "]";
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getRequestMoment() {
    return requestMoment;
  }

  public void setRequestMoment(Instant requestMoment) {
    this.requestMoment = requestMoment;
  }

  public int getConfirmNumber() {
    return confirmNumber;
  }

  public void setConfirmNumber(int confirmNumber) {
    this.confirmNumber = confirmNumber;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
