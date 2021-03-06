/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user;

import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name = ForgotPassword.TABLE_NAME)
@GeneratePropertyNames
public class ForgotPassword implements Data, Serializable {
  public static final String TABLE_NAME = "forgotpassword";
  private static final long serialVersionUID = -2805056622482303376L;
  /**
   * Database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Moment where User requested a forgot password.
   */
  @Column
  private LocalDateTime requestMoment;
  /**
   * Confirm number for the forgot password request.
   */
  @Column
  private String confirmNumber;
  /**
   * Forgot password request was used.
   */
  @Column
  private boolean used;
  /**
   * User that created this forgot password request.
   */
  @ManyToOne
  @JoinColumn
  private User user;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((confirmNumber == null) ? 0 : confirmNumber.hashCode());
    result = prime * result + ((requestMoment == null) ? 0 : requestMoment.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ForgotPassword other = (ForgotPassword) obj;
    if (confirmNumber == null) {
      if (other.confirmNumber != null) {
        return false;
      }
    } else if (!confirmNumber.equals(other.confirmNumber)) {
      return false;
    }
    if (requestMoment == null) {
      if (other.requestMoment != null) {
        return false;
      }
    } else if (!requestMoment.equals(other.requestMoment)) {
      return false;
    }
    return true;
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

  public LocalDateTime getRequestMoment() {
    return requestMoment;
  }

  public void setRequestMoment(LocalDateTime requestMoment) {
    this.requestMoment = requestMoment;
  }

  public String getConfirmNumber() {
    return confirmNumber;
  }

  public void setConfirmNumber(String confirmNumber) {
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
