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

package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import com.vaadin.server.SerializablePredicate;

import java.util.Locale;

/**
 * Filters users.
 */
public class UserWebFilter implements SerializablePredicate<User> {
  private static final long serialVersionUID = -370169011685482240L;
  private String emailContains;
  private String nameContains;
  private String laboratoryNameContains;
  private String organizationContains;
  private Boolean active;
  private Locale locale;

  public UserWebFilter(Locale locale) {
    this.locale = locale;
  }

  @Override
  public boolean test(User user) {
    boolean value = true;
    if (emailContains != null) {
      value &= user.getEmail().toLowerCase(locale).contains(emailContains.toLowerCase(locale));
    }
    if (nameContains != null) {
      value &= user.getName().toLowerCase(locale).contains(nameContains.toLowerCase(locale));
    }
    if (laboratoryNameContains != null) {
      value &= user.getLaboratory().getName().toLowerCase(locale)
          .contains(laboratoryNameContains.toLowerCase(locale));
    }
    if (organizationContains != null) {
      value &= user.getLaboratory().getOrganization().toLowerCase(locale)
          .contains(organizationContains.toLowerCase(locale));
    }
    if (active != null) {
      value &= user.isActive() == active;
    }
    return value;
  }

  public String getEmailContains() {
    return emailContains;
  }

  public void setEmailContains(String emailContains) {
    this.emailContains = emailContains;
  }

  public String getNameContains() {
    return nameContains;
  }

  public void setNameContains(String nameContains) {
    this.nameContains = nameContains;
  }

  public String getLaboratoryNameContains() {
    return laboratoryNameContains;
  }

  public void setLaboratoryNameContains(String laboratoryNameContains) {
    this.laboratoryNameContains = laboratoryNameContains;
  }

  public String getOrganizationContains() {
    return organizationContains;
  }

  public void setOrganizationContains(String organizationContains) {
    this.organizationContains = organizationContains;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
