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
import java.util.Optional;

/**
 * Filters users.
 */
public class UserWebFilter implements SerializablePredicate<User> {
  private static final long serialVersionUID = -370169011685482240L;
  public Optional<String> emailContains = Optional.empty();
  public Optional<String> nameContains = Optional.empty();
  public Optional<String> laboratoryNameContains = Optional.empty();
  public Optional<String> organizationContains = Optional.empty();
  public Optional<Boolean> active = Optional.empty();
  private final Locale locale;

  public UserWebFilter(Locale locale) {
    this.locale = locale;
  }

  @Override
  public boolean test(User user) {
    boolean value = true;
    if (emailContains.isPresent()) {
      value &=
          user.getEmail().toLowerCase(locale).contains(emailContains.get().toLowerCase(locale));
    }
    if (nameContains.isPresent()) {
      value &= user.getName().toLowerCase(locale).contains(nameContains.get().toLowerCase(locale));
    }
    if (laboratoryNameContains.isPresent()) {
      value &= user.getLaboratory().getName().toLowerCase(locale)
          .contains(laboratoryNameContains.get().toLowerCase(locale));
    }
    if (organizationContains.isPresent()) {
      value &= user.getLaboratory().getOrganization().toLowerCase(locale)
          .contains(organizationContains.get().toLowerCase(locale));
    }
    if (active.isPresent()) {
      value &= user.isActive() == active.get();
    }
    return value;
  }
}
