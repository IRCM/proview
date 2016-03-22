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
   * Returns urlEnd with prefix that allows to access application from anywhere.
   * <p>
   * For example, to obtain the full URL <code>http://myserver.com/proview/myurl?param1=abc</code>,
   * the urlEnd parameter should be <code>/proview/myurl?param1=abc</code>
   * </p>
   *
   * @param urlEnd
   *          end portion of URL
   * @return urlEnd with prefix that allows to access application from anywhere
   */
  public String getUrl(String urlEnd);

  /**
   * Returns realm name for Shiro.
   *
   * @return realm name for Shiro
   */
  public String getRealmName();

  /**
   * Returns cipher key for Shiro.
   *
   * @return cipher key for Shiro
   */
  public String getCipherKey();

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

  /**
   * Returns true if emails are enabled.
   *
   * @return true if emails are enabled
   */
  public boolean isEmailEnabled();

  /**
   * Returns email server.
   *
   * @return email server
   */
  public String getEmailServer();

  /**
   * Returns email sender.
   *
   * @return email sender
   */
  public String getEmailSender();

  /**
   * Returns email receiver for errors.
   *
   * @return email receiver for errors
   */
  public String getEmailErrorReceiver();

  /**
   * Returns default address.
   *
   * @return default address
   */
  public String getAddress();

  /**
   * Returns default town.
   *
   * @return default town
   */
  public String getTown();

  /**
   * Returns default state.
   *
   * @return default state
   */
  public String getState();

  /**
   * Returns default postal code.
   *
   * @return default postal code
   */
  public String getPostalCode();

  /**
   * Returns countries.
   *
   * @return countries
   */
  public String[] getCountries();
}
