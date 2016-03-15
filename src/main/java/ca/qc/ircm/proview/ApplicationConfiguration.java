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

package ca.qc.ircm.proview;

import ca.qc.ircm.proview.security.PasswordVersion;

import java.nio.file.Path;
import java.util.List;

/**
 * Application's properties.
 */
public interface ApplicationConfiguration {
  public Path getLogFile();

  /**
   * Returns realm name for Shiro.
   *
   * @return realm name for Shiro
   */
  public String getRealmName();

  /**
   * Returns password version to use to encode new passwords.
   * <p>
   * The newest password version is the one to use to encode new passwords.
   * </p>
   *
   * @return password version to use to encode new passwords
   */
  public PasswordVersion getPasswordVersion();

  /**
   * Returns all available password versions.
   * <p>
   * Password versions are sorted from most recent to oldest.
   * </p>
   *
   * @return all available password versions
   */
  public List<PasswordVersion> getPasswordVersions();
}
